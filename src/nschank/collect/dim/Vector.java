package nschank.collect.dim;

import nschank.note.Immutable;


/**
 * Created by Nicolas Schank for package nschank.collect.dim
 * Created on 26 Feb 2014
 * Last updated on 25 Mar 2014
 *
 * A Vector class, complete with common vector operations.
 *
 * @author nschank, Brown University
 * @version 1.2.2
 * @since 26 Feb 2014
 */
@Immutable
public class Vector extends Point
{
	/**
	 * The Origin in 2D space.
	 */
	public static final Vector ZERO_2D = new Vector(Point.ZERO_2D);
	/**
	 * The Origin in 3D space.
	 */
	public static final Vector ZERO_3D = new Vector(Point.ZERO_3D);
	/**
	 * The value of the magnitude, calculated at constructor.
	 */
	private final double mag2;

	/**
	 * Creates a 2D Vector of the given length and at the given angle from the x-axis.
	 * @param distance
	 * 		The length of the produced Vector
	 * @param angle
	 * 		The angle of the Vector in radians
	 * @return A 2D Vector as produced from the given polar coordinates
	 */
	public static Vector fromPolar(double distance, double angle)
	{
		return new Vector(distance * Math.cos(angle), distance * Math.sin(angle));
	}

	/**
	 * Creates a Vector from the origin to the given coordinates
	 *
	 * @param coordinates
	 * 		The coordinates to which this Vector points
	 */
	public Vector(double... coordinates)
	{
		super(coordinates);
		double prepMag2 = 0.0d;
		for(double d : coordinates)
			prepMag2 += d * d;
		this.mag2 = prepMag2;
	}

	/**
	 * @param asPoint
	 * 		Creates a Vector from this Dimensional
	 */
	public Vector(Dimensional asPoint)
	{
		this(asPoint.getAllCoordinates());
	}

	/**
	 * Returns a zero vector with the number of dimensions specified by dim. Caches identical Vectors on different calls
	 * with the same dimensionality.
	 *
	 * @param dim
	 * 		The number of dimensions
	 *
	 * @return An origin point in that many dimensions
	 */
	public static Vector zero(int dim)
	{
		if(dim < 2) throw new OneDimensionalPointException();
		if(dim == 2) return Vector.ZERO_2D;
		if(dim == 3) return Vector.ZERO_3D;

		return new Vector(Point.zero(dim));
	}

	/**
	 * Finds the cross product of this (2D or 3D) vector and the given (2D or 3D) vector
	 *
	 * @param v
	 * 		Another Vector (in 3D)
	 *
	 * @return The cross product vector of this Vector and the given Vector
	 *
	 * @throws nschank.collect.dim.Vector.CrossProductError
	 * 		If either Vector is not 2D or 3D
	 */
	public Vector crossProduct(Dimensional v)
	{
		if(this.getDimensions() > 3 || v.getDimensions() > 3) throw new CrossProductError();

		double u1 = this.getCoordinate(0);
		double u2 = this.getCoordinate(1);
		double u3 = this.getDimensions() > 2 ? this.getCoordinate(2) : 0;

		double v1 = v.getCoordinate(0);
		double v2 = v.getCoordinate(1);
		double v3 = v.getDimensions() > 2 ? v.getCoordinate(2) : 0;

		return new Vector(u2 * v3 - v2 * u3, u1 * v3 - v1 * u3, u1 * v2 - v1 * u2);
	}

	/**
	 * Finds the dot product between this Vector and another Vector. If the two Vectors are of different dimensionality,
	 * reduces the larger one so that the dot product exists.
	 *
	 * @param v
	 * 		A Vector to dot with this Vector
	 *
	 * @return The dot product of this Vector and v
	 */
	public double dotProduct(Dimensional v)
	{
		double d = 0.0d;
		for(int i = 0; i < Math.min(this.getDimensions(), v.getDimensions()); i++)
		{
			d += this.getCoordinate(i) * v.getCoordinate(i);
		}
		return d;
	}

	/**
	 * Define equaliy for this object
	 *
	 * @param o
	 * 		The object with which to compare equality
	 *
	 * @return Whether these objects are considered equal
	 */
	@Override
	public boolean equals(Object o)
	{
		if(o == this) return true;
		if(o == null) return false;
		if(o.getClass() != this.getClass()) return false;
		return super.equals(o);
	}

	/**
	 * @return Whether this Point is the Origin
	 */
	public boolean isZero()
	{
		return this.mag2 == 0.0d;
	}

	/**
	 * @return The actual magnitude of this Vector
	 */
	public double mag()
	{
		return Math.sqrt(this.mag2);
	}

	/**
	 * @return The squared magnitude of this Vector
	 */
	public double mag2()
	{
		return this.mag2;
	}

	/**
	 * Finds the difference of two Vectors
	 *
	 * @param v
	 * 		A Vector to subtract from this Vector
	 *
	 * @return This Vector minus the given Vector
	 */
	public Vector minus(Dimensional v)
	{
		double[] coord = new double[this.getDimensions()];
		for(int i = 0; i < Math.max(this.getDimensions(), v.getDimensions()); i++)
			coord[i] = ((i < this.getDimensions()) ? this.getCoordinate(i) : 0) - ((i < v.getDimensions()) ? v.getCoordinate(i) : 0);
		return new Vector(coord);
	}

	/**
	 * Finds this Vector, normalized
	 *
	 * @return This Vector divided by the given scalar
	 */
	public Vector normalized()
	{
		return this.sdiv(Math.sqrt(this.mag2));
	}

	/**
	 * Finds the sum of two Vectors
	 *
	 * @param v
	 * 		A Vector to add to this Vector
	 *
	 * @return This Vector added to the given Vector
	 */
	public Vector plus(Dimensional v)
	{
		double[] coord = new double[this.getDimensions()];
		for(int i = 0; i < Math.max(this.getDimensions(), v.getDimensions()); i++)
			coord[i] = ((i < this.getDimensions()) ? this.getCoordinate(i) : 0) + ((i < v.getDimensions()) ? v.getCoordinate(i) : 0);
		return new Vector(coord);
	}

	/**
	 * Returns the component of this vector along the axis specified by {@code other}.
	 *
	 * @param other
	 * 		the axis onto which to project this vector
	 *
	 * @return the projection of this vector in the axis specified by {@code other}
	 *
	 * Remodelled from cs195n.Vec2f
	 */
	public final Vector projectOnto(Vector other)
	{
		return other.smult(this.dotProduct(other) / other.mag2());
	}

	/**
	 * Returns the projection of the point represented by this vector onto a line specified by
	 * {@code p1} and {@code p2}, two points on the line.
	 *
	 * @param p1
	 * 		a point on the line onto which to project
	 * @param p2
	 * 		another point on the line onto which to project
	 *
	 * @return the projection of the point represented by this vector onto a line specified
	 * by {@code p1} and {@code p2}
	 *
	 * Remodelled from cs195n.Vec2f
	 */
	public final Vector projectOntoLine(Dimensional p1, Dimensional p2)
	{
		Vector between = new Vector(p1).minus(p2);
		return new Vector(p1).plus(between.smult(this.minus(p1).dotProduct(between) / between.mag2()));
	}

	/**
	 * Finds the scalar division of the current Vector
	 *
	 * @param d
	 * 		The amount by which to scalarly divide this Vector
	 *
	 * @return This Vector divided by the given scalar
	 */
	public Vector sdiv(double d)
	{
		assert d != 0;
		double[] coord = new double[this.getDimensions()];
		for(int i = 0; i < this.getDimensions(); i++)
			coord[i] = this.getCoordinate(i) / d;
		return new Vector(coord);
	}

	/**
	 * Finds the scalar multiplication of the current Vector
	 *
	 * @param d
	 * 		The amount by which to scalarly multiply this Vector
	 *
	 * @return This Vector multiplied by the given scalar
	 */
	public Vector smult(double d)
	{
		double[] coord = new double[this.getDimensions()];
		for(int i = 0; i < this.getDimensions(); i++)
			coord[i] = d * this.getCoordinate(i);
		return new Vector(coord);
	}

	/**
	 * @return A String representation of this Vector
	 */
	@Override
	public String toString()
	{
		return new StringBuilder("<").append(super.toString()).append(">").toString();
	}

	/**
	 * A Cross Product failing to function correctly
	 */
	private class CrossProductError extends Error
	{
		CrossProductError()
		{
			super("Cross Product is only defined in 2 or 3 dimensions.");
		}
	}
}
