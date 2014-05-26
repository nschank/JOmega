package nschank.engn.shape;

import nschank.collect.dim.Dimensional;
import nschank.util.Interval;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Created by Nicolas Schank for package nschank.engn.shape
 * Created on 15 Sep 2013
 * Last modified on 25 May 2014
 *
 * An object that can be drawn onto a Graphics2D object. The more general interface that OmegaShapes follow.
 *
 * Central interface of nschank.engn.shape
 *
 * @author nschank, Brown University
 * @version 3.0
 */
public interface Drawable
{
	/**
	 * A Shape with no size, which does not draw, is black, and is located at the origin.
	 */
	public static final Drawable NOTHING = new NoShape();

	/**
	 * Draws this shape on the Graphics object. Should use the pixel position and size of the shape to figure out where
	 * to draw.
	 *
	 * @param g
	 * 		The Graphics object this shape must be drawn upon.
	 */
	public void draw(Graphics2D g);

	/**
	 * @return The center position of this Drawable object, in a two-dimensional point.
	 */
	public Dimensional getCenterPosition();

	/**
	 * @param centerPosition
	 * 		The next center position of this Drawable object, in a two-dimensional point.
	 */
	public void setCenterPosition(Dimensional centerPosition);

	/**
	 * @return The height of this object. Depending on time, may be in pixels or game units.
	 */
	public double getHeight();

	/**
	 * @param h
	 * 		The new height of this object
	 */
	public void setHeight(double h);
	/**
	 * @return The width of this object. Depending on time, may be in pixels or game units.
	 */
	public double getWidth();
	/**
	 * @param w
	 * 		The new width of this object
	 */
	public void setWidth(double w);
	/**
	 * @return The Color of this object
	 */
	public Color getColor();
	/**
	 * @param c
	 * 		The new Color of this object
	 */
	public void setColor(Color c);

	/**
	 * @return The interval this object intersects on the x-axis
	 */
	public Interval xInterval();

	/**
	 * @return The interval this object intersects on the y-axis
	 */
	public Interval yInterval();
}
