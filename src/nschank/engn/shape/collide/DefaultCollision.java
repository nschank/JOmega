package nschank.engn.shape.collide;

import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
import nschank.note.Immutable;


/**
 * Created by Nicolas Schank for package nschank.engn.shape.collide
 * Created on 27 Oct 2013
 * Last updated on 27 May 2014
 *
 * The default implementation of the Collidable.Collision interface. Simply takes in a collision point and a minimum
 * translation vector and returns them identically. inverse() returns another DefaultCollision which returns the same
 * collision point, the negation of that minimum translation vector, and returns the original DefaultCollision as its
 * inverse.
 *
 * Does not override equality or hashCode, because DefaultCollisions are not equal if they are not identical.
 *
 * @author Nicolas Schank
 * @version 1.3
 */
@Immutable
public class DefaultCollision implements Collidable.Collision
{
	private final Dimensional point;
	private final Vector mtv;

	/**
	 * Creates a Collision which occurs at the given {@code point} and can by corrected by the given {@code mtv}.
	 *
	 * @param point
	 * 		The point at which this Collision occurs
	 * @param mtv
	 * 		The MTV which will correct this Collision
	 */
	public DefaultCollision(Dimensional point, Vector mtv)
	{
		if(point.getDimensions() != 2)
			throw new IllegalArgumentException("Collision point must happen in 2 dimensions.");
		if(mtv.getDimensions() != 2)
			throw new IllegalArgumentException("Minimum translation vectors must happen in 2 dimensions.");

		this.point = point;
		this.mtv = mtv;
	}

	/**
	 * @return The 2-dimensional point at which this Collision occurred
	 */
	@Override
	public Dimensional getCollisionPoint()
	{
		return this.point;
	}

	/**
	 * @return A 2-dimensional Vector which will negate this Collision
	 */
	@Override
	public Vector getMTV()
	{
		return this.mtv;
	}

	/**
	 * Creates another DefaultCollision, which will have the same collision point but will have the negated MTV. The inverse
	 * of that DefaultCollision will be the original DefaultCollision, by memory address.
	 *
	 * @return The inverse of this DefaultCollision
	 */
	@Override
	public Collidable.Collision inverse()
	{
		final Collidable.Collision inverseOfInverse = this;
		return new DefaultCollision(this.point, this.mtv.smult(-1))
		{
			@Override
			public Collidable.Collision inverse()
			{
				return inverseOfInverse;
			}
		};
	}

	/**
	 * @return A string representation of a DefaultCollision, containing a point and an MTV
	 */
	@Override
	public String toString()
	{
		return "DefaultCollision{" +
				"point=" + this.point +
				", mtv=" + this.mtv +
				'}';
	}
}
