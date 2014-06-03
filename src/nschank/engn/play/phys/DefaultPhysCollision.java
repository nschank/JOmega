package nschank.engn.play.phys;

import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
import nschank.engn.shape.collide.Collidable;
import nschank.note.Immutable;


/**
 * Created by Nicolas Schank for package nschank.engn.play.phys
 * Created on 28 Oct 2013
 * Last updated on 3 Jun 2014
 *
 * An immutable implementation of {@code PhysCollision}, in which the impulses and frictional impulse are only calculated
 * if their getters are called.
 *
 * @author nschank, Brown University
 * @version 1.10
 */
@Immutable
class DefaultPhysCollision implements PhysicsEntity.PhysCollision
{
	public static final double MAX_STATIC_VELOCITY = 0.05;

	private final PhysicsEntity collider;
	private final PhysicsEntity obstacle;
	private final Collidable.Collision collision;
	private Optional<Vector> impulse;
	private Optional<Vector> friction;

	/**
	 * Creates a PhysCollision
	 *
	 * @param collider
	 * 		The {@code PhysicsEntity} that is colliding
	 * @param obstacle
	 * 		The {@code PhysicsEntity} being collided with
	 * @param collision
	 * 		The {@code Collision} between them, as returned by {@code collisionWith(PhysicsEntity)}
	 */
	DefaultPhysCollision(PhysicsEntity collider, PhysicsEntity obstacle, Collidable.Collision collision)
	{
		this.collider = collider;
		this.obstacle = obstacle;
		this.collision = collision;
		this.impulse = Optional.absent();
		this.friction = Optional.absent();
	}

	/**
	 * @return The MTV for the {@code collider} to exit the {@code obstacle}
	 */
	@Override
	public Vector getMTV()
	{
		double massA = this.collider.getMass();
		double massB = this.obstacle.getMass();
		return this.collision.getMTV().smult(1.0 - (massA / (massA + massB)));
	}

	/**
	 * @return The collision point between the two {@code PhysicsEntity}s
	 */
	@Override
	public Dimensional getCollisionPoint()
	{
		return this.collision.getCollisionPoint();
	}

	/**
	 * First time, calculates the sliding frictional impulse between the two {@code PhysicsEntity}s; then stores it for
	 * later use.
	 *
	 * @return The sliding frictional impulse of these two {@code PhysicsEntity}s
	 */
	@Override
	public Vector getSlidingFrictionalImpulse()
	{
		if(this.friction.isPresent()) return this.friction.get();

		double impulseMagnitude = this.getImpulse().mag();

		Vector mtv = this.collision.getMTV().normalized();
		Vector axis = new Vector(-mtv.getCoordinate(1), mtv.getCoordinate(0));

		axis = axis.smult(Math.signum(axis.getCoordinate(0)));

		Vector obstacleVelocity = this.obstacle.getVelocity();
		Vector colliderVelocity = this.collider.getVelocity();

		double relativeVelocity = obstacleVelocity.dotProduct(axis) - colliderVelocity.dotProduct(axis);
		double frictionalDirection = Math.signum(relativeVelocity);

		boolean isStatic = (relativeVelocity <= MAX_STATIC_VELOCITY);
		double COF;
		if(isStatic) COF = this.obstacle.getCoefficientOfStaticFrictionSqrt() * this.collider
				.getCoefficientOfStaticFrictionSqrt();
		else COF = this.obstacle.getCoefficientOfDynamicFrictionSqrt() * this.collider
				.getCoefficientOfDynamicFrictionSqrt();
		double magnitude = COF * impulseMagnitude * frictionalDirection;

		this.friction = Optional.of(axis.smult(magnitude));
		return this.friction.get();
	}

	/**
	 * @return This {@code PhysCollision} but in the opposite direction
	 */
	@Override
	public PhysicsEntity.PhysCollision inverse()
	{
		return new DefaultPhysCollision(this.obstacle, this.collider, this.collision.inverse());
	}

	/**
	 * @return The {@code obstacle}
	 */
	@Override
	public PhysicsEntity getOther()
	{
		return this.obstacle;
	}

	/**
	 * Calculates and returns the impulse to apply to the {@code collider}
	 *
	 * @return The impulse to apply to {@code collider}
	 */
	@Override
	public Vector getImpulse()
	{
		if(this.impulse.isPresent()) return this.impulse.get();

		Vector obstacleVelocity = this.obstacle.getVelocity();
		Vector colliderVelocity = this.collider.getVelocity();

		Vector projectedObstacleVelocity = obstacleVelocity.projectOntoLine(Vector.ZERO_2D, this.collision.getMTV());
		Vector projectedColliderVelocity = colliderVelocity.projectOntoLine(Vector.ZERO_2D, this.collision.getMTV());

		Vector unweightedVelocityChange = projectedObstacleVelocity.minus(projectedColliderVelocity);

		double colliderMass = this.collider.getMass();
		double obstacleMass = this.obstacle.getMass();

		double multipliedMass = colliderMass * obstacleMass;
		double massSum = colliderMass + obstacleMass;

		double colliderCOR = this.collider.getCoefficientOfRestitutionSqrt();
		double obstacleCOR = this.obstacle.getCoefficientOfRestitutionSqrt();

		double COR = 1.0 + (colliderCOR * obstacleCOR);

		Vector colliderCentroidToCollision = new Vector(this.collision.getCollisionPoint())
				.minus(this.collider.getCenterPosition());
		Vector obstacleCentroidToCollision = new Vector(this.collision.getCollisionPoint())
				.minus(this.obstacle.getCenterPosition());

		Vector normalizedMTV = this.collision.getMTV().normalized();

		Vector colliderPerp = new Vector(-colliderCentroidToCollision.getCoordinate(1),
				colliderCentroidToCollision.getCoordinate(0));
		Vector obstaclePerp = new Vector(-obstacleCentroidToCollision.getCoordinate(1),
				obstacleCentroidToCollision.getCoordinate(0));

		double rotationA = 0.0;
		double rotationB = 0.0;

		if(this.collider.getMomentOfInertia() > 0)
			rotationA = (colliderPerp.dotProduct(normalizedMTV)) * (colliderPerp.dotProduct(normalizedMTV))
					/ this.collider.getMomentOfInertia();
		if(this.obstacle.getMomentOfInertia() > 0)
			rotationB = (obstaclePerp.dotProduct(normalizedMTV)) * (obstaclePerp.dotProduct(normalizedMTV))
					/ this.obstacle.getMomentOfInertia();

		double weight = (multipliedMass * COR) / (massSum + rotationA + rotationB);

		this.impulse = Optional.of(unweightedVelocityChange.smult(weight));
		return this.impulse.get();
	}

	/**
	 * @return A string representation of this Object
	 */
	@Override
	public String toString()
	{
		return this.collider + " is colliding with " + this.obstacle + " at location " + this.collision
				.getCollisionPoint() + ": applying " + this.collision.getMTV();
	}
}
