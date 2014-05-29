package nschank.util;

import nschank.note.Immutable;

import static java.lang.Math.max;
import static java.lang.Math.min;


/**
 * Created by Nicolas Schank for package nschank.util
 * Created on 29 Sep 2013
 * Last updated on 28 May 2014
 *
 * An Interval implementation that uses doubles.
 *
 * @author nschank, Brown University
 * @version 3.1
 */
@Immutable
public class DefaultInterval implements Interval
{
	private final double max;
	private final double min;

	/**
	 * Creates an DefaultInterval between the given two number. Order is unimportant, as the minimum and maximum are chosen
	 * internally.
	 *
	 * @param edge1
	 * 		Any number
	 * @param edge2
	 * 		Any other number
	 */
	public DefaultInterval(double edge1, double edge2)
	{
		this.min = Math.min(edge1, edge2);
		this.max = Math.max(edge1, edge2);
		assert this.min <= this.max;
	}

	/**
	 * Finds the union of this Interval and another Interval. It must contain everything in both Intervals, and possibly
	 * other things between the two Intervals.
	 *
	 * @param i
	 * 		Any other Interval
	 *
	 * @return The union of these Intervals, including numbers between the two Intervals
	 */
	@Override
	public Interval and(final Interval i)
	{
		return new DefaultInterval(min(this.min, i.getMin()), max(this.max, i.getMax()));
	}

	/**
	 * Finds the union of this Interval and a number, which must contain this entire Interval extended to include the
	 * given number.
	 *
	 * @param d
	 * 		Any number
	 *
	 * @return An Interval strictly larger or equal to the starting Interval
	 */
	@Override
	public Interval and(final double d)
	{
		if(d >= this.max) return new DefaultInterval(this.min, d);
		if(d <= this.min) return new DefaultInterval(d, this.max);
		return this;
	}

	/**
	 * The midpoint of this Interval
	 *
	 * @return The average of the minimum and the maximum
	 */
	@Override
	public double center()
	{
		return this.getMin() + (this.width() / 2d);
	}

	/**
	 * The 'collision' between two Intervals: the midpoint of their intersection, if one exists.
	 *
	 * @param i
	 * 		Any other Interval
	 *
	 * @return The midpoint of the intersection of the Interval
	 */
	@Override
	public double collision(final Interval i)
	{
		double shiftLeft = i.getMin() - this.max;
		double shiftRight = i.getMax() - this.min;
		if(Math.abs(shiftLeft) < Math.abs(shiftRight)) return this.max + (shiftLeft / 2f);
		else return this.min + (shiftRight / 2f);
	}

	/**
	 * Whether the given number is within this Interval.
	 *
	 * @param d
	 * 		Any number
	 *
	 * @return Whether this number is in this Interval
	 */
	@Override
	public boolean contains(final double d)
	{
		return (this.min <= d) && (d <= this.max);
	}

	/**
	 * @return The maximum number which is contained in this Interval
	 */
	@Override
	public double getMax()
	{
		return this.max;
	}

	/**
	 * @return The minimum number which is contained in this Interval
	 */
	@Override
	public double getMin()
	{
		return this.min;
	}

	/**
	 * The minimum (by absolute value) amount which, if added to this Interval, will make it so that the two Intervals
	 * now share a minimum and maximum and do not otherwise intersect.
	 *
	 * @param i
	 * 		An interval intersecting with this
	 *
	 * @return A minimum translation to remove the intersection of these two Intervals
	 */
	@Override
	public double getMinimumTranslation(final Interval i)
	{
		double shiftLeft = i.getMin() - this.max;
		double shiftRight = i.getMax() - this.min;
		if(Math.abs(shiftLeft) < Math.abs(shiftRight)) return shiftLeft;
		else return shiftRight;
	}

	/**
	 * Whether there exists any number such that it is contained in both Intervals
	 *
	 * @param i
	 * 		Any other Interval
	 *
	 * @return the existence of a number which both Intervals contain
	 */
	@Override
	public boolean isIntersecting(final Interval i)
	{
		return Intervals.intersect(this, i);
	}

	/**
	 * Translates this interval by a specified amount
	 *
	 * @param delta
	 * 		An amount by which to move this Interval
	 *
	 * @return A translated Interval of the same width as this one
	 */
	@Override
	public Interval plus(final double delta)
	{
		return new DefaultInterval(this.min + delta, this.max + delta);
	}

	/**
	 * Creates an interval with the same centre-point as the current Interval, but of a different width.
	 *
	 * @param diameter
	 * 		The new width of the created Interval
	 *
	 * @return A stretched Interval
	 */
	@Override
	public Interval stretch(final double diameter)
	{
		return Intervals.about(this.center(), diameter);
	}

	/**
	 * The difference between the maximum and minimum of this Interval
	 *
	 * @return the width of this Interval
	 */
	@Override
	public double width()
	{
		return this.max - this.min;
	}

	/**
	 * A String representation of this Interval
	 *
	 * @return "[min,max]"
	 */
	@Override
	public String toString()
	{
		return "[" + this.min + ',' + this.max + ']';
	}
}
