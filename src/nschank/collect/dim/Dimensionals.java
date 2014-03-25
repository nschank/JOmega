package nschank.collect.dim;

/**
 * Created by Nicolas Schank for package nschank.collect.dim
 * Created 13 Feb 2014
 * Last updated 25 Mar 2014
 *
 * A Utility class for dealing with Dimensional objects.
 *
 * @author nschank, Brown University
 * @version 2.1
 */
public final class Dimensionals
{
	private Dimensionals()
	{
		//Utility class
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
