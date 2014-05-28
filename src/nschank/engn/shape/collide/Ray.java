package nschank.engn.shape.collide;

import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Point;
import nschank.collect.dim.Vector;
import nschank.note.Immutable;


/**
 * Created by Nicolas Schank for package nschank.engn.shape.collide
 * Created on 19 Oct 2013
 * Last updated on 27 May 2014
 *
 * A Ray used for raycasting. Has a starting point and a direction (as a Vector) in which the Ray points.
 *
 * @author nschank, Brown University
 * @version 1.3
 */
@Immutable
public final class Ray
{
	private final Dimensional startLocation;
	private final Vector direction;

	/**
	 * @return The location this ray is originating from
	 */
	Dimensional getStartLocation()
	{
		return this.startLocation;
	}

	/**
	 * @return A vector representing the direction vector of this ray.
	 */
	Vector getDirection()
	{
		return this.direction;
	}

	/**
	 * @param distance
	 * 		The distance from the starting location
	 *
	 * @return A location representing the point on the Ray that is the given distance away from the starting location
	 */
	public Dimensional getAtDistance(double distance)
	{
		return this.direction.smult(distance).plus(this.startLocation);
	}

	/**
	 * @param point
	 * 		What point to project onto this ray.
	 *
	 * @return Where this point appears on this ray
	 */
	public Dimensional projectOnto(Dimensional point)
	{
		return new Vector(point).projectOntoLine(this.startLocation, this.direction.plus(this.startLocation));
	}

	/**
	 * Creates a single Ray, from a location and direction. Direction will be normalized, and as such cannot be of length
	 * 0.
	 *
	 * @param location
	 * 		What point the Ray originates from.
	 * @param directionalVector
	 * 		A Vector pointing in the direction of the Ray
	 */
	public Ray(Dimensional location, Vector directionalVector)
	{
		this.startLocation = new Point(location);
		this.direction = directionalVector.normalized();
	}

	/**
	 * Creates a single Ray from a location and an angle, from the positive x axis, in radians.
	 *
	 * @param location
	 * 		What point the ray originates from.
	 * @param angle
	 * 		The angle of the Ray (in radians from the x axis)
	 */
	public Ray(Dimensional location, float angle)
	{
		this.startLocation = new Point(location);
		this.direction = Vector.fromPolar(1, angle);
	}

	@Override
	public String toString()
	{
		return "Vector{" +
				this.getStartLocation() + " -> " + this.getDirection() + "}";
	}
}
