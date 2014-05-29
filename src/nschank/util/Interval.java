package nschank.util;

/**
 * Created by Nicolas Schank for package nschank.util;
 * Created on 28 May 2014
 * Last updated on 28 May 2014
 *
 * An Interval that extends between two numbers.
 *
 * @author nschank, Brown University
 * @version 1.1
 */
public interface Interval
{
	/**
	 * Finds the union of this Interval and another Interval. It must contain everything in both Intervals, and possibly
	 * other things between the two Intervals.
	 *
	 * @param i
	 * 		Any other Interval
	 *
	 * @return The union of these Intervals, including numbers between the two Intervals
	 */
	public Interval and(Interval i);
	/**
	 * Finds the union of this Interval and a number, which must contain this entire Interval extended to include the
	 * given number.
	 *
	 * @param d
	 * 		Any number
	 *
	 * @return An Interval strictly larger or equal to the starting Interval
	 */
	public Interval and(double d);
	/**
	 * The midpoint of this Interval
	 *
	 * @return The average of the minimum and the maximum
	 */
	public double center();
	/**
	 * The 'collision' between two Intervals: the midpoint of their intersection, if one exists.
	 *
	 * @param i
	 * 		Any other Interval
	 *
	 * @return The midpoint of the intersection of the Interval
	 */
	public double collision(Interval i);
	/**
	 * Whether the given number is within this Interval.
	 *
	 * @param d
	 * 		Any number
	 *
	 * @return Whether this number is in this Interval
	 */
	public boolean contains(double d);
	/**
	 * @return The maximum number which is contained in this Interval
	 */
	public double getMax();
	/**
	 * @return The minimum number which is contained in this Interval
	 */
	public double getMin();
	/**
	 * The minimum (by absolute value) amount which, if added to this Interval, will make it so that the two Intervals
	 * now share a minimum and maximum and do not otherwise intersect.
	 *
	 * @param i
	 * 		An interval intersecting with this
	 *
	 * @return A minimum translation to remove the intersection of these two Intervals
	 */
	public double getMinimumTranslation(Interval i);
	/**
	 * Whether there exists any number such that it is contained in both Intervals
	 *
	 * @param i
	 * 		Any other Interval
	 *
	 * @return the existence of a number which both Intervals contain
	 */
	public boolean isIntersecting(Interval i);
	/**
	 * Translates this interval by a specified amount
	 *
	 * @param delta
	 * 		An amount by which to move this Interval
	 *
	 * @return A translated Interval of the same width as this one
	 */
	public Interval plus(double delta);
	/**
	 * Creates an interval with the same centre-point as the current Interval, but of a different width.
	 *
	 * @param diameter
	 * 		The new width of the created Interval
	 *
	 * @return A stretched Interval
	 */
	public Interval stretch(double diameter);
	/**
	 * The difference between the maximum and minimum of this Interval
	 *
	 * @return the width of this Interval
	 */
	public double width();
}
