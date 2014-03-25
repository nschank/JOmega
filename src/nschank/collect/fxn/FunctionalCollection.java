package nschank.collect.fxn;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import nschank.note.Immutable;

import java.util.*;


/**
 * Created by Nicolas Schank for package nschank.collect.fxn
 * Created on 26 Feb 2014
 * Last updated on 25 Mar 2014
 *
 * A Collection that forwards to an internal collection, lazily performing a function on that input whenever an
 * element is accessed. Optimizes itself by remembering the entire outputted Collection if the Collection is ever
 * cycled through completely.
 *
 * Behaviour is undefined if the internal Collection changes during usage.
 *
 * @author nschank, Brown University
 * @version 1.1.5
 * @since 26 Feb 2014
 */
@Immutable
public class FunctionalCollection<T, S> implements Collection<S>
{
	/**
	 * Allows us to keep the entire Collection if it was already converted
	 */
	private Optional<List<S>> convertedCollection;
	/**
	 * A function to perform on the internal collection
	 */
	private final Function<T, S> function;
	/**
	 * An internal collection that the function will be called upon
	 */
	private final Collection<T> internalCollection;
	/**
	 * An internal set of any previously calculated values.
	 */
	private Set<S> internalSet;

	/**
	 * Wraps a given Collection; will call upon the given Function when calculating actual values
	 */
	public FunctionalCollection(final Collection<T> coll, final Function<T, S> func)
	{
		this.internalCollection = coll;
		this.function = func;
		this.convertedCollection = Optional.absent();
		this.internalSet = new HashSet<>();
	}

	/**
	 * Does nothing and returns false.
	 *
	 * @param s
	 * 		Unused
	 *
	 * @return False
	 */
	@Override
	public boolean add(final S s)
	{
		return false;
	}

	/**
	 * Does nothing and returns false.
	 *
	 * @param c
	 * 		Unused
	 *
	 * @return False
	 */
	@Override
	public boolean addAll(final Collection<? extends S> c)
	{
		return false;
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void clear()
	{

	}

	/**
	 * Checks whether this Object is in the set.
	 * Necessarily runs in O(n) time worst case where n is the size of the internal Collection, because the Collection
	 * has to be entirely converted to check for membership. In order to get O(1) time for checking membership,
	 * use an {@link InvertibleFunctionalCollection}.
	 * Converts the entire Collection internally on first call, so subsequent calls will be O(1).
	 *
	 * @param o
	 * 		The object to check the membership of
	 *
	 * @return Whether or not the given object is a member of this Collection
	 */
	@Override
	public boolean contains(final Object o)
	{
		if(this.internalSet.contains(o)) return true;
		else if(this.convertedCollection.isPresent()) return false;
		else
		{
			this.enumerate();
			return this.internalSet.contains(o);
		}
	}

	/**
	 * Checks for the membership of a Collection of Objects.
	 *
	 * @param c
	 * 		A Collection of Objects to check the membership of
	 *
	 * @return Whether all of the given Objects are in this Collection
	 */
	@Override
	public boolean containsAll(final Collection<?> c)
	{
		for(Object o : c)
			if(!this.contains(o)) return false;
		return true;
	}

	/**
	 * Enumerates on the entire Collection, calculating as it goes,
	 * and leaves the Collection with its entire internal collection
	 */
	private void enumerate()
	{
		final List<S> enumerated = new ArrayList<S>(this.internalCollection.size());
		for(T t : this.internalCollection)
		{
			final S s = this.function.apply(t);
			enumerated.add(s);
			this.internalSet.add(s);
		}
		this.convertedCollection = Optional.of(enumerated);
	}

	/**
	 * FunctionalCollections should essentially only be
	 * equal if the same object, as functional equivalency is a
	 * moral grey area at best.
	 *
	 * @param o
	 * 		The object with which to compare equality
	 *
	 * @return Whether these objects are considered equal
	 */
	@Override
	public boolean equals(Object o)
	{
		return this == o;
	}

	/**
	 * Hashes the internal Collection and XORs that with the hash of the function.
	 *
	 * @return A hash code representing this object
	 */
	@Override
	public int hashCode()
	{
		return this.internalCollection.hashCode() ^ this.function.hashCode();
	}

	/**
	 * @return Whether or not the internal Collection is empty
	 */
	@Override
	public boolean isEmpty()
	{
		return this.internalCollection.isEmpty();
	}

	/**
	 * Creates an Iterator for this Collection, which evaluates only on a "next" call.
	 *
	 * @return An Iterator over this Collection
	 */
	@Override
	public Iterator<S> iterator()
	{
		//TODO make the iterator able to add to the list of elements
		if(this.convertedCollection.isPresent()) return this.convertedCollection.get().iterator();
		else return new Iterator<S>()
		{
			final Iterator<T> internal = FunctionalCollection.this.internalCollection.iterator();

			@Override
			public boolean hasNext()
			{
				return this.internal.hasNext();
			}

			@Override
			public S next()
			{
				S s = FunctionalCollection.this.function.apply(this.internal.next());
				FunctionalCollection.this.internalSet.add(s);
				return s;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException("Cannot remove from this Collection.");
			}
		};
	}

	/**
	 * Does nothing and returns false.
	 *
	 * @param o
	 * 		Unused
	 *
	 * @return False
	 */
	@Override
	public boolean remove(final Object o)
	{
		return false;
	}

	/**
	 * Does nothing and returns false.
	 *
	 * @param c
	 * 		Unused
	 *
	 * @return False
	 */
	@Override
	public boolean removeAll(final Collection<?> c)
	{
		return false;
	}

	/**
	 * Does nothing and returns false.
	 *
	 * @param c
	 * 		Unused
	 *
	 * @return False
	 */
	@Override
	public boolean retainAll(final Collection<?> c)
	{
		return false;
	}

	/**
	 * @return The size of the internal collection
	 */
	@Override
	public int size()
	{
		return this.internalCollection.size();
	}

	/**
	 * Runtime: O(n)
	 *
	 * @return An array representing this entire Collection, fully converted
	 */
	@Override
	public Object[] toArray()
	{
		if(!this.convertedCollection.isPresent()) this.enumerate();
		return this.convertedCollection.get().toArray();
	}

	/**
	 * Runtime: O(n)
	 *
	 * @return An array representing this entire Collection, fully converted
	 */
	@Override
	public <S1> S1[] toArray(final S1[] a)
	{
		if(!this.convertedCollection.isPresent()) this.enumerate();
		return this.convertedCollection.get().toArray(a);
	}

	/**
	 * @return "A functional collection on: " followed by the toString() of the internal collection.
	 */
	@Override
	public String toString()
	{
		return "A functional collection on: " + this.internalCollection.toString();
	}
}
