package nschank.engn.gui;

import nschank.collect.dim.Dimensional;
import nschank.engn.gui.layer.Layer;

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
 * A Screen is a usable element in the Omega Engine. This implementation does some of the work for
 * those who would make one; specifically:
 * 	-  Uses the Layer abstraction (and the related subpackage .layer) to
 * 	   represent a single display method within the screen.
 * 	-  Defaults to being nontransparent and not allowing events to pass through this screen.
 * 	-  Performs no action on any event
 *
 * @author nschank, Brown University
 * @version 1.1
 */
public abstract class AbstractScreen implements Screen
{
	private final List<Layer> layers;
	private final Application myApp;

	/* DUE TO CONCURRENCY PROBLEMS PRESENTED BY addLayer AND removeLayer */
	private final Set<Layer> toRemove;
	private final Set<Layer> toAdd;

	/**
	 * Creates a Screen; AbstractScreens should be able to kill themselves,
	 *  which requires asking the Application to forget about itself.
	 *
	 * @param engine
	 * 		- What Application this Screen receives information from
	 */
	protected AbstractScreen(Application engine)
	{
		this.myApp = engine;
		this.layers = new ArrayList<>();
		this.toRemove = new HashSet<>();
		this.toAdd = new HashSet<>();
	}

	/**
	 * Adds a Layer to this Screen. Layers are always placed atop previous layers.
	 * Layers are an abstraction used by this implementation to represent a display
	 * method for different elements onscreen. They can be used to represent a single
	 * element (such as a button), but are more commonly used similarly to a LayoutManager.
	 *
	 * For more information, see the Layer class.
	 *
	 * Can be used safely by child layers to add new layers.
	 *
	 * @param layer A Layer to add to this Screen, atop all other Layers.
	 */
	public final void addLayer(Layer layer)
	{
		this.toAdd.add(layer);
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
	 * The entire point of the Layer system is to abstract away different methods
	 *  of displaying different objects; Layers can be thought of as similar to, but
	 *  (hopefully) simpler than, LayoutManagers in Swing. As such, any attempt to draw
	 *  or display anything should be done through a Layer that has been added.
	 *
	 * @param g
	 * 		- Graphics object on which to draw.
	 */
	public final void onDraw(Graphics2D g)
	{
		for(Layer l : this.layers)
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
	 * Automatically alerts all Layers of the resize. Resizing should
	 *  be relegated to Layers, not Screens, so this method is final.
	 *
	 * @param newSize
	 * 		- The new size of the screen in pixels.
	 */
	@Override
	public final void onResize(Dimensional newSize)
	{
		for(Layer l : this.layers)
			l.resize(newSize);
	}

	/**
	 * Ticks all layers, then removes any layers that have been removed.
	 * Similar to drawing, Layers are meant to abstract away the need for Screens
	 *  to have individual onTick methods. If a Screen needs to do anything on each
	 *  tick, a Layer which does no drawing (such as an {@code ActionLayer}) would suffice perfectly.
	 *
	 * @param nanoSinceLastTick
	 * 		- Billionths of a second since last tick.
	 */
	@Override
	public final void onTick(long nanoSinceLastTick)
	{
		//Add all Layers added in last tick
		this.layers.addAll(this.toAdd);
		this.toAdd.clear();

		//Run a tick in all Layers
		for(Layer l : this.layers)
			l.onTick(nanoSinceLastTick);

		//Remove all Layers to be removed from the previous tick.
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
	 * Removes a Layer from the Screen. Takes effect at the end of
	 *  this tick.
	 *
	 * @param layer
	 * 		The layer to remove.
	 *
	 * @return true iff the layer was not due to be removed already
	 */
	public final boolean removeLayer(Layer layer)
	{
		return this.toRemove.add(layer);
	}
}
