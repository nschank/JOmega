package nschank.engn.shape.collide;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import cs195n.Vec2f;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Dimensionals;
import nschank.collect.dim.Vector;
import nschank.collect.tuple.Pair;
import nschank.engn.shape.fxn.PointProjector;
import nschank.engn.shape.fxn.PointToFloat;
import nschank.util.Interval;
import nschank.util.NLists;
import nschank.util.NVec2fs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.*;


/**
 * Created by Nicolas Schank for package nschank.engn.shape.collide
 * Created on 7 Oct 2013
 * Last updated on 27 May 2014
 *
 * @author nschank, Brown University
 * @version 2.11
 */
public class Polygon extends PCollidable
{
	private double angle = 0.0f;
	private double momentOfInertia;
	protected List<Dimensional> points;
	private Interval xInterval;
	private Interval yInterval;

	public Polygon(Dimensional location, double width, double height, Color c, Dimensional relativeStart, Dimensional relativeSecond, Dimensional... relativeOthers)
	{
		super(location, width, height, c);

		List<Dimensional> points = new ArrayList<>();
		points.add(relativeStart);
		points.add(relativeSecond);
		Collections.addAll(points, relativeOthers);
		this.initPolygon(points);

		this.setCenterPosition(location);
	}

	public Polygon(Color c, Dimensional... others)
	{
		super(nschank.collect.dim.Point.ZERO_2D, 0, 0, c);
		List<Dimensional> points = new ArrayList<>();
		Collections.addAll(points, others);
		double area6 = this.area(points) * 6f;
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
	 * @param points
	 *
	 * @return
	 */
	private double area(List<Dimensional> points)
	{
		float sum = 0;
		for(int i = 0; i <= (points.size() - 1); i++)
		{
			Dimensional subI = points.get(i);
			Dimensional subIPlusOne = points.get((i + 1) % (points.size()));
			sum += subI.getCoordinate(0) * subIPlusOne.getCoordinate(1) - subIPlusOne.getCoordinate(0) * subI.getCoordinate(1);
		}
		return sum / 2f;
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

	/**
	 * TODO
	 *
	 * @param shapeAxes1
	 * @param shapeAxes2
	 * @param other
	 *
	 * @return
	 */
	Optional<Collision> collisionAlongAxes(Iterable<? extends Dimensional> shapeAxes1, Iterable<? extends Dimensional> shapeAxes2, PCollidable other)
	{
		Optional<Pair<Dimensional, Double>> shape = this.collisionAlongShapeAxes(shapeAxes1, shapeAxes2, other);
		if(shape.isPresent()) return this.minimumCollisionFrom(shape.get(), other);
		return Optional.absent();
	}

	/**
	 * TODO
	 *
	 * @param shapeAxes1
	 * @param shapeAxes2
	 * @param other
	 *
	 * @return
	 */
	Optional<Pair<Dimensional, Double>> collisionAlongShapeAxes(Iterable<? extends Dimensional> shapeAxes1, Iterable<? extends Dimensional> shapeAxes2, PCollidable other)
	{
		Optional<Dimensional> minimumAxis = Optional.absent();
		Optional<Double> minimumAxisExchange = Optional.absent();

		for(Dimensional axis : shapeAxes1)
		{
			PointToFloat use = new PointToFloat(nschank.collect.dim.Point.ZERO_2D, axis);

			Interval projection = this.projectionOnto(axis, use);
			Interval otherProjection = other.projectionOnto(axis, use);

			if(!projection.isIntersecting(otherProjection)) return Optional.absent();

			double minimumExchange = projection.getMinimumTranslation(otherProjection);

			if(!minimumAxisExchange.isPresent() || (Math.abs(minimumAxisExchange.get()) > Math.abs(minimumExchange)))
			{
				minimumAxis = Optional.of(axis);
				minimumAxisExchange = Optional.of(minimumExchange);
			}
		}

		for(Dimensional axis : shapeAxes2)
		{
			PointToFloat use = new PointToFloat(nschank.collect.dim.Point.ZERO_2D, axis);

			Interval projection = this.projectionOnto(axis, use);
			Interval otherProjection = other.projectionOnto(axis, use);

			if(!projection.isIntersecting(otherProjection)) return Optional.absent();

			double minimumExchange = projection.getMinimumTranslation(otherProjection);

			if(!minimumAxisExchange.isPresent() || (Math.abs(minimumAxisExchange.get()) > Math.abs(minimumExchange)))
			{
				minimumAxis = Optional.of(axis);
				minimumAxisExchange = Optional.of(minimumExchange);
			}
		}

		return Optional.of(Pair.tuple(minimumAxis.get(), minimumAxisExchange.get()));
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
		return inverseOf(other.collisionWithPolygon(this));
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
		Path2D path = new Path2D.Double();
		path.moveTo(this.points.get(this.points.size() - 1).getCoordinate(0), this.points.get(this.points.size() - 1).getCoordinate(1));
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
			this.points.set(i, new Vector(this.getCenterPosition()).plus(Vector.fromPolar(rel.mag(), rel.angle() + (f - this.angle))));
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
			addToCenters.add(new Vector((new Vector(v).minus(offFromCenter).getCoordinate(0) * adjustedWidth), (new Vector(v).minus(offFromCenter).getCoordinate(1) * adjustedHeight)));

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
	 * todo
	 *
	 * @param mtv
	 * @param other
	 *
	 * @return
	 */
	@Override
	Optional<Collision> minimumCollisionFrom(Pair<Vec2f, Float> mtv, PCollidable other)
	{
		return inverseOf(other.minimumCollisionFromPolygon(Pair.tuple(mtv.getA(), -1 * mtv.getB()), this));
	}

	/**
	 * todo
	 *
	 * @param mtv
	 * @param other
	 *
	 * @return
	 */
	@Override
	Optional<Collision> minimumCollisionFromCircle(Pair<Vec2f, Float> mtv, Circle other)
	{
		Vec2f closest = NVec2fs.closestTo(other.getCenterPosition(), this.getPoints());
		Collision c = new DefaultCollision(other.getCenterPosition().plus(closest.minus(other.getCenterPosition()).normalized().smult(other.getRadius())), mtv.getA().normalized().smult(mtv.getB()));
		return Optional.of(c);
	}

	/**
	 * todo
	 *
	 * @param mtv
	 * @param other
	 *
	 * @return
	 */
	@Override
	Optional<Collision> minimumCollisionFromPolygon(Pair<Vec2f, Float> mtv, Polygon other)
	{
		Set<Vec2f> allPositions = new HashSet<>();
		for(Vec2f v : this.getPoints())
			if(other.contains(v)) allPositions.add(v);
		for(Vec2f v : other.getPoints())
			if(this.contains(v)) allPositions.add(v);
		if(allPositions.isEmpty())
		{
			return Optional.absent();
		}
		Collision c = new DefaultCollision(NVec2fs.average(allPositions), mtv.getA().smult(mtv.getB()));
		return Optional.of(c);
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
	 * @param projector
	 *
	 * @return
	 */
	@Override
	Interval projectionOnto(Dimensional axis, Function<Dimensional, Double> projector)
	{
		Function<Dimensional, Dimensional> p2p = new PointProjector(Vector.ZERO_2D, axis);
		return Interval.from(this.getPoints(), Functions.compose(projector, p2p));
	}

	/**
	 * @param f
	 */
	@Override
	public void rotate(double f)
	{
		this.setRotation(((this.getRotation() + f) % (Math.PI * 2f)));
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

		Vector projectionAtHeight = Vector.fromPolar(((f / this.getHeight()) - 1), this.getRotation() + ((float) Math.PI / 2f));
		for(int i = 0; i < this.points.size(); i++)
			this.points.set(i, new Vector(this.points.get(i)).smult(2).minus(this.getCenterPosition()).projectOntoLine(Vector.ZERO_2D, projectionAtHeight).smult((f / this.getHeight()) - 1));
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
			this.points.set(i, new Vector(this.points.get(i)).smult(2).minus(this.getCenterPosition()).projectOntoLine(Vector.ZERO_2D, projectionAtWidth).smult((f / this.getWidth()) - 1));
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
	private Dimensional unweightedCenter(List<Dimensional> points)
	{
		double Cx = 0;
		double Cy = 0;
		for(int i = 0; i <= (points.size() - 1); i++)
		{
			Dimensional subI = points.get(i);
			Dimensional subIPlusOne = points.get((i + 1) % (points.size()));
			double areaOfThisTerm = (subI.getCoordinate(0) * subIPlusOne.getCoordinate(1)) - (subIPlusOne.getCoordinate(0) * subI.getCoordinate(1));
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
