package nschank.engn.shape.collide;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Optional;
import cs195n.Vec2f;
import nschank.collect.tuple.Pair;
import nschank.engn.shape.fxn.PointProjector;
import nschank.util.Interval;
import nschank.util.NLists;
import nschank.util.NObjects;

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
public class Circle extends PCollidable
{
	private Color dotColor;
	private Vec2f dotPosition;
	private float rotation = 0.0f;

	private Circle()
	{
		super();
		this.dotColor = new Color(200, 200, 200);
		this.dotPosition = new Vec2f(0, 0);
	}

	public Circle(Vec2f location, float radius, Color c)
	{
		super(location, radius * 2f, radius * 2f, c);
		this.setColor(c);
		this.dotPosition = location.plus(new Vec2f(radius / 2f, 0));
	}

	@Override
	public Optional<Collision> collisionWith(Collidable other)
	{
		return inverseOf(other.collisionWithCircle(this));
	}

	@Override
	public Optional<Collision> collisionWithAAB(AAB other)
	{
		float clampedX = Math.max(Math.min(this.getCenterPosition().x, other.getCenterPosition().x + (other.getWidth() / 2)), other.getCenterPosition().x - (other.getWidth() / 2));
		float clampedY = Math.max(Math.min(this.getCenterPosition().y, other.getCenterPosition().y + (other.getHeight() / 2)), other.getCenterPosition().y - (other.getHeight() / 2));
		if(this.getCenterPosition().minus(clampedX, clampedY).mag2() < (this.getRadius() * this.getRadius()))
		{
			Collision collision = new DefaultCollision(new Vec2f(clampedX, clampedY), this.mtvFromAAB(other));
			return Optional.of(collision);
		}
		return Optional.absent();
	}

	@Override
	public Optional<Collision> collisionWithCircle(Circle other)
	{
		Vec2f apart = other.getCenterPosition().minus(this.getCenterPosition());
		if(apart.mag2() < ((this.getRadius() + other.getRadius()) * (this.getRadius() + other.getRadius())))
		{
			float mag = apart.mag();
			Vec2f napart = apart.sdiv(mag);
			Collision collision = new DefaultCollision(this.getCenterPosition().plus(napart.smult(this.getRadius())), napart.smult((mag - (other.getRadius() + this.getRadius()))));
			return Optional.of(collision);
		}
		return Optional.absent();
	}

	@Override
	public Optional<Collision> collisionWithPoint(Point other)
	{
		return this.collisionWithCircle(other);
	}

	@Override
	public Optional<Collision> collisionWithPolygon(Polygon other)
	{
		return inverseOf(other.collisionWithCircle(this));
	}

	private boolean contains(Vec2f other)
	{
		float sqdist = ((this.getCenterPosition().x - other.x) * (this.getCenterPosition().x - other.x)) + ((this.getCenterPosition().y - other.y) * (this.getCenterPosition().y - other.y));
		return sqdist < (this.getRadius() * this.getRadius());
	}

	@Override
	public Collidable copy()
	{
		return new Circle(this.getCenterPosition(), this.getRadius(), this.getColor());
	}

	@Override
	public Optional<Float> distanceAlong(Ray r)
	{
		Vec2f relativeCenter = this.getCenterPosition().minus(r.getStartLocation());
		float projection = relativeCenter.dot(r.getDirection());
		Vec2f projectionPoint = r.getAtDistance(projection);
		if(!(this.collisionWithPoint(new Point(projectionPoint)).isPresent() || ((projection < 0) && !this.collisionWithPoint(new Point(r.getStartLocation())).isPresent())))
			return Optional.absent();

		float x2 = this.getCenterPosition().minus(projectionPoint).mag2();
		float r2 = this.getRadius() * this.getRadius();

		if(this.collisionWithPoint(new Point(r.getStartLocation())).isPresent())
			return Optional.of(projection + (float) Math.sqrt(r2 - x2));
		else return Optional.of(projection - (float) Math.sqrt(r2 - x2));
	}

	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(this.getColor());
		g.fillOval((int) (this.getCenterPosition().x - this.getRadius()), (int) (this.getCenterPosition().y - this.getRadius()), (int) this.getWidth(), (int) this.getHeight());
		g.setColor(this.dotColor);
		g.drawLine((int) this.dotPosition.x, (int) this.dotPosition.y, (int) this.dotPosition.x, (int) this.dotPosition.y);
	}

	public float getRadius()
	{
		return this.getHeight() / 2.0f;
	}

	@Override
	public float getRotation()
	{
		return this.rotation;
	}

	@Override
	public void setRotation(float f)
	{
		this.rotation = f;
		this.positionDot();
	}

	@Override
	Optional<Collision> minimumCollisionFrom(Pair<Vec2f, Float> mtv, PCollidable other)
	{
		return inverseOf(other.minimumCollisionFromCircle(Pair.tuple(mtv.getA(), -1 * mtv.getB()), this));
	}

	@Override
	Optional<Collision> minimumCollisionFromCircle(Pair<Vec2f, Float> mtv, Circle other)
	{
		Vec2f apart = other.getCenterPosition().minus(this.getCenterPosition());
		if(apart.mag2() < ((this.getRadius() + other.getRadius()) * (this.getRadius() + other.getRadius())))
		{
			float mag = apart.mag();
			Vec2f napart = apart.sdiv(mag);
			Collision collision = new DefaultCollision(this.getCenterPosition().plus(napart.smult(this.getRadius())), napart.smult((mag - (other.getRadius() + this.getRadius()))));
			return Optional.of(collision);
		}
		return Optional.absent();
	}

	@Override
	Optional<Collision> minimumCollisionFromPolygon(Pair<Vec2f, Float> mtv, Polygon other)
	{
		return inverseOf(other.minimumCollisionFromCircle(Pair.tuple(mtv.getA(), -1 * mtv.getB()), this));
	}

	@Override
	public float momentOfInertia()
	{
		return (this.getRadius() * this.getRadius()) / 2.0f;
	}

	private Vec2f mtvFromAAB(AAB other)
	{
		if(new Point(this.getCenterPosition()).collisionWithAAB(other).isPresent()) //Circle is inside AAB
		{
			float xDiff = Math.abs(other.getCenterPosition().x - this.getCenterPosition().x);
			float yDiff = Math.abs(other.getCenterPosition().y - this.getCenterPosition().y);

			float needXDiff = this.getRadius() + (other.getWidth() / 2f);
			float needYDiff = this.getRadius() + (other.getHeight() / 2f);

			if(Math.abs(xDiff - needXDiff) < Math.abs(yDiff - needYDiff))
			{
				boolean negate = other.getCenterPosition().x > this.getCenterPosition().x;
				return new Vec2f((negate ? -1f : 1f) * (needXDiff - xDiff), 0);
			} else
			{
				boolean negate = other.getCenterPosition().y > this.getCenterPosition().y;
				return new Vec2f(0, (negate ? -1f : 1f) * (needYDiff - yDiff));
			}
		} else
		{
			return this.mtvFromCircle(new Point(Math.min(other.getCenterPosition().x + (other.getWidth() / 2), Math.max(other.getCenterPosition().x - (other.getWidth() / 2), this.getCenterPosition().x)), Math.min(other.getCenterPosition().y + (other.getHeight() / 2), Math.max(other.getCenterPosition().y - (other.getHeight() / 2), this.getCenterPosition().y))));
		}
	}

	Vec2f mtvFromCircle(Circle other)
	{
		Vec2f toCenter = other.getCenterPosition().minus(this.getCenterPosition());
		float dist = toCenter.mag();
		float newDist = (other.getRadius() + this.getRadius()) - dist;
		return toCenter.normalized().smult(-newDist);
	}

	private void positionDot()
	{
		this.dotPosition = this.getCenterPosition().plus(Vec2f.fromPolar(this.rotation, this.getRadius() / 2f));
	}

	@Override
	Interval projectionOnto(Vec2f axis, Function<Vec2f, Float> projector)
	{
		Function<Vec2f, Vec2f> p2p = new PointProjector(Vec2f.ZEROES, axis);
		List<Vec2f> pair = NLists.of(this.getCenterPosition().plus(axis.smult(this.getRadius())), this.getCenterPosition().minus(axis.smult(this.getRadius())));
		Vec2f min = NObjects.minimaOf(pair, Functions.compose(projector, p2p)).get().get(0);
		Vec2f max;
		if(min == pair.get(0)) max = pair.get(1);
		else max = pair.get(0);

		Interval theirIt = Interval.of(projector.apply(p2p.apply(min)), projector.apply(p2p.apply(max)));
		return theirIt;
	}

	@Override
	public void rotate(float f)
	{
		this.rotation = (float) ((this.rotation + f) % (2 * Math.PI));
		this.positionDot();
	}

	@Override
	public void setCenterPosition(Vec2f position)
	{
		super.setCenterPosition(position);
		positionDot();
	}

	@Override
	public void setColor(Color c)
	{
		if((c.getBlue() + c.getGreen() + c.getRed()) < 50) this.dotColor = c.brighter();
		else this.dotColor = c.darker();
		super.setColor(c);
	}

	@Override
	public void setHeight(float height)
	{
		super.setHeight(height);
		super.setWidth(height);
		this.positionDot();
	}

	@Override
	public void setWidth(float width)
	{
		super.setHeight(width);
		super.setWidth(width);
		this.positionDot();
	}

	@Override
	public String toString()
	{
		return "Circle{" + getCenterPosition() + " with radius " + getRadius() + "}";
	}
}
