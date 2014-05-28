package nschank.engn.shape.collide;

import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
import nschank.util.Interval;
import nschank.util.NLists;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;


/**
 * Created by Nicolas Schank for package nschank.engn.shape.collide
 * Created on 28 Sep 2013
 * Last updated on 27 May 2014
 *
 *
 *
 * @author nschank, Brown University
 * @version 3.5
 */
public class AAB extends Polygon
{
	/**
	 * @param location
	 * @param width
	 * @param height
	 * @param c
	 */
	public AAB(Dimensional location, double width, double height, Color c)
	{
		super(location, width, height, c, new Vector(-1, -1), new Vector(1, -1), new Vector(1, 1), new Vector(-1, 1));
	}

	/**
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 * @param c
	 */
	public AAB(double left, double right, double top, double bottom, Color c)
	{
		super(new Vector(left + ((right - left) / 2d), top + ((bottom - top) / 2d)), right - left, bottom - top, c, new Vector(-1, -1), new Vector(1, -1), new Vector(1, 1), new Vector(-1, 1));
	}

	/**
	 * @return
	 */
	@Override
	protected List<? extends Dimensional> axes()
	{
		return NLists.of(new Vector(0, 1), new Vector(1, 0));
	}

	/**
	 *
	 * @param other
	 * 		Another object which may be colliding with this one.
	 *
	 * @return
	 */
	@Override
	public Optional<Collision> collisionWith(Collidable other)
	{
		return Collidables.inverseOf(other.collisionWithAAB(this));
	}

	/**
	 *
	 * @param other
	 * 		An AAB that may be colliding with this object
	 *
	 * @return
	 */
	@Override
	public Optional<Collision> collisionWithAAB(AAB other)
	{
		if(!this.xInterval().isIntersecting(other.xInterval()) || !this.yInterval().isIntersecting(other.yInterval()))
			return Optional.absent();
		else return super.collisionWithAAB(other);
	}

	/**
	 *
	 * @param other
	 * 		An Circle that may be colliding with this object
	 *
	 * @return
	 */
	@Override
	public Optional<Collision> collisionWithCircle(Circle other)
	{
		return Collidables.inverseOf(other.collisionWithAAB(this));
	}

	/**
	 *
	 * @param other
	 * 		An Point that may be colliding with this object
	 *
	 * @return
	 */
	@Override
	public Optional<Collision> collisionWithPoint(Point other)
	{
		return Collidables.inverseOf(other.collisionWithAAB(this));
	}

	/**
	 *
	 * @param other
	 *
	 * @return
	 */
	@Override
	public boolean contains(Dimensional other)
	{
		return Interval.within(this.getMinX(), this.getMaxX(), other.getCoordinate(0)) && Interval.within(this.getMinY(), this.getMaxY(), other.getCoordinate(1));
	}

	/**
	 * @return
	 */
	@Override
	public Collidable copy()
	{
		return new AAB(this.getCenterPosition(), this.getWidth(), this.getHeight(), this.getColor());
	}

	/**
	 * @param r
	 * 		A Ray in the same x-y coordinate plane as this object, which may be pointed to this object
	 *
	 * @return
	 */
	@Override
	public Optional<Double> distanceAlong(Ray r)
	{
		double shortestCollision = -1;

		for(int i = -1; i < (this.points.size() - 1); i++)
		{
			Vector start = new Vector(this.points.get((this.points.size() + i) % this.points.size()));
			Vector end = new Vector(this.points.get((this.points.size() + i + 1) % this.points.size()));
			Vector perp = new Vector(start.minus(end).getCoordinate(1), end.minus(start).getCoordinate(0));
			if((((start.minus(r.getStartLocation())).crossProduct(r.getDirection()).getCoordinate(2) * (end.minus(r.getStartLocation()).crossProduct(r.getDirection()).getCoordinate(2))) > 0) || (r.getDirection().dotProduct(perp) == 0))
				continue;

			double collision = end.minus(r.getStartLocation()).dotProduct(perp) / r.getDirection().dotProduct(perp);
			if((collision > 0) && ((shortestCollision < 0) || (shortestCollision > collision)))
				shortestCollision = collision;
		}

		if(shortestCollision < 0) return Optional.absent();
		else return Optional.of(shortestCollision);
	}

	/**
	 * @param g
	 */
	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(this.getColor());
		g.fillRect((int) (this.getCenterPosition().getCoordinate(0) - (this.getWidth() / 2f)), (int) (this.getCenterPosition().getCoordinate(1) - (this.getHeight() / 2f)), (int) this.getWidth(), (int) this.getHeight());
	}

	/**
	 *
	 * @return
	 */
	public double getMaxX()
	{
		return this.getCenterPosition().getCoordinate(0) + (this.getWidth() / 2);
	}

	/**
	 *
	 * @return
	 */
	public double getMaxY()
	{
		return this.getCenterPosition().getCoordinate(1) + (this.getHeight() / 2);
	}

	/**
	 *
	 * @return
	 */
	public double getMinX()
	{
		return this.getCenterPosition().getCoordinate(0) - (this.getWidth() / 2);
	}

	/**
	 *
	 * @return
	 */
	public double getMinY()
	{
		return this.getCenterPosition().getCoordinate(1) - (this.getHeight() / 2);
	}

	/**
	 * @return
	 */
	@Override
	public double getRotation()
	{
		return 0.0f;
	}

	/**
	 * @param f
	 */
	@Override
	public void setRotation(double f)
	{
		//Do nothing
	}

	/**
	 * @return
	 */
	@Override
	public double momentOfInertia()
	{
		return 0.0f;
	}

	/**
	 *
	 * @param f
	 */
	@Override
	public void rotate(double f)
	{
		//Do nothing
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString()
	{
		return "AAB={" + getCenterPosition() + " with width " + getWidth() + " and height " + getHeight() + '}';
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Interval xInterval()
	{
		return Interval.about(this.getCenterPosition().getCoordinate(0), this.getWidth());
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Interval yInterval()
	{
		return Interval.about(this.getCenterPosition().getCoordinate(1), this.getHeight());
	}
}
