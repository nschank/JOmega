package nschank.engn.shape;

import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Point;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


/**
 * Created by Nicolas Schank for package nschank.engn.shape
 * Created on 9 Sep 2013
 * Last updated on 26 May 2014
 *
 * A Drawable Object for a Screen that is a String. As with all AbstractDrawables, it is centre-focused. Width, when assigned,
 * is not used, but height is used as font size, and width follows directly from that.
 *
 * setHeight sets the font size. setWidth has no effect and will be undone on a draw.
 *
 * @author nschank, Brown University
 * @version 2.0
 */
public class DrawableString extends AbstractDrawable
{
	//The String to write
	private String write;

	/**
	 * Creates a Drawable String
	 *
	 * @param position
	 * 		The center position of the String to draw
	 * @param font
	 * 		The font size, as would be used in new Font(_,_,font)
	 * @param color
	 * 		Color of the String
	 * @param toWrite
	 * 		The String to write
	 */
	public DrawableString(Dimensional position, float font, Color color, String toWrite)
	{
		super(position, 0, font, color);
		this.write = toWrite;
	}

	public static Drawable of(String s)
	{
		return of(s, Color.BLACK);
	}

	/**
	 * Creates a DrawableString of a particular String and Color. Located at the origin and with a font size of 0pt.
	 * Use setHeight to set the font size.
	 *
	 * @param s
	 * 		A String to draw
	 * @param c
	 * 		A Color with which to write
	 *
	 * @return A Drawable object that is a DrawableString of the given String and Color; made for initializing quickly.
	 */
	public static Drawable of(String s, Color c)
	{
		return new DrawableString(Point.ZERO_2D, 0, c, s);
	}

	/**
	 * Draws this String onto a Graphics object. Updates the width to the correct width.
	 */
	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(this.getColor());
		g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), (int) this.getHeight()));
		Rectangle2D l = g.getFontMetrics().getStringBounds(this.write, g);
		int descent = g.getFontMetrics().getDescent();
		this.setWidth(l.getWidth());
		g.drawString(this.write, (int) (this.getCenterPosition().getCoordinate(0) - (l.getWidth() / 2)), (int) ((this.getCenterPosition().getCoordinate(1) + (l.getHeight() / 2)) - descent));
	}

	/**
	 * @return The string being drawn by this DrawableString
	 */
	public String getString()
	{
		return this.write;
	}

	/**
	 * Sets the string drawn to a new value.
	 *
	 * @param write
	 * 		A new String value
	 */
	public void setString(String write)
	{
		this.write = write;
	}

	/**
	 * @return The internal string being drawn
	 */
	@Override
	public String toString()
	{
		return "DrawableString{" +
				"write='" + this.write + '\'' +
				'}';
	}
}
