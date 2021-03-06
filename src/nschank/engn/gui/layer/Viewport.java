package nschank.engn.gui.layer;

import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
import nschank.util.Interval;


/**
 * Created by Nicolas Schank for package nschank.engn.gui.layer
 * Created on 25 May 2014
 * Last updated on 25 May 2014
 *
 * A Viewport is a Layer that takes up a specific area and cuts off any elements in the Layer which do not fit within
 * that area. It has a centre point (in pixels) and a size (in pixels) that relate to the Screen and its x-y coordinate
 * plane. It also has a center point (in game units) and a "zoom" (the number of pixels per game unit), which abstracts
 * out the translation of game units to pixels visually.
 *
 * @author nschank, Brown University
 * @version 1.1
 */
public interface Viewport extends Layer
{
	/**
	 * @param gameLoc
	 * 		A game location within the Viewport
	 *
	 * @return A pixel location relating to that game location
	 */
	public Dimensional gameLocationToPixel(Dimensional gameLoc);
	/**
	 * @return The center position, in pixels, of this viewport's outer square
	 */
	public Dimensional getCenterPosition();
	/**
	 * Changes the location of this Viewport
	 *
	 * @param newPositon
	 * 		where the center of this Viewport should now be
	 */
	public void setCenterPosition(Dimensional newPositon);
	/**
	 * @return The size, in pixels (as an x-y {@code Dimensional}), of this Viewport on the screen
	 */
	public Dimensional getSize();
	/**
	 * Changes the size of this Viewport to the given size.
	 *
	 * @param size
	 * 		How big the Viewport should be, in pixels
	 */
	public void setSize(Dimensional size);
	/**
	 * @return The center position of this Viewport's view into the game world, in game units
	 */
	public Dimensional getViewPosition();
	/**
	 * Change the game location that this Viewport is focused on
	 *
	 * @param newView
	 * 		The place that the Viewport should now focus on
	 */
	public void setViewPosition(Dimensional newView);
	/**
	 * The DefaultInterval over which this Viewport extends.
	 *
	 * @return An DefaultInterval (of the x axis and in game units)
	 */
	public Interval getXInterval();
	/**
	 * The DefaultInterval over which this Viewport extends.
	 *
	 * @return An DefaultInterval (of the y axis and in game units)
	 */
	public Interval getYInterval();
	/**
	 * Must be an integer because pixels are integral.
	 *
	 * @return The current zoom value of this Viewport, in pixels per game unit
	 */
	public int getZoom();
	/**
	 * Sets the zoom (in pixels per game unit) of this Viewport. Allows a double because zoom is rounded when used, but
	 * may be non-integral when calculated.
	 *
	 * @param zoom
	 * 		The new value of this Viewport
	 */
	public void setZoom(double zoom);
	/**
	 * @param pixels
	 * 		A pixel location onscreen, as a Dimensional (x,y)
	 *
	 * @return The game unit location that that pixel refers to
	 */
	public Dimensional pixelToGameLocation(Dimensional pixels);
	/**
	 * Move the gui position using a {@code Vector}.
	 *
	 * @param shiftAmount
	 * 		the amount of movement for the gui position to take
	 */
	public void shiftViewPosition(Vector shiftAmount);
	/**
	 * Zooms in on the viewport, using the zoom factor in getZoomFactor()
	 */
	public void zoomIn();
	/**
	 * Zooms out of the viewport, using the zoom factor in getZoomFactor()
	 */
	public void zoomOut();
}
