package nschank.engn.shape.collide;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import cs195n.Vec2f;
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
 * Tou2
 *
 * @author Nicolas Schank
 * @version 2013 10 07
 * @since 2013 10 07 3:22 PM
 */
public class Polygon extends PCollidable
{
	protected List<Vec2f> points;
	private float angle = 0.0f;
	private float momentOfInertia;
	private Interval xInterval;
	private Interval yInterval;

	public Polygon(Vec2f location, float width, float height, Color c, Vec2f relativeStart, Vec2f relativeSecond, Vec2f... relativeOthers)
	{
		super(location, width, height, c);

		List<Vec2f> points = new ArrayList<>();
		points.add(relativeStart);
		points.add(relativeSecond);
		Collections.addAll(points, relativeOthers);
		this.initPolygon(points);

		this.setCenterPosition(location);
	}

	public Polygon(Color c, Vec2f... others)
	{
		super(Vec2f.ZEROES, 0, 0, c);
		List<Vec2f> points = new ArrayList<>();
		Collections.addAll(points, others);
		float area6 = this.area(points) * 6f;
		Vec2f offFromCenter = this.unweightedCenter(points).sdiv(area6);

		Interval actualWidth = Interval.from(points, new Function<Vec2f, Float>()
		{
			@Override
			public Float apply(Vec2f o)
			{
				return Float.valueOf(o.x);
			}
		});

		Interval actualHeight = Interval.from(points, new Function<Vec2f, Float>()
		{
			@Override
			public Float apply(Vec2f o)
			{
				return Float.valueOf(o.y);
			}
		});
		super.setCenterPosition(offFromCenter);
		super.setWidth(actualWidth.width());
		super.setHeight(actualHeight.width());


		this.initPolygon(points);
	}

	public Optional<Vec2f> isConcave()
	{
		for(Vec2f point : this.getPoints())
		{
			List<Vec2f> allElse = new ArrayList<>(this.getPoints());
			allElse.remove(point);
			if(new Polygon(Color.BLACK, allElse.toArray(new Vec2f[allElse.size()])).contains(point))
				return Optional.of(point);
		}
		return Optional.absent();
	}

	private float area(List<Vec2f> points)
	{
		float sum = 0;
		for(int i = 0; i <= (points.size() - 1); i++)
		{
			Vec2f subI = points.get(i);
			Vec2f subIPlusOne = points.get((i + 1) % (points.size()));
			sum += subI.x * subIPlusOne.y - subIPlusOne.x * subI.y;
		}
		return sum / 2f;
	}

	List<Vec2f> axes()
	{
		List<Vec2f> myAxes = new ArrayList<>();
		for(int i = -1; i < (this.getPoints().size() - 1); i++)
		{
			Vec2f startPoint = this.getPoints().get((this.getPoints().size() + i) % this.getPoints().size());
			Vec2f secondaryPoint = this.getPoints().get((this.getPoints().size() + i + 1) % this.getPoints().size());
			Vec2f line = startPoint.minus(secondaryPoint);
			line = new Vec2f(line.y, -line.x).normalized();
			if(line.x < 0) line = line.smult(-1);
			myAxes.add(line);
		}

		return myAxes;
	}

	Optional<Collision> collisionAlongAxes(List<Vec2f> shapeAxes1, List<Vec2f> shapeAxes2, PCollidable other)
	{
		Optional<Pair<Vec2f, Float>> shape = this.collisionAlongShapeAxes(shapeAxes1, shapeAxes2, other);
		if(shape.isPresent()) return this.minimumCollisionFrom(shape.get(), other);
		return Optional.absent();
	}

	Optional<Pair<Vec2f, Float>> collisionAlongShapeAxes(List<Vec2f> shapeAxes1, List<Vec2f> shapeAxes2, PCollidable other)
	{
		Optional<Vec2f> minimumAxis = Optional.absent();
		Optional<Float> minimumAxisExchange = Optional.absent();

		for(Vec2f axis : shapeAxes1)
		{
			PointToFloat use = new PointToFloat(Vec2f.ZEROES, axis);

			Interval projection = this.projectionOnto(axis, use);
			Interval otherProjection = other.projectionOnto(axis, use);

			if(!projection.isIntersecting(otherProjection)) return Optional.absent();

			float minimumExchange = projection.getMinimumTranslation(otherProjection);

			if(!minimumAxisExchange.isPresent() || (Math.abs(minimumAxisExchange.get()) > Math.abs(minimumExchange)))
			{
				minimumAxis = Optional.of(axis);
				minimumAxisExchange = Optional.of(minimumExchange);
			}
		}

		for(Vec2f axis : shapeAxes2)
		{
			PointToFloat use = new PointToFloat(Vec2f.ZEROES, axis);

			Interval projection = this.projectionOnto(axis, use);
			Interval otherProjection = other.projectionOnto(axis, use);

			if(!projection.isIntersecting(otherProjection)) return Optional.absent();

			float minimumExchange = projection.getMinimumTranslation(otherProjection);

			if(!minimumAxisExchange.isPresent() || (Math.abs(minimumAxisExchange.get()) > Math.abs(minimumExchange)))
			{
				minimumAxis = Optional.of(axis);
				minimumAxisExchange = Optional.of(minimumExchange);
			}
		}

		return Optional.of(Pair.tuple(minimumAxis.get(), minimumAxisExchange.get()));
	}

	@Override
	public Optional<Collision> collisionWith(Collidable other)
	{
		return inverseOf(other.collisionWithPolygon(this));
	}

	@Override
	public Optional<Float> distanceAlong(Ray r)
	{
		float shortestCollision = -1;
		for(int i = -1; i < (this.getPoints().size() - 1); i++)
		{
			Vec2f start = this.getPoints().get((this.getPoints().size() + i) % this.getPoints().size());
			Vec2f end = this.getPoints().get((this.getPoints().size() + i + 1) % this.getPoints().size());
			Vec2f perp = new Vec2f(start.minus(end).y, end.minus(start).x);
			if((((start.minus(r.getStartLocation())).cross(r.getDirection()) * (end.minus(r.getStartLocation()).cross(r.getDirection()))) > 0) || (r.getDirection().dot(perp) == 0))
				continue;
			float collision = end.minus(r.getStartLocation()).dot(perp) / r.getDirection().dot(perp);
			if((collision > 0) && ((shortestCollision < 0) || (shortestCollision > collision)))
				shortestCollision = collision;
		}

		if(shortestCollision < 0) return Optional.absent();
		else return Optional.of(shortestCollision);
	}

	@Override
	public Optional<Collision> collisionWithAAB(AAB other)
	{
		return this.collisionAlongAxes(this.axes(), other.axes(), other);
	}

	@Override
	public Optional<Collision> collisionWithCircle(Circle other)
	{
		Vec2f closest = NVec2fs.closestTo(other.getCenterPosition(), this.getPoints());
		Vec2f line = other.getCenterPosition().minus(closest).normalized();
		if(line.x < 0) line = line.smult(-1);

		return this.collisionAlongAxes(this.axes(), NLists.of(line), other);
	}

	@Override
	public Optional<Collision> collisionWithPoint(Point other)
	{
		Vec2f closest = NVec2fs.closestTo(other.getCenterPosition(), this.getPoints());
		Vec2f line = other.getCenterPosition().minus(closest).normalized();
		if(line.x < 0) line = line.smult(-1);
		return this.collisionAlongAxes(this.axes(), NLists.of(line), other);
	}

	@Override
	public Optional<Collision> collisionWithPolygon(Polygon other)
	{
		if(!this.xInterval().isIntersecting(other.xInterval()) || !this.yInterval().isIntersecting(other.yInterval()))
			return Optional.absent();
		return this.collisionAlongAxes(this.axes(), other.axes(), other);
	}

	boolean contains(Vec2f other)
	{
		for(int i = -1; i < (this.getPoints().size() - 1); i++)
		{
			Vec2f startPoint = points.get((points.size() + i) % points.size());
			if(startPoint.equals(other)) return true;
			Vec2f secondaryPoint = points.get((points.size() + i + 1) % points.size());
			Vec2f edge = secondaryPoint.minus(startPoint);
			Vec2f toOther = other.minus(startPoint);//.sdiv(edge.mag());
			//edge = edge.normalized();
			if(edge.cross(toOther) < 0)
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(this.getColor());
		Path2D path = new Path2D.Float();
		path.moveTo(this.getPoints().get(this.getPoints().size() - 1).x, this.getPoints().get(this.getPoints().size() - 1).y);
		for(Vec2f v : this.getPoints())
			path.lineTo(v.x, v.y);
		g.fill(path);
	}

	public List<Vec2f> getPoints()
	{
		return Collections.unmodifiableList(points);
	}

	@Override
	public float getRotation()
	{
		return this.angle;
	}

	private void initPolygon(List<Vec2f> points)
	{
		float area6 = this.area(points) * 6f;
		Vec2f offFromCenter = this.unweightedCenter(points).sdiv(area6);

		Interval actualWidth = Interval.from(points, new Function<Vec2f, Float>()
		{
			@Override
			public Float apply(Vec2f o)
			{
				return Float.valueOf(o.x);
			}
		});

		Interval actualHeight = Interval.from(points, new Function<Vec2f, Float>()
		{
			@Override
			public Float apply(Vec2f o)
			{
				return Float.valueOf(o.y);
			}
		});

		List<Vec2f> addToCenters = new ArrayList<>();
		float adjustedWidth = (float) ((this.getWidth()) / (actualWidth.getMax() - actualWidth.getMin()));
		float adjustedHeight = (float) ((this.getHeight()) / (actualHeight.getMax() - actualHeight.getMin()));
		for(Vec2f v : points)
			addToCenters.add(new Vec2f((v.minus(offFromCenter).x * adjustedWidth), (v.minus(offFromCenter).y * adjustedHeight)));

		float numeratorSum = 0.0f;
		float denominatorSum = 0.0f;
		for(int i = 0; i < addToCenters.size(); i++)
		{
			Vec2f currentI = addToCenters.get(i);
			Vec2f nextI = addToCenters.get((i + 1) % (addToCenters.size()));
			float cross = nextI.cross(currentI);
			float doubleNextDot = nextI.dot(nextI);
			float crossDot = nextI.dot(currentI);
			float doubleCurrentDot = currentI.dot(currentI);

			numeratorSum += cross * (doubleNextDot + crossDot + doubleCurrentDot);
			denominatorSum += cross;

			points.set(i, this.getCenterPosition().plus(currentI));
		}

		this.points = points;
		this.xInterval = Interval.from(points, NVec2fs.getX());
		this.yInterval = Interval.from(points, NVec2fs.getY());
		this.momentOfInertia = numeratorSum / (denominatorSum * 6.0f);
	}

	@Override
	Optional<Collision> minimumCollisionFrom(Pair<Vec2f, Float> mtv, PCollidable other)
	{
		return inverseOf(other.minimumCollisionFromPolygon(Pair.tuple(mtv.getA(), -1 * mtv.getB()), this));
	}

	@Override
	Optional<Collision> minimumCollisionFromCircle(Pair<Vec2f, Float> mtv, Circle other)
	{
		Vec2f closest = NVec2fs.closestTo(other.getCenterPosition(), this.getPoints());
		Collision c = new DefaultCollision(other.getCenterPosition().plus(closest.minus(other.getCenterPosition()).normalized().smult(other.getRadius())), mtv.getA().normalized().smult(mtv.getB()));
		return Optional.of(c);
	}

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

	@Override
	public float momentOfInertia()
	{
		return this.momentOfInertia;
	}

	@Override
	Interval projectionOnto(Vec2f axis, Function<Vec2f, Float> projector)
	{
		Function<Vec2f, Vec2f> p2p = new PointProjector(Vec2f.ZEROES, axis);
		return Interval.from(this.getPoints(), Functions.compose(projector, p2p));
	}

	@Override
	public void rotate(float f)
	{
		this.setRotation((float) ((this.getRotation() + f) % (Math.PI * 2f)));
	}

	@Override
	public Collidable copy()
	{
		List<Vec2f> toUse = new ArrayList<>();
		for(Vec2f v : this.points)
			toUse.add(new Vec2f(v.x, v.y));
		return new Polygon(this.getColor(), toUse.toArray(new Vec2f[toUse.size()]));
	}

	@Override
	public void setCenterPosition(Vec2f newPosition)
	{
		Interval newX = Interval.about(newPosition.x, 0.0f);
		Interval newY = Interval.about(newPosition.y, 0.0f);
		for(int i = 0; i < this.points.size(); i++)
		{
			this.points.set(i, this.points.get(i).minus(this.getCenterPosition().minus(newPosition)));
			newX = newX.and(Interval.about(this.points.get(i).x, 0.0f));
			newY = newY.and(Interval.about(this.points.get(i).y, 0.0f));
		}
		super.setCenterPosition(newPosition);
		this.xInterval = newX;
		this.yInterval = newY;
	}

	@Override
	public void setHeight(float f)
	{
		if(f == this.getHeight()) return;

		Vec2f projectionAtHeight = Vec2f.fromPolar(this.getRotation() + ((float) Math.PI / 2f), ((f / this.getHeight()) - 1));
		for(int i = 0; i < this.getPoints().size(); i++)
			this.points.set(i, this.getPoints().get(i).plus(this.getPoints().get(i).minus(this.getCenterPosition()).projectOntoLine(Vec2f.ZEROES, projectionAtHeight).smult((f / this.getHeight()) - 1)));
		this.xInterval = Interval.from(this.points, NVec2fs.getX());
		this.yInterval = Interval.from(this.points, NVec2fs.getY());
		super.setHeight(f);
	}

	@Override
	public void setRotation(float f)
	{
		Interval newX = Interval.about(this.getCenterPosition().x, 0.0f);
		Interval newY = Interval.about(this.getCenterPosition().y, 0.0f);
		for(int i = 0; i < this.getPoints().size(); i++)
		{
			Vec2f rel = this.getPoints().get(i).minus(this.getCenterPosition());
			this.points.set(i, this.getCenterPosition().plus(Vec2f.fromPolar(rel.angle() + (f - this.angle), rel.mag())));
			newX = newX.and(Interval.about(this.points.get(i).x, 0.0f));
			newY = newY.and(Interval.about(this.points.get(i).y, 0.0f));
		}
		this.angle = f;
		this.xInterval = newX;
		this.yInterval = newY;
	}

	@Override
	public void setWidth(float f)
	{
		if(f == this.getWidth()) return;

		Vec2f projectionAtWidth = Vec2f.fromPolar(this.getRotation(), 1);
		for(int i = 0; i < this.getPoints().size(); i++)
			this.points.set(i, this.getPoints().get(i).plus(this.getPoints().get(i).minus(this.getCenterPosition()).projectOntoLine(Vec2f.ZEROES, projectionAtWidth).smult((f / this.getWidth()) - 1)));
		this.xInterval = Interval.from(this.points, NVec2fs.getX());
		this.yInterval = Interval.from(this.points, NVec2fs.getY());
		super.setWidth(f);
	}

	@Override
	public String toString()
	{
		return "Polygon{" +
				"points=" + points +
				", xInterval=" + xInterval +
				", yInterval=" + yInterval + ", axes=" + axes() +
				'}';
	}

	private Vec2f unweightedCenter(List<Vec2f> points)
	{
		float Cx = 0;
		float Cy = 0;
		for(int i = 0; i <= (points.size() - 1); i++)
		{
			Vec2f subI = points.get(i);
			Vec2f subIPlusOne = points.get((i + 1) % (points.size()));
			float areaOfThisTerm = (subI.x * subIPlusOne.y) - (subIPlusOne.x * subI.y);
			Cx += (subI.x + subIPlusOne.x) * (areaOfThisTerm);
			Cy += (subI.y + subIPlusOne.y) * (areaOfThisTerm);
		}
		return new Vec2f(Cx, Cy);
	}

	@Override
	public Interval xInterval()
	{
		return xInterval;
	}

	@Override
	public Interval yInterval()
	{
		return yInterval;
	}
}
