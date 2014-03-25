package nschank.collect.fxn;

import nschank.fxn.InvertibleFunction;
import nschank.note.Immutable;

import java.util.Collection;
import java.util.Iterator;


/**
 * Created by Nicolas Schank for package nschank.collect.fxn
 *
 * Created on 26 Feb 2014
 * Last updated on 25 Mar 2014
 *
 * A Collection that forwards to an internal collection, lazily performing a function on that input whenever an
 * element is accessed. Uses an {@link nschank.fxn.InvertibleFunction}, so checking membership is constant time.
 *
 * Behaviour is undefined if the internal Collection changes during usage.
 *
 * @author nschank, Brown University
 * @version 1.1.5
 */
@Immutable
public class InvertibleFunctionalCollection<T, S> implements Collection<S>
{
	/**
	 * A function to perform on the internal collection
	 */
	private final InvertibleFunction<T, S> function;
	/**
	 * An internal collection that the function will be called upon
	 */
	private final Collection<T> internalCollection;

	/**
	 * Wraps a given Collection; will call upon the given Function when calculating actual values
	 */
	public InvertibleFunctionalCollection(final Collection<T> coll, final InvertibleFunction<T, S> func)
	{
		this.internalCollection = coll;
		this.function = func;
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
	 * Checks whether this Object is in the set by calling the inverted function on the given object and checking for the membership of that object.
	 *
	 * @param o
	 * 		An object to check membership for
	 *
	 * @return If this object's preimage is in the internal collection.
	 */
	@Override
	public boolean contains(final Object o)
	{
		if(o.getClass().equals(this.getClass().getTypeParameters()[1].getClass()))
			return this.internalCollection.contains(this.function.applyInverse((S) o));
		else return false;
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
	 * InvertibleFunctionalCollections should essentially only be
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
		return new Iterator<S>()
		{
			final Iterator<T> internal = InvertibleFunctionalCollection.this.internalCollection.iterator();

			@Override
			public boolean hasNext()
			{
				return this.internal.hasNext();
			}

			@Override
			public S next()
			{
				return InvertibleFunctionalCollection.this.function.apply(this.internal.next());
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
		Object[] ret = new Object[this.internalCollection.size()];
		Iterator<T> it = this.internalCollection.iterator();
		for(int i = 0; it.hasNext(); i++)
			ret[i] = it.next();
		return ret;
	}

	/**
	 * Not implemented
	 *
	 * @return The given array with no change
	 */
	@Override
	public <S1> S1[] toArray(final S1[] a)
	{
		return a;
	}
}
