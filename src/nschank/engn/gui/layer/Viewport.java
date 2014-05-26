package nschank.engn.gui.layer;

import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Point;
import nschank.collect.dim.Vector;
import nschank.engn.shape.Drawable;
import nschank.util.Interval;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;


/**
 * Created by Nicolas Schank for package nschank.engn.gui.layer
 * Created on 12 Sep 2013.
 * Last updated on 24 May 2014.
 *
 * A Viewport is a Layer that takes up a specific area and cuts off any elements in the Layer which do not fit within
 * that area.
 *
 * @author nschank, Brown University
 * @version 3.0
 */
public abstract class Viewport extends AbstractLayer
{
	/**
	 * The highest zoom scale (in pixels per game unit) to which the Viewport class defaults
	 */
	public static final int DEFAULT_CLOSEST_ZOOM = 50;//pixels per game unit
	/**
	 * The lowest zoom scale (in pixels per game unit) to which the Viewport class defaults
	 */
	public static final int DEFAULT_FURTHEST_ZOOM = 5;//pixels per game unit
	/**
	 * The amount by which the zoom scale multiplies/divides when zooming in or out.
	 */
	public static final double DEFAULT_SCALE_FACTOR = 1.25f;

	//
	private Vector centerPos;
	private double scaleAmount; //pixels per game unit
	private Dimensional size;
	private Interval vX;
	private Interval vY;
	private Vector viewPosition;


	/**
	 * Creates a Viewport at a particular position on the Screen {@code centerPos}, a particular total size in pixels
	 * {@code size}, focused on a particular position within the game {@code viewPosition}, and at a particular
	 * {@code scale} measured in pixels per game unit.
	 *
	 * @param centerPos
	 * 		Where the Viewport should be, in pixels (centerpoint)
	 * @param size
	 * 		How big the Viewport should be, in pixels
	 * @param viewPosition
	 * 		What the Viewport should be looking at, in game location units
	 * @param scale
	 * 		How big a single game location unit should be, in pixels
	 */
	public Viewport(Dimensional centerPos, Dimensional size, Dimensional viewPosition, double scale)
	{
		super();
		this.centerPos = new Vector(centerPos);
		this.size = new Point(size);
		this.viewPosition = new Vector(viewPosition);
		this.scaleAmount = scale;

		assert this.closestZoom() >= this.furthestZoom();

		this.setIntervals();
	}

	/**
	 * Override to change how close this viewport will allowed to zoom. Must satisfy the invariant that closestZoom >=
	 * furthestZoom.
	 *
	 * @return The closest zoom (in pixels per game unit) which this Viewport will allow
	 */
	protected int closestZoom()
	{
		return DEFAULT_CLOSEST_ZOOM;
	}

	/**
	 * Draws all elements in this Layer that are within this Viewport and are visible; in order to improve runtime, only
	 * draws things that are returned by the iterator() method.
	 *
	 * @param g
	 * 		A Graphics object on which to draw this Viewport
	 */
	@Override
	public void draw(Graphics2D g)
	{
		Shape originalClip = g.getClip();
		g.setClip(this.getClip());

		for(Drawable o : this)
		{
			double originalWidth = o.getWidth();
			double originalHeight = o.getHeight();
			Dimensional originalLocation = o.getCenterPosition();

			o.setCenterPosition(this.gameLocationToPixel(new Vector(originalLocation)));
			o.setWidth(originalWidth * this.getZoom());
			o.setHeight(originalHeight * this.getZoom());

			o.draw(g);

			o.setCenterPosition(originalLocation);
			o.setWidth(originalWidth);
			o.setHeight(originalHeight);
		}

		g.setClip(originalClip);
	}

	/**
	 * Override to change how far this viewport will be allowed to zoom. Must satisfy the invariant that closestZoom >=
	 * furthestZoom
	 *
	 * @return The furthest zoom (in pixels per game unit) which this Viewport will allow
	 */
	protected int furthestZoom()
	{
		return DEFAULT_FURTHEST_ZOOM;
	}

	/**
	 * @param gameLoc
	 * 		A game location within the Viewport
	 *
	 * @return A pixel location relating to that game location
	 */
	public Dimensional gameLocationToPixel(Dimensional gameLoc)
	{
		Vector relativeGameLoc = this.viewPosition.minus(gameLoc).smult(-1);
		Vector scaledToPixel = relativeGameLoc.smult(this.getZoom());
		return scaledToPixel.plus(this.centerPos);
	}

	/**
	 * @return The center position, in pixels, of this viewport's outer square
	 */
	public Dimensional getCenterPosition()
	{
		return this.centerPos;
	}

	/**
	 * Changes the location of this Viewport
	 *
	 * @param newPositon
	 * 		where the center of this Viewport should now be
	 */
	public void setCenterPosition(Dimensional newPositon)
	{
		this.centerPos = new Vector(newPositon);
		this.setIntervals();
	}

	/**
	 * For more on what a clip is, see:
	 *
	 * @return The clip that this viewport uses to ignore any and all outer pixels
	 *
	 * @see java.awt.Graphics
	 */
	protected Shape getClip()
	{
		int width = (int) this.size.getCoordinate(0);
		int height = (int) this.size.getCoordinate(1);

		return new Rectangle((int) (this.centerPos.getCoordinate(0) - (width / 2)), //x boundary
				(int) (this.centerPos.getCoordinate(1) - (height / 2)), //y boundary
				width, height);
	}

	/**
	 * @return The size, in pixels (as an x-y {@code Dimensional}), of this Viewport on the screen
	 */
	public Dimensional getSize()
	{
		return this.size;
	}

	/**
	 * Changes the size of this Viewport to the given size.
	 *
	 * @param size
	 * 		How big the Viewport should be, in pixels
	 */
	public void setSize(Dimensional size)
	{
		this.size = new Point(size);
		this.setIntervals();
	}

	/**
	 * @return The center position of this Viewport's view into the game world, in game units
	 */
	public Dimensional getViewPosition()
	{
		return this.viewPosition;
	}

	/**
	 * Change the game location that this Viewport is focused on
	 *
	 * @param newView
	 * 		The place that the Viewport should now focus on
	 */
	public void setViewPosition(Dimensional newView)
	{
		this.viewPosition = new Vector(newView);
		this.setIntervals();
	}

	/**
	 * The Interval over which this Viewport extends.
	 *
	 * @return An Interval (of the x axis and in game units)
	 */
	public Interval getXInterval()
	{
		return this.vX;
	}

	/**
	 * The Interval over which this Viewport extends.
	 *
	 * @return An Interval (of the y axis and in game units)
	 */
	public Interval getYInterval()
	{
		return this.vY;
	}

	/**
	 * @return The current zoom value of this Viewport, in pixels per game unit
	 */
	public int getZoom()
	{
		return (int) this.scaleAmount;
	}

	/**
	 * Sets the zoom (in pixels per game unit) of this Viewport. Allows a double because
	 *
	 * @param zoom
	 * 		The new value of this Viewport
	 */
	public void setZoom(double zoom)
	{
		this.scaleAmount = zoom;
		this.setIntervals();
	}

	/**
	 * Override to change the zooming factor, a double greater than 1 signifying how quickly this Viewport grows.
	 *
	 * @return How quickly the scale changes per zoomIn or zoomOut
	 */
	public double getZoomFactor()
	{
		return DEFAULT_SCALE_FACTOR;
	}

	/**
	 * @param pixels
	 * 		A pixel location onscreen, as a Dimensional (x,y)
	 *
	 * @return The game unit location that that pixel refers to
	 */
	public Dimensional pixelToGameLocation(Dimensional pixels)
	{
		Vector trans = this.centerPos.minus(pixels).smult(-1);
		Vector scaledToGame = trans.sdiv(this.getZoom());
		return scaledToGame.plus(this.viewPosition);
	}

	/**
	 * Resets the Intervals of this Viewport. Must be called whenever an action is taken that will alter them, or the
	 * Viewport will behave inappropriately.
	 */
	protected void setIntervals()
	{
		this.vX = Interval.about(this.viewPosition.getCoordinate(0), (this.size.getCoordinate(0) / this.getZoom()) + 2.0);
		this.vY = Interval.about(this.viewPosition.getCoordinate(1), (this.size.getCoordinate(1) / this.getZoom()) + 2.0);
	}

	/**
	 * Move the gui position using a vector
	 *
	 * @param shiftAmount
	 * 		the amount of movement for the gui position to take
	 */
	public void shiftViewPosition(Dimensional shiftAmount)
	{
		this.setViewPosition(this.viewPosition.plus(shiftAmount));
		this.setIntervals();
	}

	@Override
	public String toString()
	{
		return "Viewport{" +
				"centerPos=" + this.centerPos +
				", size=" + this.size +
				", viewPosition=" + this.viewPosition +
				", scaleAmount=" + this.scaleAmount +
				", vX=" + this.vX +
				", vY=" + this.vY +
				'}';
	}

	/**
	 * Zooms in on the viewport, using the zoom factor in getZoomFactor()
	 */
	public void zoomIn()
	{
		this.scaleAmount = Math.max(this.furthestZoom(), this.scaleAmount / this.getZoomFactor());
		this.setIntervals();
	}

	/**
	 * Zooms out of the viewport, using the zoom factor in getZoomFactor()
	 */
	public void zoomOut()
	{
		this.scaleAmount = Math.min(this.closestZoom(), this.scaleAmount * this.getZoomFactor());
		this.setIntervals();
	}
}

