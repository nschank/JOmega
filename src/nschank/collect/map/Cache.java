package nschank.collect.map;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import nschank.collect.fxn.FunctionalCollection;
import nschank.util.NSets;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by Nicolas Schank for package nschank.collect.map
 *
 * Created on 25 Feb 2014
 * Last updated on 26 Feb 2014
 *
 * A Cache of objects holds on to a certain number of them, and is guaranteed to have O(1) access, read, and write
 * times.
 * Equality is not overridden, because a Cache should almost never equal another Cache.
 *
 * @author nschank, Brown University
 * @version 2.1
 * @since 25 Feb 2014
 */
public class Cache<T, S> implements Map<T, S>
{
	/**
	 * The number of elements to cache if the size is not set, either in the constructor or with the setMaxSize method.
	 */
	public static final int DEFAULT_MAX_SIZE = 100;
	/**
	 * A map of elements currently in the collection to a CacheNode, which allows the HashTable to act like a Queue,
	 * remembering only the most recently added or accessed elements.
	 */
	private final Map<T, _CacheNode<T, S>> cache;
	/**
	 * The element currently at the beginning of the queue (so most recently added/accessed)
	 */
	private Optional<T> first;
	/**
	 * The element that is currently at the end of the queue (so the first in line to be deleted).
	 */
	private Optional<T> last;
	/**
	 * The size of the queue that
	 */
	private int maxSize;

	/**
	 * Creates a new Cache with a maximum size given by DEFAULT_MAX_SIZE
	 */
	public Cache()
	{
		this(DEFAULT_MAX_SIZE);
	}

	/**
	 * Creates a new Cache with a given maximum size.
	 *
	 * @param maxSize
	 * 		The maximum size
	 */
	public Cache(int maxSize)
	{
		this.maxSize = maxSize;
		this.first = Optional.absent();
		this.last = Optional.absent();
		this.cache = new HashMap<>();
	}

	/**
	 * Removes the final element iff the size of this Cache has gone past the maximum
	 */
	private void checkSize()
	{
		if(this.cache.size() > this.maxSize) this.remove(this.last.get());
	}

	/**
	 * Empties this cache completely.
	 */
	@Override
	public void clear()
	{
		this.cache.clear();
		this.first = Optional.absent();
		this.last = Optional.absent();
	}

	/**
	 * Connects two nodes in order. Deals with the possibility of empty nodes, including setting first and last to
	 * be absent if both nodes are empty.
	 *
	 * @param prev
	 * 		A node in the list.
	 * @param next
	 * 		Another node in the list.
	 */
	private void connect(final Optional<_CacheNode<T, S>> prev, final Optional<_CacheNode<T, S>> next)
	{

		if(prev.isPresent()) prev.get().next = next;
		if(next.isPresent()) next.get().prev = prev;

		if(!prev.isPresent() && !next.isPresent())
		{
			this.first = Optional.absent();
			this.last = Optional.absent();
		} else if(!prev.isPresent()) this.first = Optional.of(next.get().key);
		else if(!next.isPresent()) this.last = Optional.of(prev.get().key);
	}

	/**
	 * Checks whether the key is contained in the Cache. Does not alter the ordering of the Cache. Runs in O(1) time.
	 *
	 * @param key
	 * 		An object to look for in the Cache, as a key
	 *
	 * @return Whether or not the given object is, in fact, a key in this Cache
	 */
	@Override
	public boolean containsKey(final Object key)
	{
		return this.cache.containsKey(key);
	}

	/**
	 * Checks whether the given value is in the Cache. Runs in O(n) time.
	 *
	 * @param value
	 * 		An object to look for in the Cache, as a value.
	 *
	 * @return Whether the Object is in the Cache
	 */
	@Override
	public boolean containsValue(final Object value)
	{
		if(!this.first.isPresent()) return false;
		_CacheNode<T, S> view = this.cache.get(this.first.get());
		if(view.node.equals(value)) return true;
		while(view.next.isPresent())
		{
			view = view.next.get();
			if(view.node.equals(value)) return true;
		}
		return false;
	}

	/**
	 * @return The set of all Entries in this Cache
	 */
	@Override
	public Set<Entry<T, S>> entrySet()
	{
		return NSets.map(this.cache.entrySet(), new Function<Entry<T, _CacheNode<T, S>>, Entry<T, S>>()
		{
			@Override
			public Entry<T, S> apply(final Entry<T, _CacheNode<T, S>> t_cacheNodeEntry)
			{
				return new Entry<T, S>()
				{
					@Override
					public T getKey()
					{
						return t_cacheNodeEntry.getKey();
					}

					@Override
					public S getValue()
					{
						return t_cacheNodeEntry.getValue().node;
					}

					@Override
					public S setValue(final S value)
					{
						return null;
					}
				};
			}
		});
	}

	/**
	 * Gets an object from the Cache that is attached to this key. Returns null if no such object exists. If the object
	 * does exist, it is moved to the first position of the queue and will now be deleted last.
	 *
	 * @param key
	 * 		An object to look for in the Cache, as a key
	 *
	 * @return The Object the given key is attached to, or null
	 */
	@Override
	public S get(final Object key)
	{
		if(this.cache.containsKey(key))
		{
			_CacheNode<T, S> node = this.cache.get(key);
			this.connect(node.prev, node.next);
			this.toFront(node);
			return node.node;
		} else return null;
	}

	/**
	 * @return Whether the cache is empty
	 */
	@Override
	public boolean isEmpty()
	{
		return this.cache.isEmpty();
	}

	/**
	 * The set of all keys in this cache.
	 *
	 * @return The set of all keys in this Cache.
	 */
	@Override
	public Set<T> keySet()
	{
		return this.cache.keySet();
	}

	/**
	 * Puts an object into the Cache in the top position of the queue. If the Cache is now larger than the given
	 * maximum size, the least-recently accessed element of the Cache is deleted.
	 *
	 * @param key
	 * 		An object to look for in the Cache, as a key
	 * @param value
	 * 		The value to attach to the key
	 *
	 * @return The old value attached to that key, or null if the key was not in the Cache.
	 */
	@Override
	public S put(final T key, final S value)
	{
		S oldValue = get(key);
		if(oldValue == null)
		{
			_CacheNode<T, S> node = new _CacheNode<T, S>(key, value, Optional.<_CacheNode<T, S>>absent(), Optional.<_CacheNode<T, S>>absent());
			this.cache.put(key, node);
			this.toFront(node);
			this.checkSize();
			return null;
		} else
		{
			_CacheNode<T, S> node = this.cache.get(key);
			this.connect(node.prev, node.next);
			this.toFront(node);
			final S old = node.node;
			node.node = value;
			return old;
		}
	}

	/**
	 * @param m
	 * 		Puts the entire given map into the Cache, one by one
	 */
	@Override
	public void putAll(final Map<? extends T, ? extends S> m)
	{
		for(Entry<? extends T, ? extends S> entry : m.entrySet())
			this.put(entry.getKey(), entry.getValue());
	}

	/**
	 * Removes the element associated with this key from the Cache.
	 *
	 * @param key
	 * 		The key to dissociate from this Cache
	 *
	 * @return The element that was connected to the key
	 */
	@Override
	public S remove(final Object key)
	{
		if(this.cache.containsKey(key))
		{
			_CacheNode<T, S> node = this.cache.get(key);
			this.connect(node.prev, node.next);
			this.cache.remove(key);
			return node.node;
		} else return null;
	}

	/**
	 * @return The number of elements currently in this Cache.
	 */
	@Override
	public int size()
	{
		return this.cache.size();
	}

	/**
	 * Puts a given node to the front of the Cache
	 *
	 * @param node
	 * 		A node that has been most recently added or accessed
	 */
	private void toFront(final _CacheNode<T, S> node)
	{
		if(this.first.isPresent()) this.connect(Optional.of(node), Optional.of(this.cache.get(this.first.get())));
		else
		{
			this.first = Optional.of(node.key);
			this.last = Optional.of(node.key);
		}
	}

	/**
	 * @return A String representing this Cache
	 */
	@Override
	public String toString()
	{
		return new StringBuilder("Cache of ").append(this.getClass().getTypeParameters()[0].getName()).append(" of size ").append(this.cache.size()).append(" with a maximum size of ").append(this.maxSize).append('.').toString();
	}

	/**
	 * @return An unmodifiable collection of the values currently in the Cache
	 */
	@Override
	public Collection<S> values()
	{
		return new FunctionalCollection<_CacheNode<T, S>, S>(this.cache.values(), new Function<_CacheNode<T, S>, S>()
		{
			@Override
			public S apply(final _CacheNode<T, S> s_cacheNode)
			{
				return s_cacheNode.node;
			}
		});
	}

	/**
	 * A representation of the values being Cached that are attached to the next and previous values.
	 *
	 * @param <T>
	 */
	private class _CacheNode<T, S>
	{
		T key;
		Optional<_CacheNode<T, S>> next, prev;
		S node;

		_CacheNode(T key, S node, Optional<_CacheNode<T, S>> next, Optional<_CacheNode<T, S>> prev)
		{
			this.node = node;
			this.key = key;
			this.next = next;
			this.prev = prev;
		}
	}
}
