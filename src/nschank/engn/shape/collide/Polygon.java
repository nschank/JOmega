package nschank.engn.shape.collide;

import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Dimensionals;
import nschank.collect.dim.Vector;
import nschank.engn.shape.AbstractDrawable;
import nschank.util.Interval;
import nschank.util.Intervals;
import nschank.util.NLists;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


//TODO: update with a Convex Hull
//TODO: furthest point for quicker contain

/**
 * Created by Nicolas Schank for package nschank.engn.shape.collide
 * Created on 7 Oct 2013
 * Last updated on 28 May 2014
 *
 * A Polygon is one of the four main types of Collidables, and likely the most flexible. Any convex Polygon (with a
 * finite number of points) can be expressed by this class by supplying it with all of the vertices as a series of
 * Dimensionals given in clockwise order. A non-convex polygon will be accepted, but will have undefined behaviour;
 * the method isConcave() is provided for further error checking.
 *
 * @author nschank, Brown University
 * @version 3.4
 */
public class Polygon extends AbstractDrawable implements Collidable
{
	private double angle = 0.0f;
	private double momentOfInertia;
	protected List<Dimensional> points;
	private Interval xInterval;
	private Interval yInterval;

	/**
	 * Creates a Polygon centered at the given centre of mass, with the given width and height, and using the given
	 * three or more points (in clockwise order) as a map of how the different points should be placed relative each other.
	 * For example, a square can be described by (0,0), (0,1), (1,1), (1,0); the centre of the square will be placed at
	 * the given {@code location}; and its {@code width} and {@code height} will be set as given (and will obviously be
	 * a rectangle, if not equal). Scale of the points provided is unused.
	 *
	 * @param location
	 * 		The centre of mass of the Polygon after initialization
	 * @param width
	 * 		The width of the Polygon, along the x-axis
	 * @param height
	 * 		The height of the Polygon, along the y-axis
	 * @param c
	 * 		The Color of the Polygon
	 * @param relativeStart
	 * 		Any 2D point in the coordinate plane, as a Dimensional
	 * @param relativeSecond
	 * 		Another 2D point in the coordinate plane that is one point clockwise on the Polygon
	 * @param relativeOthers
	 * 		The remainder of the points in the Polygon, in 2D, at distances relative to the distance between the first
	 * 		and second point.
	 */
	public Polygon(final Dimensional location, final double width, final double height, final Color c,
				   final Dimensional relativeStart, final Dimensional relativeSecond,
				   final Dimensional... relativeOthers)
	{
		super(location, width, height, c);

		List<Dimensional> points = new ArrayList<>();
		points.add(relativeStart);
		points.add(relativeSecond);
		Collections.addAll(points, relativeOthers);
		this.initPolygon(points);

		this.setCenterPosition(location);
	}

	/**
	 * Creates a Polygon of the given Color whose points are exactly those given. The centre of mass of those points will
	 * be the new location of the Polygon, and the width and height will be created as expected. The points should be
	 * supplied in clockwise order; if they are not, or do not produce a Convex Polygon, undefined behaviour will result.
	 *
	 * @param c
	 * 		The Color of the Polygon
	 * @param first
	 * 		Any vertex of the Polygon
	 * @param second
	 * 		The vertex one clockwise of {@code first} on the Polygon
	 * @param others
	 * 		All other vertices of the Polygon clockwise from {@code second}, not including {@code first}.
	 */
	public Polygon(final Color c, final Dimensional first, final Dimensional second, final Dimensional... others)
	{
		super(nschank.collect.dim.Point.ZERO_2D, 0, 0, c);

		List<Dimensional> points = new ArrayList<>();
		points.add(first);
		points.add(second);
		Collections.addAll(points, others);
		Dimensional offFromCenter = Dimensionals.weightedCenter(points);

		Interval actualWidth = Intervals.from(Dimensionals.getCoordinate(points, 0));
		Interval actualHeight = Intervals.from(Dimensionals.getCoordinate(points, 1));
		super.setCenterPosition(offFromCenter);
		super.setWidth(actualWidth.width());
		super.setHeight(actualHeight.width());

		this.initPolygon(points);
	}

	/**
	 * @return The axes this Polygon uses under the separating axis theorem
	 */
	protected List<Dimensional> axes()
	{
		List<Dimensional> myAxes = new ArrayList<>();
		for(int i = -1; i < (this.points.size() - 1); i++)
		{
			Dimensional startPoint = this.points.get((this.points.size() + i) % this.points.size());
			Dimensional secondaryPoint = this.points.get((this.points.size() + i + 1) % this.points.size());
			Vector line = new Vector(startPoint).minus(secondaryPoint);
			line = new Vector(line.getCoordinate(1), -line.getCoordinate(0)).normalized();
			if(line.getCoordinate(1) < 0) line = line.smult(-1);
			myAxes.add(line);
		}

		return myAxes;
	}

	/**
	 * @param other
	 * 		Another object which may be colliding with this one.
	 *
	 * @return A Collision between this object and the other object, if it exists.
	 */
	@Override
	public Optional<Collision> collisionWith(Collidable other)
	{
		return Collidables.inverseOf(other.collisionWithPolygon(this));
	}

	/**
	 * @param other
	 * 		An AAB that may be colliding with this object
	 *
	 * @return A Collision between this object and the given AAB, if it exists.
	 */
	@Override
	public Optional<Collision> collisionWithAAB(AAB other)
	{
		return this.collisionWithPolygon(other);
	}

	/**
	 * @param other
	 * 		An Circle that may be colliding with this object
	 *
	 * @return A Collision between this object and the given Circle, if it exists.
	 */
	@Override
	public Optional<Collision> collisionWithCircle(Circle other)
	{
		Dimensional closest = Dimensionals.closestTo(other.getCenterPosition(), this.points);
		Vector line = new Vector(other.getCenterPosition()).minus(closest).normalized();
		line.smult(Math.signum(line.getCoordinate(0)));

		List<Dimensional> axes = NLists.combineLists(this.axes(), NLists.of((Dimensional) line));
		Vector mtv = this.shortestMTV(other, axes);
		if(mtv.isZero()) return Optional.absent();

		Dimensional circlePoint = mtv.normalized().smult(-other.getRadius()).plus(this.getCenterPosition());
		List<Dimensional> intersectPoints = NLists.combineLists(Collidables.contained(this, NLists.of(circlePoint)),
				Collidables.contained(other, this.points));
		Dimensional collisionPoint = Dimensionals.average(intersectPoints);
		if(collisionPoint == null) return Optional.absent();

		Collision coll = new DefaultCollision(collisionPoint, mtv);

		return Optional.of(coll);
	}

	/**
	 * @param other
	 * 		An Polygon that may be colliding with this object
	 *
	 * @return A Collision between this object and the given Polygon, if it exists.
	 */
	@Override
	public Optional<Collision> collisionWithPolygon(Polygon other)
	{
		Dimensional closest = Dimensionals.closestTo(other.getCenterPosition(), this.points);
		Vector line = new Vector(other.getCenterPosition()).minus(closest).normalized();
		line.smult(Math.signum(line.getCoordinate(0)));

		List<Dimensional> axes = NLists.combineLists(this.axes(), NLists.of((Dimensional) line));
		Vector mtv = this.shortestMTV(other, axes);
		if(mtv.isZero()) return Optional.absent();

		List<Dimensional> intersectPoints = NLists
				.combineLists(Collidables.contained(this, other.points), Collidables.contained(other, this.points));
		Dimensional collisionPoint = Dimensionals.average(intersectPoints);
		if(collisionPoint == null) return Optional.absent();

		Collision coll = new DefaultCollision(collisionPoint, mtv);

		return Optional.of(coll);
	}

	/**
	 * @param other
	 * 		A point within the same plane as this shape
	 *
	 * @return Whether {@code other} is inside this shape
	 */
	@Override
	public boolean contains(Dimensional other)
	{
		Vector vother = new Vector(other);
		for(int i = -1; i < (this.points.size() - 1); i++)
		{
			Dimensional startPoint = this.points.get((this.points.size() + i) % this.points.size());
			if(startPoint.equals(other)) return true;

			Dimensional secondaryPoint = this.points.get((this.points.size() + i + 1) % this.points.size());
			Vector edge = new Vector(secondaryPoint).minus(startPoint);
			Vector toOther = vother.minus(startPoint);//.sdiv(edge.mag());
			//edge = edge.normalized();
			if(edge.crossProduct(toOther).getCoordinate(2) < 0) return false;
		}
		return true;
	}

	/**
	 * @return A Polygon identical to this one.
	 */
	@Override
	public Collidable copy()
	{
		Collection<Dimensional> rest = new ArrayList<>();
		for(int i = 2; i < this.points.size(); i++)
			rest.add(new nschank.collect.dim.Point(this.points.get(i)));
		return new Polygon(this.getColor(), new nschank.collect.dim.Point(this.points.get(0)),
				new nschank.collect.dim.Point(this.points.get(1)), rest.toArray(new Dimensional[rest.size()]));
	}

	/**
	 * @param r
	 * 		A Ray in the same x-y coordinate plane as this object, which may be pointed to this object
	 *
	 * @return How far from the origin on the ray on which this shape appears
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
	 * 		A Graphics object to draw this Polygon onto
	 */
	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(this.getColor());
		Path2D path = new Path2D.Double();
		path.moveTo(this.points.get(this.points.size() - 1).getCoordinate(0),
				this.points.get(this.points.size() - 1).getCoordinate(1));
		for(Dimensional v : this.points)
			path.lineTo(v.getCoordinate(0), v.getCoordinate(1));
		g.fill(path);
	}

	/**
	 * @return The points that make up the edges of this {@code Polygon}, in clockwise order
	 */
	public Iterable<Dimensional> getPoints()
	{
		return Collections.unmodifiableList(this.points);
	}

	/**
	 * @return The angle this Polygon make with the x-axis
	 */
	@Override
	public double getRotation()
	{
		return this.angle;
	}

	/**
	 * @param theta
	 * 		The new angle this makes with the x-axis
	 */
	@Override
	public void setRotation(double theta)
	{
		Interval newX = Intervals.about(this.getCenterPosition().getCoordinate(0), 0.0f);
		Interval newY = Intervals.about(this.getCenterPosition().getCoordinate(0), 0.0f);
		Vector centre = new Vector(this.getCenterPosition());
		for(int i = 0; i < this.points.size(); i++)
		{
			Vector rel = centre.minus(this.points.get(i)).smult(-1);
			this.points.set(i, new Vector(this.getCenterPosition())
					.plus(Vector.fromPolar(rel.mag(), rel.angle() + (theta - this.angle))));
			newX = newX.and(this.points.get(i).getCoordinate(0));
			newY = newY.and(this.points.get(i).getCoordinate(0));
		}
		this.angle = theta;
		this.xInterval = newX;
		this.yInterval = newY;
	}

	/**
	 * @param points
	 * 		Initializes a Polygon with the given list of points
	 */
	private void initPolygon(List<Dimensional> points)
	{
		Dimensional offFromCenter = Dimensionals.weightedCenter(points);

		Interval actualWidth = Intervals.from(Dimensionals.getCoordinate(points, 0));
		Interval actualHeight = Intervals.from(Dimensionals.getCoordinate(points, 1));

		List<Vector> addToCenters = new ArrayList<>();
		double adjustedWidth = ((this.getWidth()) / (actualWidth.getMax() - actualWidth.getMin()));
		double adjustedHeight = ((this.getHeight()) / (actualHeight.getMax() - actualHeight.getMin()));
		for(Dimensional v : points)
			addToCenters.add(new Vector((new Vector(v).minus(offFromCenter).getCoordinate(0) * adjustedWidth),
					(new Vector(v).minus(offFromCenter).getCoordinate(1) * adjustedHeight)));

		double numeratorSum = 0.0;
		double denominatorSum = 0.0;
		for(int i = 0; i < addToCenters.size(); i++)
		{
			Vector currentI = addToCenters.get(i);
			Vector nextI = addToCenters.get((i + 1) % (addToCenters.size()));
			double cross = nextI.crossProduct(currentI).getCoordinate(2);
			double doubleNextDot = nextI.dotProduct(nextI);
			double crossDot = nextI.dotProduct(currentI);
			double doubleCurrentDot = currentI.dotProduct(currentI);

			numeratorSum += cross * (doubleNextDot + crossDot + doubleCurrentDot);
			denominatorSum += cross;

			points.set(i, currentI.plus(this.getCenterPosition()));
		}

		this.points = points;
		this.xInterval = Intervals.from(Dimensionals.getCoordinate(points, 0));
		this.yInterval = Intervals.from(Dimensionals.getCoordinate(points, 1));
		this.momentOfInertia = numeratorSum / (denominatorSum * 6.0f);
	}

	/**
	 * Determines whether this Polygon is concave. If so, finds the particular point.
	 *
	 * Not run internally due to slow runtime.
	 *
	 * @return A point which makes this {@code Polygon} concave, if one exists
	 */
	public Optional<Dimensional> isConcave()
	{
		for(Dimensional point : this.getPoints())
		{
			List<Dimensional> allElse = new ArrayList<>(this.points);
			allElse.remove(point);
			if(new Polygon(Color.BLACK, allElse.get(0), allElse.get(1),
					allElse.subList(2, allElse.size()).toArray(new Dimensional[allElse.size() - 2])).contains(point))
				return Optional.of(point);
		}
		return Optional.absent();
	}

	/**
	 * @return The moment of inertia of this {@code Polygon}, assuming it has a mass of 1
	 */
	@Override
	public double momentOfInertia()
	{
		return this.momentOfInertia;
	}

	/**
	 * Given an object with which this Polygon is colliding, and the axes over which the shortest collision may be
	 * occurring (by Separating Axis Theorem), finds the shortest MTV among those axes. If there is no collision, returns
	 * a zero vector.
	 *
	 * @param other
	 * 		Another object which may be colliding with this one.
	 * @param axes
	 * 		All axes, by the Separating Axis Theorem, which are necessary to check for a collision
	 *
	 * @return The minimal Vector when, added to this Polygon, the Collision will no longer be present.
	 */
	private Vector shortestMTV(final Collidable other, final Iterable<Dimensional> axes)
	{
		Vector shortest = null;
		double mag2 = -1;

		for(Dimensional axis : axes)
		{
			Interval mine = this.projectionOnto(axis);
			Interval theirs = other.projectionOnto(axis);

			if(!mine.isIntersecting(theirs)) return Vector.ZERO_2D;

			Vector norm = new Vector(axis).normalized();
			Vector mtv = norm.smult(Math.signum(norm.getCoordinate(0)) * mine.getMinimumTranslation(theirs));

			if(shortest == null || mtv.mag2() < mag2)
			{
				shortest = mtv;
				mag2 = mtv.mag2();
			}
		}
		return shortest;
	}

	/**
	 * @param axis
	 * 		An axis onto which to project this {@code Polygon}
	 *
	 * @return The DefaultInterval along this axis along which this {@code Polygon} falls
	 */
	@Override
	public Interval projectionOnto(Dimensional axis)
	{
		return Dimensionals.project(this.points, axis);
	}

	/**
	 * Rotates this {@code Polygon} {@code theta} radians.
	 *
	 * @param theta
	 * 		A number of radians to rotate this Polygon counter-clockwise
	 */
	@Override
	public void rotate(double theta)
	{
		this.setRotation(((this.getRotation() + theta) % (Math.PI * 2d)));
	}

	/**
	 * @param newPosition
	 * 		A new centre of mass for this {@code Polygon}
	 */
	@Override
	public void setCenterPosition(Dimensional newPosition)
	{
		Vector newVector = new Vector(newPosition);
		for(int i = 0; i < this.points.size(); i++)
			this.points.set(i, newVector.plus(this.points.get(i)).minus(this.getCenterPosition()));

		this.xInterval = this.xInterval.plus(newVector.minus(this.getCenterPosition()).getCoordinate(0));
		this.yInterval = this.yInterval.plus(newVector.minus(this.getCenterPosition()).getCoordinate(1));
		super.setCenterPosition(newPosition);
	}

	/**
	 * @param h
	 * 		The new height of this {@code Polygon}
	 */
	@Override
	public void setHeight(double h)
	{
		if(h == this.getHeight()) return;

		Vector projectionAtHeight = Vector
				.fromPolar(((h / this.getHeight()) - 1), this.getRotation() + ((float) Math.PI / 2f));
		for(int i = 0; i < this.points.size(); i++)
			this.points.set(i, new Vector(this.points.get(i)).smult(2).minus(this.getCenterPosition())
															 .projectOntoLine(Vector.ZERO_2D, projectionAtHeight)
															 .smult((h / this.getHeight()) - 1));
		this.xInterval = Intervals.from(Dimensionals.getCoordinate(this.points, 0));
		this.yInterval = Intervals.from(Dimensionals.getCoordinate(this.points, 1));
		super.setHeight(h);
	}

	/**
	 * @param w
	 * 		The new width of this {@code Polygon}
	 */
	@Override
	public void setWidth(double w)
	{
		if(w == this.getWidth()) return;

		Vector projectionAtWidth = Vector.fromPolar(1, this.getRotation());
		for(int i = 0; i < this.points.size(); i++)
			this.points.set(i, new Vector(this.points.get(i)).smult(2).minus(this.getCenterPosition())
															 .projectOntoLine(Vector.ZERO_2D, projectionAtWidth)
															 .smult((w / this.getWidth()) - 1));
		this.xInterval = Intervals.from(Dimensionals.getCoordinate(this.points, 0));
		this.yInterval = Intervals.from(Dimensionals.getCoordinate(this.points, 1));
		super.setWidth(w);
	}

	/**
	 * @return A string representation of this Polygon, with points, x and y Intervals, and axes.
	 */
	@Override
	public String toString()
	{
		return "Polygon{" +
				"points=" + this.points +
				", xInterval=" + this.xInterval +
				", yInterval=" + this.yInterval + ", axes=" + this.axes() +
				'}';
	}

	/**
	 * @return The x interval (along the x axis) of this Polygon
	 */
	@Override
	public Interval xInterval()
	{
		return this.xInterval;
	}

	/**
	 * @return The y interval (along the y axis) of this Polygon
	 */
	@Override
	public Interval yInterval()
	{
		return this.yInterval;
	}
}
