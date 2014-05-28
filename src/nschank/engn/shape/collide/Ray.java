package nschank.engn.shape.collide;

import cs195n.Vec2f;


/**
 * A Ray used for raycasting. Exists in a World and
 * can identify what things with which it is isColliding, and
 * which thing it collides with first. Immutable.
 *
 * @author Nicolas Schank
 * @version M2
 * @since 2013 10 19 4:14 PM
 */
public final class Ray
{
	private final Vec2f startLocation;
	private final Vec2f direction;

	/**
	 * @return The location this ray is originating from
	 */
	Vec2f getStartLocation()
	{
		return this.startLocation;
	}

	/**
	 * @return A vector representing the direction vector of this ray.
	 */
	Vec2f getDirection()
	{
		return this.direction;
	}

	/**
	 * @param f
	 * 		The distance from the starting location
	 *
	 * @return A vector representing the point on the Ray the given distance away from the starting location
	 */
	public Vec2f getAtDistance(float f)
	{
		return this.startLocation.plus(this.direction.smult(f));
	}

	/**
	 * @param point
	 * 		What point vector to project onto this ray.
	 *
	 * @return Where this point appears on this ray
	 */
	Vec2f projectOnto(Vec2f point)
	{
		return point.projectOntoLine(this.startLocation, this.startLocation.plus(this.direction));
	}

	/**
	 * Creates a single Ray, from a location and direction.
	 * Direction will be normalized.
	 *
	 * @param locationalVector
	 * @param directionalVector
	 */
	public Ray(Vec2f locationalVector, Vec2f directionalVector)
	{
		this.startLocation = locationalVector;
		this.direction = directionalVector.normalized();
	}

	/**
	 * Creates a single Ray, from a location and an angle, from the positive x axis, in radians.
	 *
	 * @param locationalVector
	 * 		What point the ray originates from.
	 * @param angle
	 * 		The angle
	 */
	public Ray(Vec2f locationalVector, float angle)
	{
		this.startLocation = locationalVector;
		this.direction = Vec2f.fromPolar(angle, 1.0f);
	}

	@Override
	public String toString()
	{
		return getStartLocation() + " -> " + getDirection();
	}
}
