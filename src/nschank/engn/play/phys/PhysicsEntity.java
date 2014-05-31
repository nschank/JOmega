package nschank.engn.play.phys;

import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
import nschank.engn.play.Entity;
import nschank.engn.shape.Drawable;
import nschank.engn.shape.collide.Collidable;
import nschank.engn.shape.collide.Ray;


/**
 * Created by Nicolas Schank for package nschank.engn.play.phys
 * Created on 21 Oct 2013
 * Last updated on 30 May 2014
 *
 * A {@code PhysicsEntity} is one that is expected to interact in a 2D physical world (aka a {@code Universe}) and, as
 * such, it must know how to interact with forces, impulses, and collisions with other objects.
 *
 * @author nschank, Brown University
 * @version 3.2
 */
public interface PhysicsEntity extends Entity, Drawable
{
	/**
	 * Applies the given 2D {@code force} (as a {@code Vector}) at a particular {@code position} on the object.
	 *
	 * @param force
	 * @param position
	 */
	void applyForceAt(Vector force, Dimensional position);
	/**
	 *
	 * @param impulse
	 * @param position
	 */
	void applyImpulseAt(Vector impulse, Dimensional position);
	/**
	 *
	 * @param change
	 */
	void applyLocationChange(Vector change);
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
	Optional<Double> collisionWith(Ray ray);
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
	 * @return The current angle of this PhysicsEntity, in radians, relative to the x axis
	 */
	double getAngle();
	/**
	 * Changes the angle of this PhysicsEntity relative to the x-axis.
	 *
	 * @param theta
	 * 		The new angle of this PhysicsEntity, from the x-axis
	 */
	void setAngle(double theta);
	/**
	 *
	 * @return
	 */
	double getCoefficientOfDynamicFrictionSqrt();
	/**
	 * Used by the collision class; since the coefficient of restitution for a collision
	 * is calculated using sqrt(r_1*r_2), it is more efficient for a PhysicsEntity to
	 * remember the square root of its restitution since sqrt(r_1*r_2)=sqrt(r_1)*sqrt(r_2)
	 *
	 * @return The square root of the coefficient of restitution
	 */
	double getCoefficientOfRestitutionSqrt();
	/**
	 *
	 * @return
	 */
	double getCoefficientOfStaticFrictionSqrt();
	/**
	 * @return the mass of this PhysicsEntity
	 */
	double getMass();
	/**
	 * @param newMass
	 * 		the new mass of this PhysicsEntity
	 */
	void setMass(double newMass);
	/**
	 *
	 * @return
	 */
	double getMomentOfInertia();
	/**
	 *
	 * @return
	 */
	double getRotationalVelocity();
	/**
	 *
	 * @param f
	 */
	void setRotationalVelocity(double f);
	/**
	 * Guaranteed to completely contain the PhysicsEntity.
	 *
	 * @return The shape of this PhysicsEntity
	 */
	Collidable getShape();
	/**
	 *
	 * @return
	 */
	Vector getVelocity();
	/**
	 *
	 * @param newVelocity
	 */
	void setVelocity(Dimensional newVelocity);
	/**
	 * @param collision
	 * @param reactionType
	 */
	void react(PhysCollision collision, ReactionType reactionType);
	/**
	 * Adds to the current angle of this PhysicsEntity
	 *
	 * @param plusTheta
	 * 		How much to add to the current angle of this PhysicsEntity
	 */
	void rotate(double plusTheta);

	/**
	 *
	 */
	public static interface PhysCollision extends Collidable.Collision
	{
		/**
		 *
		 * @return
		 */
		Vector getImpulse();
		/**
		 *
		 * @return
		 */
		PhysicsEntity getOther();
		/**
		 *
		 * @return
		 */
		Vector getSlidingFrictionalImpulse();
		/**
		 *
		 * @return
		 */
		@Override
		PhysCollision inverse();
	}

	/**
	 *
	 */
	public enum ReactionType
	{
		OVERLAP_ONLY, IMPULSE_ONLY, FRICTION_ONLY, FRICTION_AND_IMPULSE
	}
}
