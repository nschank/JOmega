package nschank.util;

import com.google.common.base.Function;


/**
 * Created by Nicolas Schank for package nschank.util;
 * Created on 28 May 2014
 * Last updated on 28 May 2014
 *
 * A Utility class for dealing with Intervals.
 *
 * @author nschank, Brown University
 * @version 1.2
 */
public final class Intervals
{
	/**
	 * Never used
	 */
	private Intervals()
	{
		//Utility class
	}

	/**
	 * Creates an Interval about a particular point and at a particular width
	 *
	 * @param mid
	 * 		The center of the new Interval
	 * @param width
	 * 		The width of the new Interval
	 *
	 * @return An Interval from [mid-width/2, mid+width/2]
	 */
	public static Interval about(double mid, double width)
	{
		return new DefaultInterval(mid - (width / 2f), mid + (width / 2f));
	}

	/**
	 * Creates an Interval from a collection of numbers
	 *
	 * @param numbers
	 * 		A collection of any numbers
	 *
	 * @return An Interval which contains every number in the collection, between the minimum and maximum of the numbers
	 */
	public static Interval from(Iterable<Double> numbers)
	{
		Interval startInterval = null;
		for(Double t : numbers)
		{
			if(startInterval == null) startInterval = Intervals.about(t, 0);
			else startInterval = startInterval.and(t);
		}
		if(startInterval == null) throw new IllegalStateException("Cannot make an interval from nothing.");
		return startInterval;
	}

	/**
	 * Creates an Interval from a collections of numbers formed from any collection and a function relating that collection
	 * to a number.
	 *
	 * @param anything
	 * 		Any collection
	 * @param todouble
	 * 		A function that will translate that collection into a number
	 * @param <T>
	 * 		Any type
	 *
	 * @return The Interval of all numbers produced by {@code todouble} applied to {@code anything}
	 */
	public static <T> Interval from(Iterable<? extends T> anything, Function<? super T, Double> todouble)
	{
		Interval startInterval = null;
		for(T t : anything)
		{
			if(startInterval == null) startInterval = Intervals.about(todouble.apply(t), 0);
			else startInterval = startInterval.and(Intervals.about(todouble.apply(t), 0));
		}
		if(startInterval == null) throw new IllegalArgumentException("Cannot make an interval from nothing.");
		return startInterval;
	}

	/**
	 * Whether {@code between} is between {@code sideone} and {@code sidetwo}.
	 *
	 * @param sideone
	 * 		Any number
	 * @param sidetwo
	 * 		Any other number
	 * @param between
	 * 		Any third number
	 *
	 * @return Whether the third number is between the other two
	 */
	public static boolean within(double sideone, double sidetwo, double between)
	{
		return Math.signum(between - sideone) == Math.signum(between - sidetwo) || sideone == between
				|| sidetwo == between;
	}
}
