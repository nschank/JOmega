package nschank.engn.play.phys;

import com.google.common.base.Optional;
import cs195n.Vec2f;
import nschank.engn.play.Entity;
import nschank.engn.shape.Drawable;
import nschank.engn.shape.collide.Collidable;
import nschank.engn.shape.collide.Ray;


/**
 * Omega
 *
 * @author Nicolas Schank
 * @version 2013 10 21
 * @since 2013 10 21 7:19 AM
 */
public interface PhysicsEntity extends Entity, Drawable
{
	/**
	 * Stops all motion of any kind. This includes any derivative of position over time.
	 */
	void arrestMotion();

	/**
	 * Used in raycasting.
	 * {@see nschank.engn.shape.collide.Ray}
	 *
	 * @param ray
	 * 		A Ray to raycast with
	 *
	 * @return Either the distance from the ray's point to its collision with this PhysicsEntity, or Optional.absent()
	 */
	Optional<Float> collisionWith(Ray ray);

	/**
	 * @return The current angle of this PhysicsEntity, in radians, relative to the x axis
	 */
	float getAngle();

	/**
	 * Used by the collision class; since the coefficient of restitution for a collision
	 * is calculated using sqrt(r_1*r_2), it is more efficient for a PhysicsEntity to
	 * remember the square root of its restitution since sqrt(r_1*r_2)=sqrt(r_1)*sqrt(r_2)
	 *
	 * @return The square root of the coefficient of restitution
	 */
	float getCoefficientOfRestitutionSqrt();

	/**
	 * @return the mass of this PhysicsEntity
	 */
	float getMass();

	/**
	 * Guaranteed to completely contain the PhysicsEntity.
	 *
	 * @return The shape of this PhysicsEntity
	 */
	Collidable getShape();

	/**
	 * Whether or not this PhysicsEntityI is isColliding with another PhysicsEntityI
	 *
	 * @param physicsEntity
	 * 		Another PhysicsEntityI to check
	 *
	 * @return Whether or not they are colliding
	 */
	Optional<PhysCollision> collisionWith(PhysicsEntity physicsEntity);

	/**
	 * Adds to the current angle of this PhysicsEntity
	 *
	 * @param plusTheta
	 * 		How much to add to the current angle of this PhysicsEntity
	 */
	void rotate(float plusTheta);

	/**
	 * Changes the angle of this PhysicsEntity relative to the x-axis.
	 *
	 * @param theta
	 * 		The new angle of this PhysicsEntity, from the x-axis
	 */
	void setAngle(float theta);

	/**
	 * @param newMass
	 * 		the new mass of this PhysicsEntity
	 */
	void setMass(float newMass);

	float getMomentOfInertia();
	float getCoefficientOfStaticFrictionSqrt();
	float getCoefficientOfDynamicFrictionSqrt();
	void setRotationalVelocity(float f);

	Vec2f getVelocity();

	float getRotationalVelocity();

	void onCollide(PhysCollision collision, boolean impulses, boolean friction);

	void applyLocationChange(Vec2f change);

	void applyForceAt(Vec2f force, Vec2f position);

	void applyImpulseAt(Vec2f impulse, Vec2f position);

	void setVelocity(Vec2f newVelocity);

	public static interface PhysCollision
	{
		Vec2f getMTV();
		Vec2f getCollisionPoint();
		PhysCollision inverse();
		PhysicsEntity getOther();
		Vec2f getImpulse();
		Vec2f getSlidingFrictionalImpulse();
	}
}
