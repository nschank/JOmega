package nschank.engn.shape.collide;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Dimensionals;
import nschank.collect.dim.Vector;
import nschank.engn.shape.AbstractDrawable;
import nschank.engn.shape.fxn.PointProjector;
import nschank.util.Interval;
import nschank.util.NLists;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//TODO: update with a Convex Hull

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
 * @version 3.1
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
		double area6 = Dimensionals.area(points) * 6d;
		Dimensional offFromCenter = this.unweightedCenter(points).sdiv(area6);

		Interval actualWidth = Interval.from(points, new Function<Dimensional, Double>()
		{
			@Override
			public Double apply(Dimensional o)
			{
				return Double.valueOf(o.getCoordinate(0));
			}
		});

		Interval actualHeight = Interval.from(points, new Function<Dimensional, Double>()
		{
			@Override
			public Double apply(Dimensional o)
			{
				return Double.valueOf(o.getCoordinate(1));
			}
		});
		super.setCenterPosition(offFromCenter);
		super.setWidth(actualWidth.width());
		super.setHeight(actualHeight.width());

		this.initPolygon(points);
	}

	/**
	 * @return
	 */
	protected List<? extends Dimensional> axes()
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

//	/**
//	 * TODO
//	 *
//	 * @param shapeAxes1
//	 * @param shapeAxes2
//	 * @param other
//	 *
//	 * @return
//	 */
//	Optional<Collision> collisionAlongAxes(Iterable<? extends Dimensional> shapeAxes1, Iterable<? extends Dimensional> shapeAxes2, PCollidable other)
//	{
//		Optional<Pair<Dimensional, Double>> shape = this.collisionAlongShapeAxes(shapeAxes1, shapeAxes2, other);
//		if(shape.isPresent()) return this.minimumCollisionFrom(shape.get(), other);
//		return Optional.absent();
//	}
//
//	/**
//	 * TODO
//	 *
//	 * @param shapeAxes1
//	 * @param shapeAxes2
//	 * @param other
//	 *
//	 * @return
//	 */
//	Optional<Pair<Dimensional, Double>> collisionAlongShapeAxes(Iterable<? extends Dimensional> shapeAxes1, Iterable<? extends Dimensional> shapeAxes2, PCollidable other)
//	{
//		Optional<Dimensional> minimumAxis = Optional.absent();
//		Optional<Double> minimumAxisExchange = Optional.absent();
//
//		for(Dimensional axis : shapeAxes1)
//		{
//			PointToFloat use = new PointToFloat(nschank.collect.dim.Point.ZERO_2D, axis);
//
//			Interval projection = this.projectionOnto(axis, use);
//			Interval otherProjection = other.projectionOnto(axis, use);
//
//			if(!projection.isIntersecting(otherProjection)) return Optional.absent();
//
//			double minimumExchange = projection.getMinimumTranslation(otherProjection);
//
//			if(!minimumAxisExchange.isPresent() || (Math.abs(minimumAxisExchange.get()) > Math.abs(minimumExchange)))
//			{
//				minimumAxis = Optional.of(axis);
//				minimumAxisExchange = Optional.of(minimumExchange);
//			}
//		}
//
//		for(Dimensional axis : shapeAxes2)
//		{
//			PointToFloat use = new PointToFloat(nschank.collect.dim.Point.ZERO_2D, axis);
//
//			Interval projection = this.projectionOnto(axis, use);
//			Interval otherProjection = other.projectionOnto(axis, use);
//
//			if(!projection.isIntersecting(otherProjection)) return Optional.absent();
//
//			double minimumExchange = projection.getMinimumTranslation(otherProjection);
//
//			if(!minimumAxisExchange.isPresent() || (Math.abs(minimumAxisExchange.get()) > Math.abs(minimumExchange)))
//			{
//				minimumAxis = Optional.of(axis);
//				minimumAxisExchange = Optional.of(minimumExchange);
//			}
//		}
//
//		return Optional.of(Pair.tuple(minimumAxis.get(), minimumAxisExchange.get()));
//	}

	/**
	 * @param other
	 * 		Another object which may be colliding with this one.
	 *
	 * @return
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
	 * @return
	 */
	@Override
	public Optional<Collision> collisionWithAAB(AAB other)
	{
		return this.collisionAlongAxes(this.axes(), other.axes(), other);
	}

	/**
	 * @param other
	 * 		An Circle that may be colliding with this object
	 *
	 * @return
	 */
	@Override
	public Optional<Collision> collisionWithCircle(Circle other)
	{
		Dimensional closest = Dimensionals.closestTo(other.getCenterPosition(), this.points);
		Vector line = new Vector(other.getCenterPosition()).minus(closest).normalized();
		if(line.getCoordinate(0) < 0) line = line.smult(-1);

		return this.collisionAlongAxes(this.axes(), NLists.of(line), other);
	}

	/**
	 * @param other
	 * 		An Point that may be colliding with this object
	 *
	 * @return
	 */
	@Override
	public Optional<Collision> collisionWithPoint(Point other)
	{
		Dimensional closest = Dimensionals.closestTo(other.getCenterPosition(), this.points);
		Vector line = new Vector(other.getCenterPosition()).minus(closest).normalized();
		if(line.getCoordinate(0) < 0) line = line.smult(-1);
		return this.collisionAlongAxes(this.axes(), NLists.of(line), other);
	}

	/**
	 * @param other
	 * 		An Polygon that may be colliding with this object
	 *
	 * @return
	 */
	@Override
	public Optional<Collision> collisionWithPolygon(Polygon other)
	{
		if(!this.xInterval().isIntersecting(other.xInterval()) || !this.yInterval().isIntersecting(other.yInterval()))
			return Optional.absent();
		return this.collisionAlongAxes(this.axes(), other.axes(), other);
	}

	/**
	 * @param other
	 *
	 * @return
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
	 * @return
	 */
	@Override
	public Collidable copy()
	{
		List<Dimensional> toUse = new ArrayList<>();
		for(Dimensional v : this.points)
			toUse.add(new nschank.collect.dim.Point(v.getCoordinate(0), v.getCoordinate(1)));
		return new Polygon(this.getColor(), toUse.toArray(new Dimensional[toUse.size()]));
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
	 * @return
	 */
	public Iterable<Dimensional> getPoints()
	{
		return Collections.unmodifiableList(this.points);
	}

	/**
	 * @return
	 */
	@Override
	public double getRotation()
	{
		return this.angle;
	}

	/**
	 * @param f
	 */
	@Override
	public void setRotation(double f)
	{
		Interval newX = Interval.about(this.getCenterPosition().getCoordinate(0), 0.0f);
		Interval newY = Interval.about(this.getCenterPosition().getCoordinate(0), 0.0f);
		Vector centre = new Vector(this.getCenterPosition());
		for(int i = 0; i < this.points.size(); i++)
		{
			Vector rel = centre.minus(this.points.get(i)).smult(-1);
			this.points.set(i, new Vector(this.getCenterPosition())
					.plus(Vector.fromPolar(rel.mag(), rel.angle() + (f - this.angle))));
			newX = newX.and(Interval.about(this.points.get(i).getCoordinate(0), 0.0f));
			newY = newY.and(Interval.about(this.points.get(i).getCoordinate(0), 0.0f));
		}
		this.angle = f;
		this.xInterval = newX;
		this.yInterval = newY;
	}

	/**
	 * @param points
	 */
	private void initPolygon(List<Dimensional> points)
	{
		double area6 = this.area(points) * 6d;
		Dimensional offFromCenter = this.unweightedCenter(points).sdiv(area6);

		Interval actualWidth = Interval.from(points, new Function<Dimensional, Double>()
		{
			@Override
			public Double apply(Dimensional o)
			{
				return Double.valueOf(o.getCoordinate(0));
			}
		});

		Interval actualHeight = Interval.from(points, new Function<Dimensional, Double>()
		{
			@Override
			public Double apply(Dimensional o)
			{
				return Double.valueOf(o.getCoordinate(1));
			}
		});

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
		this.xInterval = Interval.from(Dimensionals.getCoordinate(points, 0));
		this.yInterval = Interval.from(Dimensionals.getCoordinate(points, 1));
		this.momentOfInertia = numeratorSum / (denominatorSum * 6.0f);
	}

	/**
	 * @return
	 */
	public Optional<Dimensional> isConcave()
	{
		for(Dimensional point : this.getPoints())
		{
			List<Dimensional> allElse = new ArrayList<>(this.points);
			allElse.remove(point);
			if(new Polygon(Color.BLACK, allElse.toArray(new Dimensional[allElse.size()])).contains(point))
				return Optional.of(point);
		}
		return Optional.absent();
	}

	/**
	 * @return
	 */
	@Override
	public double momentOfInertia()
	{
		return this.momentOfInertia;
	}

	/**
	 * @param axis
	 *
	 * @return
	 */
	@Override
	public Interval projectionOnto(Dimensional axis)
	{
		Function<Dimensional, Dimensional> p2p = new PointProjector(Vector.ZERO_2D, axis);
		return Interval.from(this.points, Functions.compose(projector, p2p));
	}

	/**
	 * @param f
	 */
	@Override
	public void rotate(double f)
	{
		this.setRotation(((this.getRotation() + f) % (Math.PI * 2d)));
	}

	/**
	 * @param newPosition
	 */
	@Override
	public void setCenterPosition(Dimensional newPosition)
	{
		Vector newVector = new Vector(newPosition);
		for(int i = 0; i < this.points.size(); i++)
			this.points.set(i, newVector.plus(this.points.get(i)).minus(this.getCenterPosition()));

		this.xInterval = this.xInterval.plus(-newVector.minus(this.getCenterPosition()).getCoordinate(0));
		this.yInterval = this.yInterval.plus(-newVector.minus(this.getCenterPosition()).getCoordinate(1));
		super.setCenterPosition(newPosition);
	}

	/**
	 * @param f
	 */
	@Override
	public void setHeight(double f)
	{
		if(f == this.getHeight()) return;

		Vector projectionAtHeight = Vector
				.fromPolar(((f / this.getHeight()) - 1), this.getRotation() + ((float) Math.PI / 2f));
		for(int i = 0; i < this.points.size(); i++)
			this.points.set(i, new Vector(this.points.get(i)).smult(2).minus(this.getCenterPosition())
															 .projectOntoLine(Vector.ZERO_2D, projectionAtHeight)
															 .smult((f / this.getHeight()) - 1));
		this.xInterval = Interval.from(Dimensionals.getCoordinate(this.points, 0));
		this.yInterval = Interval.from(Dimensionals.getCoordinate(this.points, 1));
		super.setHeight(f);
	}

	/**
	 * @param f
	 */
	@Override
	public void setWidth(double f)
	{
		if(f == this.getWidth()) return;

		Vector projectionAtWidth = Vector.fromPolar(1, this.getRotation());
		for(int i = 0; i < this.points.size(); i++)
			this.points.set(i, new Vector(this.points.get(i)).smult(2).minus(this.getCenterPosition())
															 .projectOntoLine(Vector.ZERO_2D, projectionAtWidth)
															 .smult((f / this.getWidth()) - 1));
		this.xInterval = Interval.from(Dimensionals.getCoordinate(this.points, 0));
		this.yInterval = Interval.from(Dimensionals.getCoordinate(this.points, 1));
		super.setWidth(f);
	}

	/**
	 * @return
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
	 * @param points
	 *
	 * @return
	 */
	private Vector unweightedCenter(List<Dimensional> points)
	{
		double Cx = 0;
		double Cy = 0;
		for(int i = 0; i <= (points.size() - 1); i++)
		{
			Dimensional subI = points.get(i);
			Dimensional subIPlusOne = points.get((i + 1) % (points.size()));
			double areaOfThisTerm = (subI.getCoordinate(0) * subIPlusOne.getCoordinate(1)) - (
					subIPlusOne.getCoordinate(0) * subI.getCoordinate(1));
			Cx += (subI.getCoordinate(0) + subIPlusOne.getCoordinate(0)) * (areaOfThisTerm);
			Cy += (subI.getCoordinate(1) + subIPlusOne.getCoordinate(1)) * (areaOfThisTerm);
		}
		return new Vector(Cx, Cy);
	}

	/**
	 * @return
	 */
	@Override
	public Interval xInterval()
	{
		return this.xInterval;
	}

	/**
	 * @return
	 */
	@Override
	public Interval yInterval()
	{
		return this.yInterval;
	}
}
