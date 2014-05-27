package nschank.engn.shape;

import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Point;
import nschank.util.Interval;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Created by Nicolas Schank for package nschank.engn.shape
 * Created on 9 Sep 2013 (originally OmegaShape)
 * Last updated 26 May 2014
 *
 * Implements the Drawable interface in all aspects except drawing. Allows implementations of Drawable to worry about the
 * use of width, height, intervals, color, and center point rather than actually doing the basic outline of those things.
 *
 * @author nschank, Brown University
 * @version 6.0
 */
public abstract class AbstractDrawable implements Drawable
{
	private Color color;
	private double height;
	private Dimensional position;
	private double width;
	private Interval xInterval;
	private Interval yInterval;

	/**
	 * Creates a black AbstractDrawable with no size at position (0,0)
	 */
	protected AbstractDrawable()
	{
		this.position = Point.ZERO_2D;
		this.width = 0;
		this.height = 0;
		this.color = Color.BLACK;
		this.xInterval = Interval.of(0, 0);
		this.yInterval = Interval.of(0, 0);
	}

	/**
	 * Creates an AbstractDrawable using the given information
	 *
	 * @param location
	 * 		- the center position of this nschank.engn.shape
	 * @param width
	 * 		- the width in pixels of this nschank.engn.shape
	 * @param height
	 * 		- the height in pixels of this nschank.engn.shape
	 * @param c
	 * 		- the Color of this nschank.engn.shape
	 */
	protected AbstractDrawable(Dimensional location, double width, double height, Color c)
	{
		if(location.getDimensions() != 2) throw new IllegalArgumentException("Location must be in two dimensions.");
		this.position = new Point(location);
		this.width = width;
		this.height = height;
		this.color = c;
		this.updateIntervals();
	}

	/**
	 * Should use getColor(), getCenterPosition(), getWidth(), and getHeight() to draw onto this graphics object.
	 *
	 * @param g
	 * 		A Graphics object onto which to draw this object
	 */
	@Override
	public abstract void draw(Graphics2D g);

	/**
	 * @return The center position of this Drawable object, in a two-dimensional point.
	 */
	@Override
	public Dimensional getCenterPosition()
	{
		return new Point(this.position);
	}

	/**
	 * @param centerPosition
	 * 		The next center position of this Drawable object, in a two-dimensional point.
	 */
	@Override
	public void setCenterPosition(final Dimensional centerPosition)
	{
		this.position = new Point(centerPosition);
		this.updateIntervals();
	}

	/**
	 * @return The Color of this object
	 */
	@Override
	public Color getColor()
	{
		return this.color;
	}

	/**
	 * @param c
	 * 		The new Color of this object
	 */
	@Override
	public void setColor(final Color c)
	{
		this.color = c;
	}

	/**
	 * @return The height of this object. Depending on time, may be in pixels or game units.
	 */
	@Override
	public double getHeight()
	{
		return this.height;
	}

	/**
	 * @param h
	 * 		The new height of this object
	 */
	@Override
	public void setHeight(final double h)
	{
		this.height = h;
		this.updateIntervals();
	}

	/**
	 * @return The width of this object. Depending on time, may be in pixels or game units.
	 */
	@Override
	public double getWidth()
	{
		return this.width;
	}

	/**
	 * @param w
	 * 		The new width of this object
	 */
	@Override
	public void setWidth(final double w)
	{
		this.width = w;
		this.updateIntervals();
	}

	@Override
	public String toString()
	{
		return "AbstractDrawable{" +
				"position=" + this.position +
				", color=" + this.color +
				", width=" + this.width +
				", height=" + this.height +
				", xInterval=" + this.xInterval +
				", yInterval=" + this.yInterval +
				'}';
	}

	/**
	 * Updates the x and y intervals to reflect the current bounding box of this shape.
	 * To be used after a change to width, height, or center position.
	 */
	private void updateIntervals()
	{
		this.xInterval = Interval.about(this.position.getCoordinate(0), this.width);
		this.yInterval = Interval.about(this.position.getCoordinate(1), this.height);
	}

	/**
	 * @return The interval this object intersects on the x-axis
	 */
	@Override
	public Interval xInterval()
	{
		return this.xInterval;
	}

	/**
	 * @return The interval this object intersects on the y-axis
	 */
	@Override
	public Interval yInterval()
	{
		return this.yInterval;
	}
}
