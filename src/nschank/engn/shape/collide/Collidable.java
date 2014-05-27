package nschank.engn.shape.collide;

import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
import nschank.engn.shape.Drawable;


/**
 * Created by Nicolas Schank for package nschank.engn.shape.collide
 * Created on 28 Sep 2013
 * Last updated on 27 May 2014
 *
 * A Collidable object can be drawn, but also has the ability to tell whether it intersects with other Collidable objects.
 * Further, using the Collision subinterface, Collidables that are intersecting should be able to report how exactly to
 * correct that intersection using collision points and MTVs (Minimum Translation Vectors).
 *
 * @author Nicolas Schank
 * @version 4.0
 */
public interface Collidable extends Drawable
{
	/**
	 * Determines the collision point and MTV of the collision between this object and any other Collidable. The Collision
	 * should be from the point of view of this object; that is, this object's center position added to the MTV of this
	 * object should make the objects stop touching non-negligibly. If these objects are not colliding, Optional.absent()
	 * should be returned.
	 *
	 * @param other
	 * 		Another object which may be colliding with this one.
	 *
	 * @return A Collision between this object and {@code other} if it exists, or Optional.absent() if it does not
	 */
	public Optional<Collision> collisionWith(Collidable other);
	/**
	 * Determines how this object should collide with one of the four main Collidable types: an Axis-Aligned Box. Identical
	 * to collisionWith, except with the knowledge that the colliding object is an AAB.
	 *
	 * @param other
	 * 		An AAB that may be colliding with this object
	 *
	 * @return A Collision, as would be returned in collisionWith
	 */
	public Optional<Collision> collisionWithAAB(AAB other);
	/**
	 * Determines how this object should collide with one of the four main Collidable types: a Circle. Identical
	 * to collisionWith, except with the knowledge that the colliding object is an Circle.
	 *
	 * @param other
	 * 		An Circle that may be colliding with this object
	 *
	 * @return A Collision, as would be returned in collisionWith
	 */
	public Optional<Collision> collisionWithCircle(Circle other);
	/**
	 * Determines how this object should collide with one of the four main Collidable types: a Point. Identical
	 * to collisionWith, except with the knowledge that the colliding object is an Point.
	 *
	 * @param other
	 * 		An Point that may be colliding with this object
	 *
	 * @return A Collision, as would be returned in collisionWith
	 */
	public Optional<Collision> collisionWithPoint(Point other);
	/**
	 * Determines how this object should collide with one of the four main Collidable types: a Polygon. Identical
	 * to collisionWith, except with the knowledge that the colliding object is an Polygon.
	 *
	 * @param other
	 * 		An Polygon that may be colliding with this object
	 *
	 * @return A Collision, as would be returned in collisionWith
	 */
	public Optional<Collision> collisionWithPolygon(Polygon other);
	/**
	 * @return A Collidable identical to this Collidable in position, size, rotation, and color.
	 */
	public Collidable copy();
	/**
	 * Ray tracing for this object. If this Ray collides with this object, returns the distance from the Ray's starting
	 * point at which this object's edge occurs in the same plane. If the Ray and the object do not collide, returns
	 * Optional.absent()
	 *
	 * @param r
	 * 		A Ray in the same x-y coordinate plane as this object, which may be pointed to this object
	 *
	 * @return The distance along the given {@code Ray} at which this object's edge may be found, if that exists.
	 *
	 * @see nschank.engn.shape.collide.Ray
	 */
	public Optional<Double> distanceAlong(Ray r);
	/**
	 * @return The rotation, in radians, of this object.
	 */
	public double getRotation();

	/**
	 * @param theta
	 * 		The new rotation, in radians, of this object after calling this method
	 */
	public void setRotation(double theta);
	/**
	 * Assumes a mass of 1. Multiply by a mass, if that mass is incorrect.
	 *
	 * @return The moment of inertia of this shape around the z-axis passing through the center of mass
	 */
	public double momentOfInertia();

	/**
	 * @param theta
	 * 		Rotates this object {@code theta} radians clockwise
	 */
	public void rotate(double theta);

	/**
	 * A Collision between one Collidable and another. A Collision refers to one Collidable over the other; the Collision
	 * in the opposite direction can be returned by inverse(). Contains a collision point, as a two-dimension Dimensional,
	 * which must satisfy the following properties:
	 * - A Collision and its inverse must have equal collision points
	 * - The collision point must be contained within both Collidables within the Collision
	 * Also contains an MTV, which satisfies the following properties:
	 * - A Collision and its inverse must have MTV's which are additive inverses
	 * - The MTV must displace the first Collidable such that it is no longer intersecting the second Collidable (other
	 * than perhaps a single point)
	 */
	public static interface Collision
	{
		/**
		 * A point within both Collidables that can be treated as the point at which the two are touching. Impulses can
		 * be applied here to allow rotation to react correctly.
		 *
		 * @return A two-dimensional Dimensional within the two Collidables
		 */
		Dimensional getCollisionPoint();

		/**
		 * A Vector that can be added to the centre point of an object in order to negate the collision.
		 *
		 * @return A Vector that can be used to correct this Collision
		 */
		Vector getMTV();

		/**
		 * A Collision in the "opposite direction": between the same two objects, the other way around.
		 *
		 * @return The inverse of this Collision
		 */
		Collision inverse();
	}
}
