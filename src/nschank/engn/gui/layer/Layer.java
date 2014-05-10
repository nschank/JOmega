package nschank.engn.gui.layer;

import nschank.collect.dim.Dimensional;

import java.awt.Graphics2D;


/**
 * Created by Nicolas Schank for package nschank.engn.gui
 * Created on 09 May 2014
 * Last updated on 09 May 2014
 *
 * A Layer which is able to draw itself. Layers are used by AbstractScreens
 * as an abstraction of elements on a screen and interactions with a user.
 * Layers given to an AbstractScreen are guaranteed to be ticked and to be
 * drawn on each tick, and to be told of screen size updates.
 *
 * @author nschank, Brown University
 * @version 1.1
 */
public interface Layer
{
	/**
	 * How to draw this Layer, using a Graphics object.
	 * Do not clear any areas of the Graphics object that the Layer
	 * does not occlude, as this will cause other Layers to appear incorrectly.
	 *
	 * @param g
	 * 		The Graphics2D object to use.
	 */
	public void onDraw(Graphics2D g);

	/**
	 * All time-related actions should occur as an extension of onTick. onTick is called
	 *  every few billionths of a second, and sends along the amount of time passed.
	 * @param nanosSinceLatTick The number of billionths of seconds that have passed since the last call
	 */
	public void onTick(long nanosSinceLatTick);

	/**
	 * Called every time that the Screen changes size. This includes
	 *  the first time the Screen appears, so no initialization by
	 *  size is really necessary.
	 * @param size The new size of the entire window, in pixels
	 */
	public void resize(Dimensional size);
}
