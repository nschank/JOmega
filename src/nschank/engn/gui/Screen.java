package nschank.engn.gui;

import nschank.collect.dim.Dimensional;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;


/**
 * Created by Nicolas Schank for package nschank.engn.gui
 * Created 9 Sep 2013
 * Last updated on 8 May 2014
 *
 * A Screen, a single visible and/or interactable section or element of the Application.
 *
 * @author Nicolas Schank
 * @version 2.0
 */
public interface Screen
{
	/**
	 * Whether or not to pass draw events below to other screens.
	 */
	public boolean hasTransparency();
	/**
	 * Kills this screen and removes it from the Application.
	 */
	public void kill();
	/**
	 * Draws all the layers in this Screen.
	 *
	 * @param g
	 * 		- Graphics object on which to draw.
	 */
	public void onDraw(Graphics2D g);
	/**
	 * Processes key pressed events. By default, does nothing.
	 *
	 * @param e
	 * 		- The KeyEvent to process.
	 */
	public void onKeyPressed(KeyEvent e);
	/**
	 * Processes key released events. By default, does nothing.
	 *
	 * @param e
	 * 		- The KeyEvent to process.
	 */
	public void onKeyReleased(KeyEvent e);
	/**
	 * Processes key typed events. By default, does nothing.
	 *
	 * @param e
	 * 		- The KeyEvent to process.
	 */
	public void onKeyTyped(KeyEvent e);
	/**
	 * Processes mouse clicked events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	public void onMouseClicked(MouseEvent e);
	/**
	 * Processes mouse dragged events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	public void onMouseDragged(MouseEvent e);
	/**
	 * Processes mouse moved events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	public void onMouseMoved(MouseEvent e);
	/**
	 * Processes mouse pressed events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	public void onMousePressed(MouseEvent e);
	/**
	 * Processes mouse released events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	public void onMouseReleased(MouseEvent e);
	/**
	 * Processes mouse wheel moved events. By default, does nothing.
	 *
	 * @param e
	 * 		- The MouseEvent to process.
	 */
	public void onMouseWheelMoved(MouseWheelEvent e);
	/**
	 * Automatically alerts all Layers of the resize.
	 *
	 * @param newSize
	 * 		- The new size of the screen in pixels.
	 */
	public void onResize(Dimensional newSize);
	/**
	 * What to do when a tick occurs.
	 *
	 * @param nanoSinceLastTick
	 * 		- Billionths of a second since last tick.
	 */
	public void onTick(long nanoSinceLastTick);
	/**
	 * Whether or not to pass key events below to other Screens when this Screen receives a tick.
	 */
	public boolean passesKeyEventsBelow();

	/**
	 * Whether or not to pass mouse events below to other Screens when this Screen receives a tick.
	 */
	public boolean passesMouseEventsBelow();

	/**
	 * Whether or not to pass ticks below to other Screens when this Screen receives a tick.
	 */
	public boolean passesTicksBelow();

	/**
	 * Whether or not this Screen wants to receive all ticks, even if the Screens above it are not
	 * tick-transparent.
	 */
	public boolean needsAllTicks();
}
