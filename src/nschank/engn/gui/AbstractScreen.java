package nschank.engn.gui;

import nschank.collect.dim.Dimensional;
import nschank.engn.gui.layer.AbstractLayer;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by Nicolas Schank for package nschank.engn.gui.
 * Created on 08 May 2014
 * Last updated on 08 May 2014
 *
 * A Screen is a usable element in the Omega Engine. This does some of the work for
 * those who would make one.
 *
 * @author nschank, Brown University
 * @version 1.1
 */
public abstract class AbstractScreen implements Screen
{
	private List<AbstractLayer> layers;
	private final Application myApp;
	/* We need to allow layers to ask for other layers to be removed,
	 * especially themselves.
	 */
	private Set<AbstractLayer> toRemove;

	/**
	 * Creates a Screen
	 *
	 * @param engine
	 * 		- What Application this Screen receives information from
	 */
	public AbstractScreen(Application engine)
	{
		this.myApp = engine;
		this.layers = new ArrayList<>();
		this.toRemove = new HashSet<>();
	}

	/**
	 * Adds a Layer to this Screen. Layers are always placed atop previous layers.
	 * Layers are an abstraction used by this implementation to represent visible
	 * objects, similar to layers in, for example, Flash or Photoshop.
	 *
	 * Generally, the game screen is implemented to include some singular, central
	 * Layer which the player interacts with primarily. Other examples of Layers
	 * might include a button, dropdown, or
	 *
	 * @param layer
	 */
	public final void addLayer(AbstractLayer layer)
	{
		this.layers.add(layer);
	}

	/**
	 * @return The Application that this Screen is living under.
	 */
	public Application getApplication()
	{
		return this.myApp;
	}

	/**
	 * Whether or not to pass draw events below to other screens. By default, does not.
	 *
	 * @return false
	 */
	@Override
	public boolean hasTransparency()
	{
		return false;
	}

	/**
	 * Kills this screen and removes it from the Application.
	 */
	@Override
	public void kill()
	{
		this.myApp.removeGameScreen(this);
	}

	/**
	 * Whether or not this Screen wants to receive all ticks, even if the Screens above it are not
	 * tick-transparent. By default, it does not.
	 *
	 * @return false
	 */
	@Override
	public boolean needsAllTicks()
	{
		return false;
	}

	/**
	 * Draws all the layers in this Screen.
	 * The entire point of the Layer system is to
	 *
	 * @param g
	 * 		- Graphics object on which to draw.
	 */
	public final void onDraw(Graphics2D g)
	{
		for(AbstractLayer l : this.layers)
			l.draw(g);
	}

	/**
	 * Processes key pressed events. By default, does nothing.
	 *
	 * @param e
	 * 		- The KeyEvent to process.
	 */
	@Override
	public void onKeyPressed(KeyEvent e)
	{

	}

	/**
	 * Processes key released events. By default, does nothing.
	 *
	 * @param e
	 * 		- The KeyEvent to process.
	 */
	@Override
	public void onKeyReleased(KeyEvent e)
	{

	}

	/**
	 * Processes key typed events. By default, does nothing.
	 *
	 * @param e
	 * 		- The KeyEvent to process.
	 */
	@Override
	public void onKeyTyped(KeyEvent e)
	{

	}

	/**
	 * Processes mouse clicked events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	@Override
	public void onMouseClicked(MouseEvent e)
	{

	}

	/**
	 * Processes mouse dragged events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	@Override
	public void onMouseDragged(MouseEvent e)
	{

	}

	/**
	 * Processes mouse moved events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	@Override
	public void onMouseMoved(MouseEvent e)
	{

	}

	/**
	 * Processes mouse pressed events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	@Override
	public void onMousePressed(MouseEvent e)
	{

	}

	/**
	 * Processes mouse released events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	@Override
	public void onMouseReleased(MouseEvent e)
	{

	}

	/**
	 * Processes mouse wheel moved events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	@Override
	public void onMouseWheelMoved(MouseWheelEvent e)
	{

	}

	/**
	 * Automatically alerts all Layers of the resize.
	 *
	 * @param newSize
	 * 		- The new size of the screen in pixels.
	 */
	@Override
	public void onResize(Dimensional newSize)
	{
		for(AbstractLayer l : this.layers)
			l.resize(newSize);
	}

	/**
	 * Ticks all layers, then removes any layers that have been removed.
	 *
	 * @param nanoSinceLastTick
	 * 		- Billionths of a second since last tick.
	 */
	@Override
	public final void onTick(long nanoSinceLastTick)
	{
		for(AbstractLayer l : this.layers)
			l.onTick(nanoSinceLastTick);
		this.layers.removeAll(this.toRemove);
		this.toRemove.clear();
	}

	/**
	 * Whether or not to pass key events below to other Screens when this Screen receives a tick. By default, does not.
	 *
	 * @return false
	 */
	@Override
	public boolean passesKeyEventsBelow()
	{
		return false;
	}

	/**
	 * Whether or not to pass mouse events below to other Screens when this Screen receives a tick. By default, does not.
	 *
	 * @return false
	 */
	@Override
	public boolean passesMouseEventsBelow()
	{
		return false;
	}

	/**
	 * Whether or not to pass ticks below to other Screens when this Screen receives a tick. By default, does not.
	 *
	 * @return false
	 */
	@Override
	public boolean passesTicksBelow()
	{
		return false;
	}

	/**
	 * Removes a Layer from the Screen. Takes effect on next tick.
	 *
	 * @param layer
	 * 		The layer to remove.
	 *
	 * @return true iff the layer was not due to be removed already
	 */
	public final boolean removeLayer(AbstractLayer layer)
	{
		return this.toRemove.add(layer);
	}
}
