package nschank.engn.shape;

import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Point;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Created by Nicolas Schank for package nschank.engn.shape
 * Created on 9 Sep 2013
 * Last updated on 26 May 2014
 *
 * An AbstractDrawable that is a simple, filled rectangle.
 *
 * @author Nicolas Schank
 * @version 2013 09 09
 * @since 2013 09 09 5:46 PM
 */
public class FilledRectangle extends AbstractDrawable
{
	/**
	 * Creates a Filled Rectangle object
	 *
	 * @param location
	 * 		- The centre position of this rectangle
	 * @param width
	 * 		- the width of this rectangle
	 * @param height
	 * 		- the height of this rectangle
	 * @param c
	 * 		- the Color of this rectangle, both border and fill
	 */
	public FilledRectangle(Dimensional location, float width, float height, Color c)
	{
		super(location, width, height, c);
	}

	/**
	 * Creates a Rectangle of the given color. It is at the origin with a width and height of 0.
	 *
	 * @param color
	 * 		A Color to make a new FilledRectangle from
	 *
	 * @return A mutable Drawable, perfect for initializing some Rectangles with different widths and heights.
	 */
	public static Drawable of(Color color)
	{
		return new FilledRectangle(Point.ZERO_2D, 0, 0, color);
	}

	@Override
	public void draw(Graphics2D g)
	{
		int xc = (int) this.getCenterPosition().getCoordinate(0);
		int yc = (int) this.getCenterPosition().getCoordinate(1);
		int width = (int) this.getWidth();
		int height = (int) this.getHeight();

		g.setColor(this.getColor());
		g.fillRect(xc - width / 2, yc - height / 2, width, height);
	}
}
