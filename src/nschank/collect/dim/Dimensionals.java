package nschank.collect.dim;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicolas Schank for package nschank.collect.dim
 * Created 13 Feb 2014
 * Last updated 27 May 2014
 *
 * A Utility class for dealing with Dimensional objects.
 *
 * @author nschank, Brown University
 * @version 2.2
 */
public final class Dimensionals
{
	private Dimensionals()
	{
		//Utility class
	}

	/**
	 * Given a collection of points and a point of origin, returns the point which is closest to the point of origin. If
	 * the collection is empty, returns {@code null}. If any point in the collection has a distance of 0, returns that point
	 * immediately. If multiple points are at the same calculated closest distance from the point of origin, will
	 * return the first one.
	 *
	 * @param from
	 * 		A point to compare the distance from
	 * @param of
	 * 		A collection of points to measure from {@code from}
	 * @return The closest point to {@code from} within {@code of}
	 */
	public static Dimensional closestTo(Dimensional from, Iterable<Dimensional> of)
	{
		Dimensional closest = null;
		double distance = Double.MAX_VALUE;
		for(Dimensional pt : of)
		{
			double ptdist = sqdistance(pt, from);
			if((closest == null) || (distance > ptdist))
			{
				closest = pt;
				distance = ptdist;
				if(distance == 0) return closest;
			}
		}
		return closest;
	}

	/**
	 * Given a collection of points and a particular dimension, returns a list (ordered one-to-one with the given iterable)
	 * which contains the coordinates of each of those points within that dimension. If any of the points do not have
	 * that dimension, an error will likely be thrown.
	 *
	 * @param dimensionals
	 * 		Any collection of {@code Dimensional}s
	 * @param coordinate
	 * 		A dimension
	 * @return All coordinates in that dimension
	 */
	public static List<Double> getCoordinate(Iterable<Dimensional> dimensionals, int coordinate)
	{
		List<Double> coordinates = new ArrayList<>();
		for(Dimensional d : dimensionals)
			coordinates.add(d.getCoordinate(coordinate));
		return coordinates;
	}

	/**
	 * Does the same as sqdistance, but finds the square root of that value.
	 *
	 * @param a
	 * 		- One Dimensional.
	 * @param b
	 * 		- A second Dimensional.
	 *
	 * @return The distance between them.
	 */
	public static double distance(Dimensional a, Dimensional b)
	{
		return Math.sqrt(sqdistance(a, b));
	}

	/**
	 * Finds the squared distance between two Dimensionals. Treats a coordinate with fewer dimensions as being at 0 in
	 * any dimensions it doesn't possess. For example, the distance between (5,6) and (4,5,3,2) will treat (5,6) as
	 * (5,6,0,0).
	 * <p/>
	 * When only comparing distances, this is MUCH faster and highly recommended.
	 *
	 * @param a
	 * 		- One Dimensional.
	 * @param b
	 * 		- A second Dimensional.
	 *
	 * @return The squared distance between them.
	 */
	public static double sqdistance(Dimensional a, Dimensional b)
	{
		Dimensional moreDimensions = (a.getDimensions() >= b.getDimensions() ? a : b);
		Dimensional fewerDimensions = (a.getDimensions() < b.getDimensions() ? a : b);
		double dist = 0;

		for(int i = 0; i < fewerDimensions.getDimensions(); i++)
			dist += Math.pow(a.getCoordinate(i) - b.getCoordinate(i), 2);
		for(int i = fewerDimensions.getDimensions(); i < moreDimensions.getDimensions(); i++)
			dist += Math.pow(moreDimensions.getCoordinate(i), 2);
		return dist;
	}
}
