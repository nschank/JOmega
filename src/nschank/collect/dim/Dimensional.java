package nschank.collect.dim;

/**
 * Created by Nicolas Schank for package nschank.collect.dim Created 13 Feb 2013 Last updated on 25 Feb 2014
 * <p/>
 * A Dimensional object has k coordinates represented by {@code double}'s. Dimensionals must know all of their
 * coordinates, be able to get any of those coordinates on command, and must know how many coordinates they have.
 *
 * @author nschank, Brown University
 * @version 2.1
 * @since 13 Feb 2013
 */
public interface Dimensional
{
	/**
	 * Returns a copy of all coordinates as an array of doubles.
	 *
	 * @return An array of doubles containing all coordinates of this Dimensional.
	 */
	public double[] getAllCoordinates();

	/**
	 * The number of dimensions that this Dimensional contains. Must be a positive integer greater than 1.
	 *
	 * @return The dimensionality of this object
	 */
	public int getDimensions();

	/**
	 * Returns a particular coordinate for this Dimensional object.
	 *
	 * @param dimension
	 * 		- Which dimension of coordinate to return. From 0..getDimensions()-1;
	 *
	 * @return The coordinate at that index of this Dimensional Object.
	 */
	public double getCoordinate(int dimension);
}
