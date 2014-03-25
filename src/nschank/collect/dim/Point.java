package nschank.collect.dim;

import nschank.collect.map.Cache;
import nschank.note.Immutable;

import java.util.Arrays;
import java.util.Map;


/**
 * Created by Nicolas Schank for package nschank.collect.dim Created on 25 Feb 2014 Last updated 26 Feb 2014
 * <p/>
 * The flagship class of the Dimensional package. Represents a Cartesian point in space, with at least an x and y, as
 * well as the possibility of further coordinates.
 *
 * @author nschank, Brown University
 * @version 1.2
 * @since 25 Feb 2014
 */
@Immutable
public class Point implements Dimensional
{
	/**
	 * A Cache of Origins that the user requests
	 */
	private static final Map<Integer, Dimensional> ORIGIN = new Cache<>(10);
	/**
	 * The Origin in 2D space.
	 */
	public static final Dimensional ZERO_2D = new Point(0.0d, 0.0d);
	/**
	 * The Origin in 3D space.
	 */
	public static final Dimensional ZERO_3D = new Point(0.0d, 0.0d, 0.0d);
	/**
	 * All coordinates as an array of doubles.
	 */
	private final double[] coordinates;

	/**
	 * Creates an origin point in the given dimension
	 *
	 * @param dim
	 * 		The dimensionality of the origin to create
	 */
	private Point(int dim)
	{
		this.coordinates = new double[dim];
		for(int i = 0; i < dim; i++)
			this.coordinates[i] = 0.0d;
	}

	/**
	 * Creates a Point with the coordinates given
	 *
	 * @param coordinates
	 * 		The Dimensions of this Point
	 */
	public Point(double... coordinates)
	{
		this.coordinates = Arrays.copyOf(coordinates, coordinates.length);
	}

	/**
	 * @param copy
	 * 		A Point to make an exact copy of
	 */
	public Point(Point copy)
	{
		this(copy.coordinates);
	}

	/**
	 * @param asPoint
	 * 		Creates a Point out of the location of this dimensional
	 */
	public Point(Dimensional asPoint)
	{
		this(asPoint.getAllCoordinates());
	}

	/**
	 * Returns an origin with the number of dimensions specified by dim. Guaranteed to return identical Points on
	 * different calls with the same dimensionality.
	 *
	 * @param dim
	 * 		The number of dimensions
	 *
	 * @return An origin point in that many dimensions
	 */
	public static Dimensional zero(int dim)
	{
		if(dim < 2) throw new OneDimensionalPointException();
		if(dim == 2) return ZERO_2D;
		if(dim == 3) return ZERO_3D;

		if(ORIGIN.containsKey(dim)) return ORIGIN.get(dim);
		else
		{
			Dimensional origin = new Point(dim);
			ORIGIN.put(dim, origin);
			return origin;
		}
	}

	/**
	 * Two points are equal if they are in the same k-space and have the same coordinates.
	 *
	 * @param o
	 * 		The Object to check the equality of
	 *
	 * @return Whether the given Object is equal to this Point
	 */
	@Override
	public boolean equals(Object o)
	{
		if(o == this) return true;
		if(o == null) return false;
		if(o.getClass() != this.getClass()) return false;
		Point p = (Point) o;
		return Arrays.equals(this.coordinates, p.coordinates);
	}

	/**
	 * All of the coordinates of this Point
	 *
	 * @return An array of coordinates representing this Point's dimensions in space
	 */
	@Override
	public double[] getAllCoordinates()
	{
		return Arrays.copyOf(this.coordinates, this.coordinates.length);
	}

	/**
	 * @param dimension
	 * 		- Which dimension of coordinate to return. From 0..getDimensions()-1;
	 *
	 * @return The coordinate's value in that dimension
	 */
	@Override
	public double getCoordinate(final int dimension)
	{
		return this.coordinates[dimension];
	}

	/**
	 * @return The dimensionality of this Point
	 */
	@Override
	public int getDimensions()
	{
		return this.coordinates.length;
	}

	/**
	 * @return The hash of all of the coordinates of this Point
	 */
	@Override
	public int hashCode()
	{
		return Arrays.hashCode(this.coordinates);
	}

	/**
	 * @return Whether this Point is the Origin
	 */
	public boolean isZero()
	{
		for(int i = 0; i < this.coordinates.length; i++)
			if(this.coordinates[i] != 0.0d) return true;
		return false;
	}

	/**
	 * @return A String representation of this Point.
	 */
	@Override
	public String toString()
	{
		return Arrays.toString(this.coordinates);
	}
}
