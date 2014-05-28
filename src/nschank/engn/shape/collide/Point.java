package nschank.engn.shape.collide;

import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Created by Nicolas Schank for package nschank.engn.shape.collide
 * Created on 28 Sep 2013
 * Last updated on 27 May 2014
 *
 *
 *
 * @author nschank, Brown University
 * @version 2.8
 */
public final class Point extends Circle
{

	/**
	 * @param x
	 * @param y
	 */
	public Point(double x, double y)
	{
		this(new nschank.collect.dim.Point(x, y));
	}

	/**
	 * @param location
	 */
	public Point(Dimensional location)
	{
		super(location, 0, Color.BLACK);
	}

	/**
	 * @param other
	 * 		Another object which may be colliding with this one.
	 *
	 * @return
	 */
	@Override
	public Optional<Collision> collisionWith(Collidable other)
	{
		return inverseOf(other.collisionWithPoint(this));
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
		double x = this.getCenterPosition().getCoordinate(0);
		double y = this.getCenterPosition().getCoordinate(1);
		if((x < other.getMaxX()) && (x > other.getMinX()) && (y < other.getMaxY()) && (y > other.getMinY()))
		{
			Vector out = this.mtvFromAAB(other);
			Collision ret = new DefaultCollision(out.plus(this.getCenterPosition()), out);
			return Optional.of(ret);
		}
		return Optional.absent();
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
		Vector dir = new Vector(this.getCenterPosition()).minus(other.getCenterPosition());
		if(dir.mag2() < (((other.getWidth() / 2) + (other.getWidth() / 2)) * ((other.getWidth() / 2) + (other.getWidth() / 2))))
		{
			Vector out = this.mtvFromCircle(other);
			Collision ret = new DefaultCollision(out.plus(this.getCenterPosition()), out);
			return Optional.of(ret);
		}
		return Optional.absent();
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
		return Optional.absent();
	}

	/**
	 *
	 * @param other
	 * 		An Polygon that may be colliding with this object
	 *
	 * @return
	 */
	@Override
	public Optional<Collision> collisionWithPolygon(Polygon other)
	{
		return inverseOf(other.collisionWithPoint(this));
	}

	/**
	 *
	 * @return
	 */
	@Override
	public Collidable copy()
	{
		return new Point(this.getCenterPosition());
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
		Vector relativeCenter = new Vector(this.getCenterPosition()).minus(r.getStartLocation());
		double projection = relativeCenter.dotProduct(r.getDirection());
		Dimensional projectionPoint = r.getAtDistance(projection);
		if(projectionPoint.equals(this.getCenterPosition())) return Optional.of(projection);
		return Optional.absent();
	}

	/**
	 *
	 * @param g
	 */
	@Override
	public void draw(Graphics2D g)
	{

	}

	/**
	 *
	 * @return
	 */
	@Override
	public double getRotation()
	{
		return 0.0f;
	}

	/**
	 *
	 * @param theta
	 */
	@Override
	public void setRotation(double theta)
	{
		//Do nothing
	}

	/**
	 *
	 * @return
	 */
	@Override
	public double momentOfInertia()
	{
		return 0.0f;
	}

	/**
	 * @param other
	 *
	 * @return
	 */
	private Vector mtvFromAAB(AAB other)
	{
		double xDiff = Math.abs(other.getCenterPosition().getCoordinate(0) - this.getCenterPosition().getCoordinate(0));
		double yDiff = Math.abs(other.getCenterPosition().getCoordinate(1) - this.getCenterPosition().getCoordinate(1));

		double needXDiff = other.getWidth() / 2d;
		double needYDiff = other.getHeight() / 2d;

		if(Math.abs(xDiff - needXDiff) < Math.abs(yDiff - needYDiff))
		{
			boolean negate = other.getCenterPosition().getCoordinate(0) > this.getCenterPosition().getCoordinate(0);
			return new Vector((negate ? -1 : 1) * (needXDiff - xDiff), 0);
		} else
		{
			boolean negate = other.getCenterPosition().getCoordinate(1) > this.getCenterPosition().getCoordinate(1);
			return new Vector(0, (negate ? -1 : 1) * (needYDiff - yDiff));
		}
	}

	/**
	 * @param theta
	 */
	@Override
	public void rotate(double theta)
	{
		//Do nothing
	}

	/**
	 * @param c
	 */
	@Override
	public void setColor(Color c)
	{

	}

	/**
	 * @param f
	 */
	@Override
	public void setHeight(double f)
	{

	}

	/**
	 *
	 * @param f
	 */
	@Override
	public void setWidth(double f)
	{

	}
}
