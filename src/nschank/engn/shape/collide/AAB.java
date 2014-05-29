package nschank.engn.shape.collide;

import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
import nschank.util.DefaultInterval;
import nschank.util.Interval;
import nschank.util.Intervals;
import nschank.util.NLists;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;


/**
 * Created by Nicolas Schank for package nschank.engn.shape.collide
 * Created on 28 Sep 2013
 * Last updated on 28 May 2014
 *
 * An Axis Aligned Box is a subset of Polygons satisfying two invariants: they are boxes (rectangles), and they are not
 * rotatable.
 *
 * @author nschank, Brown University
 * @version 4.1
 */
public class AAB extends Polygon
{
	/**
	 * Creates an AAB with the given center-point, the given width and height, and the given color.
	 *
	 * @param location
	 * 		The center point of this AAB
	 * @param width
	 * 		The width of this AAB
	 * @param height
	 * 		The height of this AAB
	 * @param c
	 * 		The Color of this AAB
	 */
	public AAB(Dimensional location, double width, double height, Color c)
	{
		super(location, width, height, c, new Vector(-1, -1), new Vector(1, -1), new Vector(1, 1), new Vector(-1, 1));
	}

	/**
	 * Creates an AAB with the given let, right, top, and bottom edges. The four points of this AAB will be:
	 * (left,top),(right,top),(right,bottom),(left,bottom)
	 *
	 * @param left
	 * 		The left border of the AAB
	 * @param right
	 * 		The right border of the AAB
	 * @param top
	 * 		The top border of the AAB
	 * @param bottom
	 * 		The bottom border of the AAB
	 * @param c
	 * 		The Color of this AAB
	 */
	public AAB(double left, double right, double top, double bottom, Color c)
	{
		super(new Vector(left + ((right - left) / 2d), top + ((bottom - top) / 2d)), right - left, bottom - top, c,
				new Vector(-1, -1), new Vector(1, -1), new Vector(1, 1), new Vector(-1, 1));
	}

	/**
	 * @return The two axes of this AAB: the x and y axes
	 */
	@Override
	protected List<? extends Dimensional> axes()
	{
		return NLists.of(new Vector(0, 1), new Vector(1, 0));
	}

	/**
	 * @param other
	 * 		Another object which may be colliding with this one.
	 *
	 * @return The Collision between this object and another object
	 */
	@Override
	public Optional<Collision> collisionWith(Collidable other)
	{
		return Collidables.inverseOf(other.collisionWithAAB(this));
	}

	/**
	 * @param other
	 * 		An AAB that may be colliding with this object
	 *
	 * @return The Collision between this object and another AAB
	 */
	@Override
	public Optional<Collision> collisionWithAAB(AAB other)
	{
		if(!this.xInterval().isIntersecting(other.xInterval()) || !this.yInterval().isIntersecting(other.yInterval()))
			return Optional.absent();
		else return super.collisionWithAAB(other);
	}

	/**
	 * @param other
	 * 		An Circle that may be colliding with this object
	 *
	 * @return The Collision between this object and a circle
	 */
	@Override
	public Optional<Collision> collisionWithCircle(Circle other)
	{
		return Collidables.inverseOf(other.collisionWithAAB(this));
	}

	/**
	 * @param other
	 * 		A point in the same plane as this AAB
	 *
	 * @return Whether ot not {@code other} is inside this AAB
	 */
	@Override
	public boolean contains(Dimensional other)
	{
		return Intervals.within(this.leftBorder(), this.rightBorder(), other.getCoordinate(0)) && Intervals
				.within(this.bottomBorder(), this.topBorder(), other.getCoordinate(1));
	}

	/**
	 * @return An exact copy of this AAB
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
	 * @return The distance along this ray where this AAB is located, if the ray intersects it.
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
			if((((start.minus(r.getStartLocation())).crossProduct(r.getDirection()).getCoordinate(2) * (end
					.minus(r.getStartLocation()).crossProduct(r.getDirection()).getCoordinate(2))) > 0) || (
					r.getDirection().dotProduct(perp) == 0)) continue;

			double collision = end.minus(r.getStartLocation()).dotProduct(perp) / r.getDirection().dotProduct(perp);
			if((collision > 0) && ((shortestCollision < 0) || (shortestCollision > collision)))
				shortestCollision = collision;
		}

		if(shortestCollision < 0) return Optional.absent();
		else return Optional.of(shortestCollision);
	}

	/**
	 * @param g
	 * 		The Graphics object on which to draw this AAB
	 */
	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(this.getColor());
		g.fillRect((int) (this.getCenterPosition().getCoordinate(0) - (this.getWidth() / 2f)),
				(int) (this.getCenterPosition().getCoordinate(1) - (this.getHeight() / 2f)), (int) this.getWidth(),
				(int) this.getHeight());
	}

	/**
	 * @return The right border of this AAB
	 */
	public double rightBorder()
	{
		return this.getCenterPosition().getCoordinate(0) + (this.getWidth() / 2);
	}

	/**
	 * @return The top border of this AAB
	 */
	public double topBorder()
	{
		return this.getCenterPosition().getCoordinate(1) + (this.getHeight() / 2);
	}

	/**
	 * @return The left border of this AAB
	 */
	public double leftBorder()
	{
		return this.getCenterPosition().getCoordinate(0) - (this.getWidth() / 2);
	}

	/**
	 * @return The bottom border of this AAB
	 */
	public double bottomBorder()
	{
		return this.getCenterPosition().getCoordinate(1) - (this.getHeight() / 2);
	}

	/**
	 * AAB's cannot be rotated, so rotation is always 0
	 *
	 * @return 0
	 */
	@Override
	public double getRotation()
	{
		return 0;
	}

	/**
	 * AAB cannot be rotated, so this does nothing.
	 *
	 * @param theta
	 * 		Unused
	 */
	@Override
	public void setRotation(double theta)
	{
		//Do nothing
	}

	/**
	 * @return The mass moment of inertia of this AAB
	 */
	@Override
	public double momentOfInertia()
	{
		return (this.getHeight() * this.getHeight() + this.getWidth() * this.getWidth()) / 12d;
	}

	/**
	 * Does nothing.
	 *
	 * @param f
	 * 		Unused
	 */
	@Override
	public void rotate(double f)
	{
		//Do nothing
	}

	/**
	 * @return A String representation of this AAB, containing its center position, width, and height.
	 */
	@Override
	public String toString()
	{
		return "AAB={" + this.getCenterPosition() + " with width " + this.getWidth() + " and height " + this.getHeight()
				+ '}';
	}

	/**
	 * @return This AAB projected onto the x axis
	 */
	@Override
	public Interval xInterval()
	{
		return DefaultInterval.about(this.getCenterPosition().getCoordinate(0), this.getWidth());
	}

	/**
	 * @return This AAB projected onto the y axis
	 */
	@Override
	public Interval yInterval()
	{
		return DefaultInterval.about(this.getCenterPosition().getCoordinate(1), this.getHeight());
	}
}
