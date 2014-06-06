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
 * {@code PhysicsEntity}s have certain default properties, {@code Output}s, and {@code Input}s that must be honoured
 * internally, in addition to those from {@code Entity}
 *
 * The following properties (marked with a starting exclamation mark) must be immutable and follow the given definitions:
 * - !self 		->	This {@code Entity}
 * - !universe 	-> 	The {@code Universe} in which this {@code Entity} lives.
 *
 * The following {@code Input}s must not be replaceable and must take the following actions:
 * - !addToBoundary		->	Adds this {@code PhysicsEntity} to the boundary given by {@code group}
 * - !addToCollisionGroup->	Adds this {@code PhysicsEntity} to the collision group given by {@code group}
 * - !addToForceGroup	->	Adds this {@code PhysicsEntity} to the force group given by {@code group}
 * - !addToRayGroup		->	Adds this {@code PhysicsEntity} to the ray group given by {@code group}
 * - !doApplyForce		->  Applies the force given by {@code force}, as a {@code Dimensional}, at the point given by
 * {@code position}, as a {@code Dimensional}. {@code position} is {@code :centerPosition} by
 * default.
 * - !doApplyImpulse	->  Applies the impulse given by {@code impulse}, as a {@code Dimensional}, at the point given by
 * {@code position}, as a {@code Dimensional}. {@code position} is {@code :centerPosition} by
 * default.
 * - !doPhysicalRemove	->	Removes this {@code PhysicsEntity} from its {@code Universe}. Automatically called by
 * {@code onRemove}.
 * - !doRemove			->	Removes this {@code Entity} from its {@code Universe}.
 * - !doRotate			-> 	Rotates this {@code PhysicsEntity} the number of radians given by {@code theta}.
 * - !errorCheckPrint	->	Used for error checking. Prints every argument/value pair that was input to it.
 * - !removeFromBoundary->	Removes this {@code PhysicsEntity} from the boundary given by {@code group}
 * - !removeFromCollisionGroup
 * ->	Removes this {@code PhysicsEntity} from the collision group given by {@code group}
 * - !removeFromForceGroup
 * ->	Removes this {@code PhysicsEntity} from the force group given by {@code group}
 * - !removeFromRayGroup->	Removes this {@code PhysicsEntity} from the ray group given by {@code group}
 * - !removeProperty	->	For the String value of the argument name "property", remove this {@code Entity}'s property
 * of that name.
 * - !runOutput			->	For the String value of the argument name "target", runs this {@code Entity}'s output of that
 * name. All arguments besides "target" should be passed along to that output.
 * - !setProperty		->	For every argument name/value pair, set this {@code Entity}'s property of that name to that
 * value.
 *
 * The following {@code Output}s must be used in the following manners, with the following arguments:
 * - !onDraw			->	Must be called by the draw(Graphics2D) method, with no arguments. Should not (and literally
 * cannot) attempt to draw anything directly.
 * - !onTick			->	Must be called by the onTick(long) method, with one argument: nanosSinceLastTick->that long.
 *
 * @author nschank, Brown University
 * @version 3.2
 * @see nschank.engn.play.Entity
 */
public interface PhysicsEntity extends Entity, Drawable
{
	/**
	 * Applies the given 2D {@code force} (as a {@code Vector}) at a particular {@code position} on the object. Should
	 * update this tick's force and torque components, to be applied upon the next tick.
	 *
	 * @param force
	 * 		A force, represented by a {@code Vector}
	 * @param position
	 * 		The location at which the force must be applied, as a {@code Dimensional}. Generally, should be inside this
	 * 		{@code PhysicsEntity}.
	 */
	void applyForceAt(Vector force, Dimensional position);
	/**
	 * Applies the given 2D {@code impulse}, as a {@code Vector}, at a particular {@code position} on the object. Should
	 * update this tick's impulse and rotational-impulse components, to be applied upon the next tick.
	 *
	 * @param impulse
	 * 		An impulse, represented by a {@code Vector}
	 * @param position
	 * 		The location at which this impulse must be applied, as a {@code Dimensional}. Generally, should be inside
	 * 		this {@code PhysicsEntity}.
	 */
	void applyImpulseAt(Vector impulse, Dimensional position);
	/**
	 * Apples the given location change to this {@code PhysicsEntity}.
	 *
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
	 * @param ray
	 * 		A {@code Ray} which may be colliding with this {@code PhysicsEntity}
	 *
	 * @return Either the distance from the ray's point to its collision with this {@code PhysicsEntity}, or
	 * {@code Optional.absent()}
	 *
	 * @see nschank.engn.shape.collide.Ray
	 */
	Optional<Double> collisionWith(Ray ray);
	/**
	 * Whether or not this {@code PhysicsEntity} is colliding with another, given {@code PhysicsEntity}
	 *
	 * @param physicsEntity
	 * 		Another {@code PhysicsEntity} which may be colliding with this one
	 *
	 * @return The {@code PhysCollision} between this {@code PhysicsEntity} and another, if they are colliding. Otherwise,
	 * returns {@code Optional.absent()}
	 */
	Optional<PhysCollision> collisionWith(PhysicsEntity physicsEntity);
	/**
	 * @return The current angle of this {@code PhysCollision}, in radians, relative to the x axis
	 */
	double getAngle();
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
	 *
	 * @return The square root of the coefficient of static friction.
	 */
	double getCoefficientOfStaticFrictionSqrt();
	/**
	 * @return The mass of this {@code PhysicsEntity}
	 */
	double getMass();
	/**
	 * The mass moment of inertia of this {@code PhysicsEntity}, calculated by multiplying the moment of inertia of the
	 * {@code PhysicsEntity}'s shape, and its mass.
	 *
	 * @return The moment of inertia of this {@code PhysicsEntity}
	 */
	double getMomentOfInertia();
	/**
	 * TODO: counterclockwise, I think
	 *
	 * @return The current rotational velocity of this {@code PhysicsEntity}, in radians per second
	 */
	double getRotationalVelocity();
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
	 * Whenever a {@code PhysCollision} is created by {@code collisionWith(PhysicsEntity)}, this method causes the overlap
	 * between this {@code PhysicsEntity} and another to be undone using the MTV. Depending on {@code reactionType},
	 * this method may also enforce friction, impulse, or both.
	 *
	 * @param collision
	 * 		A {@code PhysCollision} between this {@code PhysicsEntity} and another {@code PhysicsEntity}
	 * @param reactionType
	 * 		A {@code ReactionType}, dealing with any of several types of reactions.
	 */
	void react(PhysCollision collision, ReactionType reactionType);
	/**
	 * Adds to the current angle of this {@code PhysicsEntity}
	 * TODO counterclockwise?
	 *
	 * @param plusTheta
	 * 		How much to add to the current angle of this {@code PhysicsEntity}
	 */
	void rotate(double plusTheta);
	/**
	 * Changes the angle of this {@code PhysCollision} relative to the x-axis, in radians.
	 *
	 * @param theta
	 * 		An angle from the x-axis, in radians
	 */
	void setAngle(double theta);
	/**
	 * Sets the mass of this {@code PhysicsEntity}
	 *
	 * @param newMass
	 * 		A new mass for this {@code PhysicsEntity}
	 */
	void setMass(double newMass);
	/**
	 * Sets the rotational velocity of this {@code PhysicsEntity}
	 *
	 * @param f
	 * 		A rotational velocity, in radians per second
	 */
	void setRotationalVelocity(double f);
	/**
	 * Changes the velocity of this {@code PhysicsEntity}
	 *
	 * @param newVelocity
	 * 		A {@code Dimensional} representing the new velocity of this {@code PhysicsEntity}
	 */
	void setVelocity(Dimensional newVelocity);

	/**
	 * Attaches the value of {@code ofValue} to the name {@code ofName} within this {@code Entity}. The value {@code null}
	 * must be identical to removing the property. The following properties are internally set and calling putProperty
	 * with them should have no effect:
	 * - !momentOfInertia	-> 	Should always refer to the moment of inertia of this {@code PhysicsEntity}. Should change
	 * only if :shape changes
	 * - !self 				-> 	Should always refer to this {@code Entity}
	 * - !universe 			-> 	Should always refer to the {@code Universe} in which this {@code Entity} resides
	 *
	 * The following properties are settable, and setting them will have an effect internally. They are marked by starting
	 * with a colon; in order to prevent confusion, any property starting with a colon that does not correspond to an
	 * internal property will throw an error.
	 * - :angle				-> 	double; identical to :rotation
	 * - :animated			->	boolean; whether :sprite should be ticked as an AnimatedSprite
	 * - :boundaryGroups	->	List<Double>; sets which boundary groups this object is contained by
	 * - :centerPosition	->	Dimensional; sets the center position of :shape
	 * - :coefficientOfDynamicFrictionSqrt
	 * -> 	double; sets the coefficient of dynamic friction of this {@code PhysicsEntity}. A required
	 * property of {@code PhysicsEntity}s
	 * - :coefficientOfRestitutionSqrt
	 * ->	double; sets the coefficient of restitution of this {@code PhysicsEntity}. A required
	 * property of {@code PhysicsEntity}s
	 * - :coefficientOfStaticFrictionSqrt
	 * ->	double; sets the coefficient of static friction of this {@code PhysicsEntity}. A required
	 * property of {@code PhysicsEntity}s
	 * - :collisionGroups	->	List<Double>; sets which collision groups this object is contained by
	 * - :color				->	Color; sets the color of :shape
	 * - :deriv{number}		->	Dimensional; where {number} is an integer greater than {@value 0}. Sets that derivative
	 * of position.
	 * - :forceGroups		->	List<Double>; sets which force groups this object is contained by
	 * - :hasSprite			-> 	boolean; referring to whether this PhysicsEntity has a :sprite property, which will be
	 * drawn instead of :shape on each draw tick
	 * - :height			->	double; sets the height of :shape
	 * - :mass				->	double; sets the mass of this {@code PhysicsEntity}. A required property of
	 * {@code PhysicsEntity}s
	 * - :rayGroups			->	List<Double>; sets which ray groups this object is contained by
	 * - :rderiv{number}	->	double; where {number} is an integer greater than {@value 0}. Sets that derivative of
	 * rotation.
	 * - :rotation			->	double; sets the angle of :shape
	 * - :shape				->	Collidable; set the shape of this {@code PhysicsEntity}. This shape will be drawn if
	 * :hasSprite is not present or is false. A required property of {@code PhysicsEntity}s.
	 * - :sprite			->	Sprite; a sprite to draw on each draw tick, instead of :shape
	 * - :velocity			-> 	Dimensional; sets the velocity of this {@code PhysicsEntity}
	 * - :width				->	double; sets the width of :shape
	 *
	 * @param ofName
	 * 		The name of a property
	 * @param ofValue
	 * 		The value to set to that property
	 */
	@Override
	void putProperty(String ofName, Object ofValue);
	/**
	 * Returns the value of the property of a given name. Should return {@code null}, if that property is not set. Certain
	 * properties should always return particular things, having been internally set:
	 * - !momentOfInertia	->	The moment of inertia of :shape
	 * - !self 				-> 	This {@code Entity}
	 * - !universe 			-> 	The {@code Universe} in which this {@code Entity} resides
	 *
	 * The following properties can be used to get internal aspects of this {@code PhysicsEntity}. They are marked by
	 * starting with a colon; in order to prevent confusion, any property starting with a colon that does not correspond
	 * to an internal property will throw an error.
	 * - :angle				-> 	double; identical to :rotation
	 * - :animated			->	boolean; whether :sprite should be ticked as an AnimatedSprite
	 * - :boundaryGroups	->	List<Double>; which boundary groups this object is contained by
	 * - :centerPosition	->	Dimensional; the center position of :shape
	 * - :coefficientOfDynamicFrictionSqrt
	 * -> 	double; the coefficient of dynamic friction of this {@code PhysicsEntity}
	 * - :coefficientOfRestitutionSqrt
	 * ->	double; the coefficient of restitution of this {@code PhysicsEntity}
	 * - :coefficientOfStaticFrictionSqrt
	 * ->	double; the coefficient of static friction of this {@code PhysicsEntity}
	 * - :collisionGroups	->	List<Double>; which collision groups this object is contained by
	 * - :color				->	Color; the color of :shape
	 * - :deriv{number}		->	Dimensional; where {number} is an integer greater than {@value 0}. That derivative of
	 * position.
	 * - :forceGroups		->	List<Double>; which force groups this object is contained by
	 * - :hasSprite			-> 	boolean; whether this PhysicsEntity has a :sprite property, which will be
	 * drawn instead of :shape on each draw tick
	 * - :height			->	double; the height of :shape
	 * - :mass				->	double; the mass of this {@code PhysicsEntity}
	 * - :rayGroups			->	List<Double>; which ray groups this object is contained by
	 * - :rderiv{number}	->	double; where {number} is an integer greater than {@value 0}. That derivative of
	 * rotation.
	 * - :rotation			->	double; the angle of :shape, in radians from the x axis
	 * - :shape				->	Collidable; the shape of this {@code PhysicsEntity}. This shape will be drawn if
	 * :hasSprite is not present or is {@literal false}.
	 * - :sprite			->	Sprite; a sprite to draw on each draw tick, instead of :shape
	 * - :velocity			-> 	Dimensional; the velocity of this {@code PhysicsEntity}
	 * - :width				->	double; the width of :shape
	 *
	 * @param ofName
	 * 		The name of a property
	 *
	 * @return The value of the property named {@code ofName}
	 */
	@Override
	Object getProperty(String ofName);

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
		 *
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
