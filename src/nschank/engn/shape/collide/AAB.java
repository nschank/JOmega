package nschank.engn.shape.collide;

import com.google.common.base.Optional;
import cs195n.Vec2f;
import nschank.util.Interval;
import nschank.util.NLists;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;


/**
 * Tou1
 *
 * @author Nicolas Schank
 * @version 2013 09 28
 * @since 2013 09 28 9:26 PM
 */
public class AAB extends Polygon
{
	@Override
	public Optional<Collision> collisionWith(Collidable other)
	{
		return inverseOf(other.collisionWithAAB(this));
	}

	@Override
	List<Vec2f> axes()
	{
		return NLists.of(new Vec2f(0, 1), new Vec2f(1, 0));
	}

	@Override
	public Optional<Collision> collisionWithAAB(AAB other)
	{
		if(!this.xInterval().isIntersecting(other.xInterval()) || !this.yInterval().isIntersecting(other.yInterval()))
			return Optional.absent();
		else return super.collisionWithAAB(other);
//		Vec2f point = new Vec2f(this.xInterval().collision(other.xInterval()),
//				this.yInterval().collision(other.yInterval()));
//		float diffX = this.xInterval().getMinimumTranslation(other.xInterval());
//		float diffY = this.yInterval().getMinimumTranslation(other.yInterval());
//		if(Math.abs(diffX) < Math.abs(diffY))
//			return Optional.of((Collision)new DefaultCollision(point, new Vec2f(1,0).smult(diffX)));
//		return Optional.of((Collision)new DefaultCollision(point, new Vec2f(0,1).smult(diffY)));
	}

	@Override
	public Optional<Collision> collisionWithCircle(Circle other)
	{
		return inverseOf(other.collisionWithAAB(this));
	}

	@Override
	public Optional<Collision> collisionWithPoint(Point other)
	{
		return inverseOf(other.collisionWithAAB(this));
	}

	@Override
	public Optional<Float> distanceAlong(Ray r)
	{
		float shortestCollision = -1;

		for(int i = -1; i < (this.points.size() - 1); i++)
		{
			Vec2f start = this.points.get((this.points.size() + i) % this.points.size());
			Vec2f end = this.points.get((this.points.size() + i + 1) % this.points.size());
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
		return new AAB(this.getCenterPosition(), this.getWidth(), this.getHeight(), this.getColor());
	}

	@Override
	public float momentOfInertia()
	{
		return 0.0f;
	}

	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(this.getColor());
		g.fillRect((int) (this.getCenterPosition().x - (this.getWidth() / 2f)), (int) (this.getCenterPosition().y - (this.getHeight() / 2f)), (int) this.getWidth(), (int) this.getHeight());
	}

	@Override
	boolean contains(Vec2f other)
	{
		return Interval.within(getMinX(), getMaxX(), other.x) && Interval.within(getMinY(), getMaxY(), other.y);
	}

	@Override
	public List<Vec2f> getPoints()
	{
		return NLists.of(new Vec2f(getMinX(), getMinY()), new Vec2f(getMinX(), getMaxY()), new Vec2f(getMaxX(), getMinY()), new Vec2f(getMaxX(), getMaxY()));
	}

	public AAB(Vec2f location, float width, float height, Color c)
	{
		super(location, width, height, c, new Vec2f(-1, -1), new Vec2f(1, -1), new Vec2f(1, 1), new Vec2f(-1, 1));
	}

	public AAB(float left, float right, float top, float bottom, Color c)
	{
		super(new Vec2f(left + ((right - left) / 2f), top + ((bottom - top) / 2f)), right - left, bottom - top, c, new Vec2f(-1, -1), new Vec2f(1, -1), new Vec2f(1, 1), new Vec2f(-1, 1));
	}

	public float getMinX()
	{
		return this.getCenterPosition().x - (this.getWidth() / 2);
	}

	public float getMaxX()
	{
		return this.getCenterPosition().x + (this.getWidth() / 2);
	}

	public float getMinY()
	{
		return this.getCenterPosition().y - (this.getHeight() / 2);
	}

	public float getMaxY()
	{
		return this.getCenterPosition().y + (this.getHeight() / 2);
	}

	@Override
	public String toString()
	{
		return "AAB={" + getCenterPosition() + " with width " + getWidth() + " and height " + getHeight() + '}';
	}

	@Override
	public Interval xInterval()
	{
		return Interval.about(this.getCenterPosition().x, this.getWidth());
	}

	@Override
	public Interval yInterval()
	{
		return Interval.about(this.getCenterPosition().y, this.getHeight());
	}
}
