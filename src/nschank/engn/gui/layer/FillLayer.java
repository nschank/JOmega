package nschank.engn.gui.layer;

import nschank.collect.dim.Dimensional;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * Created by Nicolas Schank for package nschank.engn.gui.layer
 * Created on 24 Sep 2013
 * Last updated on 20 May 2014
 *
 * A Layer (generally added on the bottom, for relatively obvious reasons) which is of a single color, and simply fills
 * the entire Screen with that color. Perfect for backgrounds. Automatically resizes to fill the entire Screen.
 *
 * @author nschank, Brown University
 * @version 2.0
 */
public class FillLayer implements Layer
{
	private int width = 0;
	private int height = 0;
	private Color color;

	/**
	 * Initializes a FillLayer.
	 *
	 * @param color
	 * 		A Color with which to fill this Screen
	 * @param initialSize
	 * 		The initial sizing of this
	 */
	public FillLayer(final Color color, final Dimensional initialSize)
	{
		this.color = color;
		this.width = (int) initialSize.getCoordinate(0);
		this.height = (int) initialSize.getCoordinate(1);
	}

	/**
	 * How to draw this Layer, using a Graphics object. Do not clear any areas of the Graphics object that the Layer
	 * does not occlude, as this will cause other Layers to appear incorrectly.
	 *
	 * @param g
	 * 		The Graphics2D object to use.
	 */
	@Override
	public final void draw(final Graphics2D g)
	{
		g.setColor(this.color);
		g.fillRect(0, 0, this.width, this.height);
	}

	/**
	 * @return The Color this FillLayer is currently filling the Screen with.
	 */
	public Color getColor()
	{
		return this.color;
	}

	/**
	 * Does nothing automatically.
	 *
	 * @param nanosSinceLastTick
	 * 		The number of billionths of seconds since the last time this method has been called.
	 */
	@Override
	public void onTick(final long nanosSinceLastTick)
	{

	}

	/**
	 * Called every time that the Screen changes size. This includes
	 * the first time the Screen appears, so no initialization by
	 * size is really necessary.
	 *
	 * @param size
	 * 		The new size of the entire window, in pixels (x,y)
	 */
	@Override
	public final void resize(final Dimensional size)
	{
		this.width = (int) size.getCoordinate(0);
		this.height = (int) size.getCoordinate(1);
	}

	/**
	 * Changes the color of this FillLayer to the provided Color. Takes immediate effect.
	 *
	 * @param color
	 * 		A new Color with which to fill this Screen
	 */
	public void setColor(final Color color)
	{
		this.color = color;
	}
}
