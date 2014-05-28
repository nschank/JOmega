package nschank.engn.shape.collide;

import com.google.common.base.Optional;
import cs195n.Vec2f;
import nschank.collect.dim.Dimensional;
import nschank.collect.tuple.Pair;
import nschank.engn.shape.AbstractDrawable;

import java.awt.Color;


/**
 * Created by Nicolas Schank for package nschank.engn.shape.collide
 * Created on 27 Oct 2013
 * Last updated on 27 May 2014
 *
 * Omega
 *
 * @author nschank, Brown University
 * @version 1.2
 */
abstract class PCollidable extends AbstractDrawable implements Collidable
{
	/**
	 *
	 */
	protected PCollidable()
	{
		super();
	}

	/**
	 * @param location
	 * @param w
	 * @param h
	 * @param c
	 */
	protected PCollidable(Dimensional location, double w, double h, Color c)
	{
		super(location, w, h, c);
	}

	/**
	 * @param mtvInfo
	 * @param collidable
	 *
	 * @return
	 */
	abstract Optional<Collision> minimumCollisionFrom(Pair<Vec2f, Float> mtvInfo, PCollidable collidable);

	/**
	 * @param mtvInfo
	 * @param collidable
	 *
	 * @return
	 */
	abstract Optional<Collision> minimumCollisionFromPolygon(Pair<Vec2f, Float> mtvInfo, Polygon collidable);

	/**
	 * @param mtvInfo
	 * @param collidable
	 *
	 * @return
	 */
	abstract Optional<Collision> minimumCollisionFromCircle(Pair<Vec2f, Float> mtvInfo, Circle collidable);

	/**
	 * @param other
	 *
	 * @return
	 */
	protected static Optional<Collision> inverseOf(Optional<Collision> other)
	{
		if(other.isPresent()) return Optional.of(other.get().inverse());
		return other;
	}
}
