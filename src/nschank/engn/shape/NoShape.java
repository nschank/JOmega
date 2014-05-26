package nschank.engn.shape;

import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Point;
import nschank.util.Interval;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Created by Nicolas Schank for package nschank.engn.shape
 * Created on 21 Sep 2013
 * Last updated on 26 May 2014
 *
 * @author nschank, Brown University
 * @version 2.1
 */
final class NoShape implements Drawable
{
	static final Drawable NO_SHAPE = new NoShape();

	/**
	 * Draws this shape on the Graphics object. Should use the pixel position and size of the shape to figure out where
	 * to draw.
	 *
	 * No-op.
	 *
	 * @param g
	 * 		The Graphics object this shape must be drawn upon.
	 */
	@Override
	public void draw(Graphics2D g)
	{

	}

	/**
	 * @return The center position of this Drawable object, in a two-dimensional point.
	 */
	@Override
	public Dimensional getCenterPosition()
	{
		return Point.ZERO_2D;
	}

	/**
	 * No-op.
	 */
	@Override
	public void setCenterPosition(final Dimensional centerPosition)
	{

	}

	/**
	 * @return 0
	 */
	@Override
	public double getHeight()
	{
		return 0;
	}

	/**
	 * No-op.
	 */
	@Override
	public void setHeight(final double h)
	{

	}

	/**
	 * @return 0
	 */
	@Override
	public double getWidth()
	{
		return 0;
	}

	/**
	 * No-op.
	 */
	@Override
	public void setWidth(final double w)
	{

	}

	/**
	 * @return Color.BLACK
	 */
	@Override
	public Color getColor()
	{
		return Color.BLACK;
	}

	/**
	 * No-op
	 */
	@Override
	public void setColor(final Color c)
	{

	}

	/**
	 * @return A zero interval.
	 */
	@Override
	public Interval xInterval()
	{
		return Interval.about(0, 0);
	}

	/**
	 * @return A zero interval.
	 */
	@Override
	public Interval yInterval()
	{
		return Interval.about(0, 0);
	}

	/**
	 * Creates a NoShape. Used to define Drawable.NOTHING
	 */
	private NoShape()
	{

	}
}
