package nschank.engn.shape.collide;

import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Dimensionals;
import nschank.collect.dim.Point;
import nschank.collect.dim.Vector;
import nschank.engn.shape.AbstractDrawable;
import nschank.util.Interval;
import nschank.util.NLists;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Created by Nicolas Schank for package nschank.engn.shape.collide
 * Created on 28 Sep 2013
 * Last updated on 27 May 2014
 *
 * A Circle is one of the 4 main types of Collidables. It has a center point, a radius/diameter, and a color. It does
 * rotate, though it may be hard (read: imposible) to see without overriding the draw method. Satisfies the invariant that
 * the width and height are equal.
 *
 * @author nschank, Brown University
 * @version 4.1
 */
public class Circle extends AbstractDrawable implements Collidable
{
	private double rotation = 0.0f;

	/**
	 * Creates a circle at the given {@code location}, with a given {@code radius}, and of the given {@code color}.
	 *
	 * @param location
	 * 		The center location of the circle
	 * @param radius
	 * 		The radius of this circle (half the width and height)
	 * @param c
	 * 		The Color of this circle
	 */
	public Circle(Dimensional location, double radius, Color c)
	{
		super(location, radius * 2d, radius * 2d, c);
	}

	/**
	 * @param other
	 * 		Another object which may be colliding with this one.
	 *
	 * @return A Collision from this circle to another object
	 */
	@Override
	public Optional<Collision> collisionWith(Collidable other)
	{
		return Collidables.inverseOf(other.collisionWithCircle(this));
	}

	/**
	 * @param other
	 * 		An AAB that may be colliding with this object
	 *
	 * @return A Collision from this circle to an AAB
	 */
	@Override
	public Optional<Collision> collisionWithAAB(AAB other)
	{
		double clampedX = Math.max(Math.min(this.getCenterPosition().getCoordinate(0),
				other.getCenterPosition().getCoordinate(0) + (other.getWidth() / 2)),
				other.getCenterPosition().getCoordinate(0) - (other.getWidth() / 2));
		double clampedY = Math.max(Math.min(this.getCenterPosition().getCoordinate(1),
				other.getCenterPosition().getCoordinate(1) + (other.getHeight() / 2)),
				other.getCenterPosition().getCoordinate(1) - (other.getHeight() / 2));
		if(Dimensionals.sqdistance(new Vector(clampedX, clampedY), this.getCenterPosition()) < (this.getRadius() * this
				.getRadius()))
		{
			Collision collision = new DefaultCollision(new Point(clampedX, clampedY), this.mtvFromAAB(other));
			return Optional.of(collision);
		}
		return Optional.absent();
	}

	/**
	 * @param other
	 * 		An Circle that may be colliding with this object
	 *
	 * @return A Collision with the given circle, if it exists
	 */
	@Override
	public Optional<Collision> collisionWithCircle(Circle other)
	{
		Vector apart = new Vector(other.getCenterPosition()).minus(this.getCenterPosition());
		if(apart.mag2() < ((this.getRadius() + other.getRadius()) * (this.getRadius() + other.getRadius())))
		{
			double mag = apart.mag();
			Vector napart = apart.normalized();
			Collision collision = new DefaultCollision(napart.smult(this.getRadius()).plus(this.getCenterPosition()),
					napart.smult((mag - (other.getRadius() + this.getRadius()))));
			return Optional.of(collision);
		}
		return Optional.absent();
	}

	/**
	 * @param other
	 * 		An Polygon that may be colliding with this object
	 *
	 * @return A Collision between this circle and a Polygon
	 */
	@Override
	public Optional<Collision> collisionWithPolygon(Polygon other)
	{
		return Collidables.inverseOf(other.collisionWithCircle(this));
	}

	/**
	 * @param other
	 *		A point in the same plane as this circle
	 * @return Whether or not this point is inside this {@code Circle}
	 */
	@Override
	public boolean contains(Dimensional other)
	{
		return Dimensionals.sqdistance(other, this.getCenterPosition()) <= (this.getRadius() * this.getRadius());
	}

	/**
	 * @return A Circle identical to this one in location, size, and color
	 */
	@Override
	public Collidable copy()
	{
		return new Circle(this.getCenterPosition(), this.getRadius(), this.getColor());
	}

	/**
	 * @param r
	 * 		A Ray in the same x-y coordinate plane as this object, which may be pointed to this object
	 *
	 * @return How far along the Ray one can find this Circle, if they intersect
	 */
	@Override
	public Optional<Double> distanceAlong(Ray r)
	{
		Vector relativeCenter = new Vector(this.getCenterPosition()).minus(r.getStartLocation());
		double projection = relativeCenter.dotProduct(r.getDirection());
		Dimensional projectionPoint = r.getAtDistance(projection);
		if(!(this.contains(projectionPoint) || ((projection < 0) && !this.contains(r.getStartLocation()))))
			return Optional.absent();

		double x2 = Dimensionals.sqdistance(this.getCenterPosition(), projectionPoint);
		double r2 = this.getRadius() * this.getRadius();

		if(this.contains(r.getStartLocation())) return Optional.of(projection + Math.sqrt(r2 - x2));
		else return Optional.of(projection - Math.sqrt(r2 - x2));
	}

	/**
	 * @param g
	 * 		The Graphics object on which to draw this circle
	 */
	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(this.getColor());
		g.fillOval((int) (this.getCenterPosition().getCoordinate(0) - this.getRadius()),
				(int) (this.getCenterPosition().getCoordinate(1) - this.getRadius()), (int) this.getWidth(),
				(int) this.getHeight());
	}

	/**
	 * @return The radius of this circle
	 */
	public double getRadius()
	{
		return this.getHeight() / 2.0;
	}

	/**
	 * @return The angle of this circle, in radians
	 */
	@Override
	public double getRotation()
	{
		return this.rotation;
	}

	/**
	 * Sets the angle of this circle from the x-axis
	 *
	 * @param theta
	 * 		The angle of this circle, in radians
	 */
	@Override
	public void setRotation(double theta)
	{
		this.rotation = theta % (2.0 * Math.PI);
	}

	/**
	 * @return The moment of inertia of this circle: (r^2)/2
	 */
	@Override
	public double momentOfInertia()
	{
		return (this.getRadius() * this.getRadius()) / 2.0;
	}

	/**
	 * @param other
	 *		An AAB that may be colliding with this object
	 * @return A Vector that, if this Circle followed, would displace this Circle completely from outside this AAB
	 */
	private Vector mtvFromAAB(AAB other)
	{
		if(other.contains(this.getCenterPosition())) //Circle is inside AAB
		{
			double xDiff = Math
					.abs(other.getCenterPosition().getCoordinate(0) - this.getCenterPosition().getCoordinate(0));
			double yDiff = Math
					.abs(other.getCenterPosition().getCoordinate(1) - this.getCenterPosition().getCoordinate(1));

			double needXDiff = this.getRadius() + (other.getWidth() / 2.0);
			double needYDiff = this.getRadius() + (other.getHeight() / 2.0);

			if(Math.abs(xDiff - needXDiff) < Math.abs(yDiff - needYDiff))
			{
				boolean negate = other.getCenterPosition().getCoordinate(0) > this.getCenterPosition().getCoordinate(0);
				return new Vector((negate ? -1f : 1f) * (needXDiff - xDiff), 0);
			} else
			{
				boolean negate = other.getCenterPosition().getCoordinate(1) > this.getCenterPosition().getCoordinate(1);
				return new Vector(0, (negate ? -1f : 1f) * (needYDiff - yDiff));
			}
		} else
		{
			double minXa = other.getCenterPosition().getCoordinate(0) + (other.getWidth() / 2d);
			double minXb = Math.max(other.getCenterPosition().getCoordinate(0) - (other.getWidth() / 2d),
					this.getCenterPosition().getCoordinate(0));
			double clampedX = Math.min(minXa, minXb);

			double minYa = other.getCenterPosition().getCoordinate(1) + (other.getHeight() / 2d);
			double minYb = Math.max(other.getCenterPosition().getCoordinate(1) - (other.getHeight() / 2d),
					this.getCenterPosition().getCoordinate(1));
			double clampedY = Math.min(minYa, minYb);

			return this.mtvFromCircle(new Circle(new Point(clampedX, clampedY), 0, Color.BLACK));
		}
	}

	/**
	 * @param other
	 *		A Circle that may be colliding with this object
	 * @return A Vector that, if this Circle were to follow, would displace it such that it would no longer be colliding
	 * 		with {@code other}
	 */
	Vector mtvFromCircle(Circle other)
	{
		Vector toCenter = new Vector(other.getCenterPosition()).minus(this.getCenterPosition());
		double dist = toCenter.mag();
		double newDist = (other.getRadius() + this.getRadius()) - dist;
		return toCenter.normalized().smult(-newDist);
	}

	/**
	 * @param axis
	 *		An axis onto which to project this circle
	 * @return The Interval along this axis upon which this circle falls.
	 */
	@Override
	public Interval projectionOnto(Dimensional axis)
	{
		Vector center = new Vector(this.getCenterPosition());

		Vector radius = new Vector(axis).normalized().smult(this.getRadius());

		Vector plusRadius = center.plus(radius);
		Vector minusRadius = center.minus(radius);

		return Dimensionals.project(NLists.of(plusRadius, minusRadius), axis);
	}

	/**
	 * @param theta
	 * 		An amount by which this Circle should rotate counter-clockwise
	 */
	@Override
	public void rotate(double theta)
	{
		this.rotation = ((this.rotation + theta) % (2 * Math.PI));
	}

	/**
	 * Changes the diameter of this circle
	 *
	 * @param height
	 * 		The new diameter of this {@code Circle}
	 */
	@Override
	public void setHeight(double height)
	{
		super.setHeight(height);
		super.setWidth(height);
	}

	/**
	 * Changes the radius of this Circle
	 *
	 * @param radius
	 * 		The new radius of this {@code Circle}
	 */
	public void setRadius(double radius)
	{
		super.setHeight(radius * 2d);
		super.setWidth(radius * 2d);
	}

	/**
	 * Changes the diameter of this circle
	 *
	 * @param width
	 * 		The new diameter of this {@code Circle}
	 */
	@Override
	public void setWidth(double width)
	{
		super.setHeight(width);
		super.setWidth(width);
	}

	/**
	 * @return A String representation of this Circle, including the center position and radius.
	 */
	@Override
	public String toString()
	{
		return "Circle{" + getCenterPosition() + " with radius " + getRadius() + "}";
	}
}
