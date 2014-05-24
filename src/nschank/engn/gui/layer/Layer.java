package nschank.engn.gui.layer;

import nschank.collect.dim.Dimensional;

import java.awt.Graphics2D;


/**
 * Created by Nicolas Schank for package nschank.engn.gui.layer
 * Created on 09 May 2014
 * Last updated on 20 May 2014
 *
 * A Layer which is able to draw itself. Layers are used by AbstractScreens  as an abstraction of a display method,
 * similar in spirit to a LayoutManager. Layers may be specialized for a certain screen element that needs to be drawn
 * and resized (and may change onTick as well); may be used as in an ActionLayer in which only the onTick method is used;
 * or may be used more generally, as in some of the more common subclasses used in this package.
 *
 * @author nschank, Brown University
 * @version 1.1
 */
public interface Layer
{
	/**
	 * How to draw this Layer, using a Graphics object. Do not clear any areas of the Graphics object that the Layer
	 * does not occlude, as this will cause other Layers to appear incorrectly.
	 *
	 * @param g
	 * 		The Graphics2D object to use.
	 */
	public void draw(Graphics2D g);

	/**
	 * All time-related actions should occur as an extension of onTick. onTick is called every few billionths of a second,
	 * and sends along the amount of time passed.
	 *
	 * @param nanosSinceLatTick
	 * 		The number of billionths of seconds that have passed since the last call
	 */
	public void onTick(long nanosSinceLatTick);

	/**
	 * Called every time that the Screen changes size. This includes  the first time the Screen appears, so no initialization
	 * by size is really necessary.
	 *
	 * @param size
	 * 		The new size of the entire window, in pixels (x,y)
	 */
	public void resize(Dimensional size);
}
