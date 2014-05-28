package nschank.util;

import com.google.common.base.Function;
import nschank.note.Immutable;


/**
 * Tou1
 *
 * @author Nicolas Schank
 * @version 2013 09 29
 * @since 2013 09 29 8:19 PM
 */
@Immutable
public class Interval
{
	private final double min;
	private final double max;

	public double collision(Interval other)
	{
		double shiftLeft = other.min - max;
		double shiftRight = other.max - min;
		if(Math.abs(shiftLeft) < Math.abs(shiftRight)) return max + (shiftLeft / 2f);
		else return min + (shiftRight / 2f);
	}

	public double getMin()
	{
		return min;
	}

	public double getMax()
	{
		return max;
	}

	public double getMinimumTranslation(Interval other)
	{
		double shiftLeft = other.min - max;
		double shiftRight = other.max - min;
		if(Math.abs(shiftLeft) < Math.abs(shiftRight)) return shiftLeft;
		else return shiftRight;
	}

	public static boolean within(double minimum, double maximum, double between)
	{
		return (minimum <= between) && (maximum >= between);
	}

	public Interval stretch(double diameter)
	{
		return Interval.about(this.center(), diameter);
	}

	public double center()
	{
		return this.getMin() + (this.width() / 2f);
	}

	public double width()
	{
		return this.getMax() - this.getMin();
	}

	public boolean contains(double is)
	{
		return (min <= is) && (is <= max);
	}

	public Interval(double min, double max)
	{
		this.min = Math.min(min, max);
		this.max = Math.max(min, max);
	}

	public static Interval about(double mid, double width)
	{
		return new Interval(mid - (width / 2f), mid + (width / 2f));
	}

	public Interval and(Interval b)
	{
		return and(this, b);
	}

	public static Interval and(Interval a, Interval b)
	{
		return new Interval(Math.min(a.getMin(), b.getMin()), Math.max(b.getMax(), a.getMax()));
	}

	public static Interval of(double min, double max)
	{
		return new Interval(min, max);
	}

	public static Interval from(Iterable<Double> anything)
	{
		Interval startInterval = null;
		for(Double t : anything)
		{
			if(startInterval == null) startInterval = Interval.about(t, 0);
			else startInterval = startInterval.and(Interval.about(t, 0));
		}
		if(startInterval == null) throw new IllegalStateException("Cannot make an interval from nothing.");
		return startInterval;
	}

	public static <T> Interval from(Iterable<T> anything, Function<T, Double> todouble)
	{
		Interval startInterval = null;
		for(T t : anything)
		{
			if(startInterval == null) startInterval = Interval.about(todouble.apply(t), 0);
			else startInterval = startInterval.and(Interval.about(todouble.apply(t), 0));
		}
		if(startInterval == null) throw new IllegalStateException("Cannot make an interval from nothing.");
		return startInterval;
	}

	public static boolean intersect(Interval a, Interval b)
	{
		return b.contains(a.getMin()) || b.contains(a.getMax()) || a.contains(b.getMin()) || a.contains(b.getMax());
	}

	public Interval plus(int delta)
	{
		return new Interval(this.min + delta, this.max + delta);
	}

	public boolean isIntersecting(Interval b)
	{
		return intersect(this, b);
	}

	@Override
	public String toString()
	{
		return (new StringBuilder("(").append(min).append(',').append(max).append(')')).toString();
	}
}
