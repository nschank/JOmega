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
 * Last updated on 2 Jun 2014
 *
 * A {@code PhysicsEntity} is one that is expected to interact in a 2D physical world (aka a {@code Universe}) and, as
 * such, it must know how to interact with forces, impulses, and collisions with other objects. A Collision with another
 * PhysicsEntity should cause this object to be moved away using the MTV and, depending on the {@code ReactionType},
 * possibly reacting using impulses and friction.
 *
 * @author nschank, Brown University
 * @version 3.2
 */
public interface PhysicsEntity extends Entity, Drawable
{
	/**
	 * Applies the given 2D {@code force} (as a {@code Vector}) at a particular {@code position} on the object. Should
	 * update this tick's force and torque components, to be applied upon the next tick.
	 * @param force
	 *		A force, represented by a {@code Vector}
	 * @param position
	 *		The location at which the force must be applied, as a {@code Dimensional}. Generally, should be inside this
	 *		{@code PhysicsEntity}.
	 */
	void applyForceAt(Vector force, Dimensional position);
	/**
	 * Applies the given 2D {@code impulse}, as a {@code Vector}, at a particular {@code position} on the object. Should
	 * update this tick's impulse and rotational-impulse components, to be applied upon the next tick.
	 * @param impulse
	 * 		An impulse, represented by a {@code Vector}
	 * @param position
	 * 		The location at which this impulse must be applied, as a {@code Dimensional}. Generally, should be inside
	 * 		this {@code PhysicsEntity}.
	 */
	void applyImpulseAt(Vector impulse, Dimensional position);
	/**
	 * Apples the given location change to this {@code PhysicsEntity}.
	 * @param change
	 * 		A {@code Vector} of movement to apply tho this {@code PhysicsEntity}.
	 */
	void applyLocationChange(Vector change);
	/**
	 * Stops all motion of any kind. This includes any derivative of position over time.
	 */
	void arrestMotion();
	/**
	 * Used for raycasting with this {@code PhysicsEntity}'s shape
	 *
	 * @see nschank.engn.shape.collide.Ray
	 * @param ray
	 * 		A {@code Ray} which may be colliding with this {@code PhysicsEntity}
	 * @return Either the distance from the ray's point to its collision with this {@code PhysicsEntity}, or
	 * 		{@code Optional.absent()}
	 */
	Optional<Double> collisionWith(Ray ray);
	/**
	 * Whether or not this {@code PhysicsEntity} is colliding with another, given {@code PhysicsEntity}
	 *
	 * @param physicsEntity
	 * 		Another {@code PhysicsEntity} which may be colliding with this one
	 *
	 * @return The {@code PhysCollision} between this {@code PhysicsEntity} and another, if they are colliding. Otherwise,
	 * 		returns {@code Optional.absent()}
	 */
	Optional<PhysCollision> collisionWith(PhysicsEntity physicsEntity);
	/**
	 * @return The current angle of this {@code PhysCollision}, in radians, relative to the x axis
	 */
	double getAngle();
	/**
	 * Changes the angle of this {@code PhysCollision} relative to the x-axis, in radians.
	 *
	 * @param theta
	 * 		An angle from the x-axis, in radians
	 */
	void setAngle(double theta);
	/**
	 * Used by the {@code PhysCollision} class. TODO
	 *
	 * @return The square root of the coefficient of dynamic friction.
	 */
	double getCoefficientOfDynamicFrictionSqrt();
	/**
	 * Used by the {@code PhysCollision} class; since the coefficient of restitution for a collision is calculated using
	 * {@code sqrt(r_1*r_2)}, it is more efficient for a {@code PhysicsEntity} to remember the square root of its
	 * restitution since {@code sqrt(r_1*r_2)=sqrt(r_1)*sqrt(r_2)}.
	 *
	 * @return The square root of the coefficient of restitution
	 */
	double getCoefficientOfRestitutionSqrt();
	/**
	 * Used by the {@code PhysCollision} class. TODO
	 * @return The square root of the coefficient of static friction.
	 */
	double getCoefficientOfStaticFrictionSqrt();
	/**
	 * @return The mass of this {@code PhysicsEntity}
	 */
	double getMass();
	/**
	 * Sets the mass of this {@code PhysicsEntity}
	 * @param newMass
	 * 		A new mass for this {@code PhysicsEntity}
	 */
	void setMass(double newMass);
	/**
	 * The mass moment of inertia of this {@code PhysicsEntity}, calculated by multiplying the moment of inertia of the
	 * {@code PhysicsEntity}'s shape, and its mass.
	 * @return The moment of inertia of this {@code PhysicsEntity}
	 */
	double getMomentOfInertia();
	/**
	 * TODO: counterclockwise, I think
	 * @return The current rotational velocity of this {@code PhysicsEntity}, in radians per second
	 */
	double getRotationalVelocity();
	/**
	 * Sets the rotational velocity of this {@code PhysicsEntity}
	 * @param f
	 * 		A rotational velocity, in radians per second
	 */
	void setRotationalVelocity(double f);
	/**
	 * The shape of this {@code PhysicsEntity}. Guaranteed to entirely contain this {@code PhysicsEntity} minimally.
	 *
	 * @return The shape of this {@code PhysicsEntity}
	 */
	Collidable getShape();
	/**
	 * @return The velocity of this {@code PhysicsEntity}, as a {@code Vector} representing both direction and magnitude
	 */
	Vector getVelocity();
	/**
	 * Changes the velocity of this {@code PhysicsEntity}
	 * @param newVelocity
	 * 		A {@code Dimensional} representing the new velocity of this {@code PhysicsEntity}
	 */
	void setVelocity(Dimensional newVelocity);
	/**
	 * Whenever a {@code PhysCollision} is created by {@code collisionWith(PhysicsEntity)}, this method causes the overlap
	 * between this {@code PhysicsEntity} and another to be undone using the MTV. Depending on {@code reactionType},
	 * this method may also enforce friction, impulse, or both.
	 * @param collision
	 * 		A {@code PhysCollision} between this {@code PhysicsEntity} and another {@code PhysicsEntity}
	 * @param reactionType
	 * 		A {@code ReactionType}, dealing with any of several types of reactions.
	 */
	void react(PhysCollision collision, ReactionType reactionType);
	/**
	 * Adds to the current angle of this {@code PhysicsEntity}
	 * TODO counterclockwise?
	 * @param plusTheta
	 * 		How much to add to the current angle of this {@code PhysicsEntity}
	 */
	void rotate(double plusTheta);

	/**
	 * Created by Nicolas Schank for package nschank.engn.play.phys
	 * Created on 21 Oct 2013
	 * Last updated on 2 Jun 2014
	 *
	 * An extension of the {@code Collision} class, intended for {@code PhysicsEntity}'s. Includes important aspects
	 * necessary for accurate collision response on top of those already included in the {@code Collision} class:
	 * namely, impulse and frictional impulse.
	 *
	 * @author nschank, Brown University
	 * @version 2.2
	 */
	public static interface PhysCollision extends Collidable.Collision
	{
		/**
		 * The impulse that should be added to the primary {@code PhysicsEntity} in the collision.
		 * @return A {@code Vector} representing the impulse of this collision
		 */
		Vector getImpulse();
		/**
		 * @return The other {@code PhysicsEntity} involved in this collision.
		 */
		PhysicsEntity getOther();
		/**
		 * @return The impulse that should be added to the primary {@code PhysicsEntity} in the collision due to friction.
		 */
		Vector getSlidingFrictionalImpulse();
		/**
		 * @return This {@code PhysCollision} from the perspective of {@code getOther()}.
		 */
		@Override
		PhysCollision inverse();
	}

	/**
	 * Created by Nicolas Schank for package nschank.engn.play.phys
	 * Created on 30 May 2014
	 * Last updated on 2 Jun 2014
	 *
	 * The way in which a collision should affect a {@code PhysicsEntity}. The same type of reaction should occur on both
	 * sides of a collision; in other words.
	 *
	 * @author nschank, Brown University
	 * @version 1.1
	 */
	public enum ReactionType
	{
		/**
		 * Collisions do not result in a momentum change of any kind, and the collision does not result in any friction.
		 * Collisions only result in objects no longer overlapping.
		 */
		OVERLAP_ONLY,
		/**
		 * Collisions result in the objects no longer overlapping, and momentum change is applied in a natural way.
		 */
		IMPULSE_ONLY,
		/**
		 * Collisions result in the objects no longer overlapping, and friction reacts, but impulse does not. Not a very
		 * useful reaction type.
		 */
		FRICTION_ONLY,
		/**
		 * As close to realistic reactions as possible. Overlaps, impulses, and friction are all applied.
		 */
		FRICTION_AND_IMPULSE
	}
}
