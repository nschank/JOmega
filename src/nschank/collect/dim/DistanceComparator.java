package nschank.collect.dim;

import java.util.Comparator;

import static nschank.collect.dim.Dimensionals.sqdistance;


/**
 * Created by Nicolas Schank for package nschank.collect.dim Created 13 Feb 2013 Last updated 25 Mar 2014
 * <p/>
 * A Comparator that compares based on how far it's two compared objects are from a single source given at construction.
 * Uses hashcode to break ties.
 *
 * @author Nicolas Schank
 * @version 2.1
 * @since 25 Mar 2014
 */
public class DistanceComparator implements Comparator<Dimensional>
{
	/**
	 * Measure the distance from here
	 */
	private Dimensional from;

	/**
	 * This DistanceComparator will sort objects from nearest to furthest based on the position of the given
	 * Dimensional.
	 *
	 * @param distanceFrom
	 * 		- The position to measure other Dimensionals from.
	 */
	public DistanceComparator(Dimensional distanceFrom)
	{
		this.from = distanceFrom;
	}

	/**
	 * @return 0 if the objects are equal; 1 if b>a; and -1 is b<a.
	 */
	public int compare(Dimensional a, Dimensional b)
	{
		if(a.equals(b)) return 0;
		double distanceA = sqdistance(this.from, a);
		double distanceB = sqdistance(this.from, b);
		if(distanceA < distanceB) return -1;
		else if(distanceA > distanceB) return 1;
		else return (int) Math.signum(a.hashCode() - b.hashCode());
	}
}
