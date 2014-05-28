package nschank.engn.shape.collide;

import com.google.common.base.Optional;
import cs195n.Vec2f;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Tou1
 *
 * @author Nicolas Schank
 * @version 2013 09 28
 * @since 2013 09 28 9:25 PM
 */
public final class Point extends Circle
{

	@Override
	public Optional<Collision> collisionWith(Collidable other)
	{
		return inverseOf(other.collisionWithPoint(this));
	}

	@Override
	public Optional<Collision> collisionWithCircle(Circle other)
	{
		Vec2f dir = this.getCenterPosition().minus(other.getCenterPosition());
		if(dir.mag2() < (((other.getWidth() / 2) + (other.getWidth() / 2)) * ((other.getWidth() / 2) + (other.getWidth() / 2))))
		{
			Vec2f out = this.mtvFromCircle(other);
			Collision ret = new DefaultCollision(this.getCenterPosition().plus(out), out);
			return Optional.of(ret);
		}
		return Optional.absent();
	}

	@Override
	public Optional<Collision> collisionWithAAB(AAB other)
	{
		float x = this.getCenterPosition().x;
		float y = this.getCenterPosition().y;
		if((x < other.getMaxX()) && (x > other.getMinX()) && (y < other.getMaxY()) && (y > other.getMinY()))
		{
			Vec2f out = this.mtvFromAAB(other);
			Collision ret = new DefaultCollision(this.getCenterPosition().plus(out), out);
			return Optional.of(ret);
		}
		return Optional.absent();
	}

	@Override
	public Optional<Collision> collisionWithPoint(Point other)
	{
		return Optional.absent();
	}

	@Override
	public Optional<Collision> collisionWithPolygon(Polygon other)
	{
		return inverseOf(other.collisionWithPoint(this));
	}

	@Override
	public Optional<Float> distanceAlong(Ray r)
	{
		Vec2f relativeCenter = this.getCenterPosition().minus(r.getStartLocation());
		float projection = relativeCenter.dot(r.getDirection());
		Vec2f projectionPoint = r.getAtDistance(projection);
		if(projectionPoint.equals(this.getCenterPosition())) return Optional.of(projection);
		return Optional.absent();
	}

	@Override
	public float getRotation()
	{
		return 0.0f;
	}

	@Override
	public void setRotation(float f)
	{
		//Do nothing
	}

	@Override
	public void rotate(float f)
	{
		//Do nothing
	}

	@Override
	public Collidable copy()
	{
		return new Point(this.getCenterPosition());
	}

	@Override
	public float momentOfInertia()
	{
		return 0.0f;
	}

	@Override
	public void draw(Graphics2D g)
	{

	}

	private Vec2f mtvFromAAB(AAB other)
	{
		float xDiff = Math.abs(other.getCenterPosition().x - getCenterPosition().x);
		float yDiff = Math.abs(other.getCenterPosition().y - getCenterPosition().y);

		float needXDiff = other.getWidth() / 2f;
		float needYDiff = other.getHeight() / 2f;

		if(Math.abs(xDiff - needXDiff) < Math.abs(yDiff - needYDiff))
		{
			boolean negate = other.getCenterPosition().x > getCenterPosition().x;
			return new Vec2f((negate ? -1f : 1f) * (needXDiff - xDiff), 0);
		} else
		{
			boolean negate = other.getCenterPosition().y > getCenterPosition().y;
			return new Vec2f(0, (negate ? -1f : 1f) * (needYDiff - yDiff));
		}
	}

	public Point(float x, float y)
	{
		this(new Vec2f(x, y));
	}

	public Point(Vec2f location)
	{
		super(location, 0, Color.BLACK);
	}

	@Override
	public void setHeight(float f)
	{

	}

	@Override
	public void setWidth(float f)
	{

	}

	@Override
	public void setColor(Color c)
	{

	}
}
