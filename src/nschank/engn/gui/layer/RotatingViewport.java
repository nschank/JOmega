package nschank.engn.gui.layer;

import nschank.collect.dim.Dimensional;
import nschank.engn.shape.Drawable;
import nschank.util.Interval;

import java.awt.Graphics2D;
import java.awt.Shape;


/**
 * Created by Nicolas Schank for package nschank.engn.gui.layer
 * Created in November 2013
 * Last modified 25 May 2014
 *
 * A basic extension of the AbstractViewport which adds a concept of rotation. The rotation is the angle, in radians,
 * between the Screen and the in-game location system.
 *
 * @author nschank, Brown University
 * @version 2.0
 */
public abstract class RotatingViewport extends AbstractViewport
{
	private double rotation = 0;

	/**
	 * Creates a RotatingViewport at a particular position
	 *
	 * @param centerPos
	 * 		Where the Viewport should be, in pixels (centerpoint)
	 * @param size
	 * 		How big the Viewport should be, in pixels
	 * @param viewPosition
	 * 		What the Viewport should be looking at, in game location units
	 * @param scale
	 * 		How big a single game location unit should be, in pixels
	 * @param theta
	 * 		The angle between a line in the Screen and a line in the in-game location system.
	 */
	protected RotatingViewport(Dimensional centerPos, Dimensional size, Dimensional viewPosition, double scale, double theta)
	{
		super(centerPos, size, viewPosition, scale);
		this.rotation = theta;
	}

	/**
	 * Draws all objects within the RotatingViewport given by iterator().
	 *
	 * @param g
	 * 		A Graphics object used to draw
	 */
	@Override
	public void draw(Graphics2D g)
	{
		Shape clip = g.getClip();
		g.setClip(this.getClip());
		g.rotate(-this.rotation, this.getCenterPosition().getCoordinate(0), this.getCenterPosition().getCoordinate(1));
		for(Drawable o : this)
		{
			double originalWidth = o.getWidth();
			double originalHeight = o.getHeight();
			Dimensional originalLocation = o.getCenterPosition();

			o.setCenterPosition(this.gameLocationToPixel(originalLocation));
			o.setWidth(originalWidth * this.getZoom());
			o.setHeight(originalHeight * this.getZoom());

			o.draw(g);

			o.setCenterPosition(originalLocation);
			o.setWidth(originalWidth);
			o.setHeight(originalHeight);
		}
		g.rotate(this.getRotation(), this.getCenterPosition().getCoordinate(0), this.getCenterPosition().getCoordinate(1));
		g.setClip(clip);
	}

	/**
	 * @return The current angle of the in-game coordinate system, from the Screen coordinate system
	 */
	public double getRotation()
	{
		return this.rotation;
	}

	/**
	 * @param rotation
	 * 		The new angle at which to put the in-game coordinate system
	 */
	public void setRotation(double rotation)
	{
		this.rotation = rotation;
	}

	/*
	 * TODO improve the y-interval calculation
	 */
	@Override
	public Interval getXInterval()
	{
		if(Math.abs(this.getRotation()) < .002) return super.getXInterval();
		float width = super.getXInterval().width();
		float height = super.getYInterval().width();
		float diameter = Math.max(width, height);

		return super.getXInterval().stretch(diameter * 2.83);
	}

	/*
	 * TODO improve the y-interval calculation
	 */
	@Override
	public Interval getYInterval()
	{
		if(Math.abs(this.getRotation()) < .002) return super.getYInterval();
		double width = super.getXInterval().width();
		double height = super.getYInterval().width();
		double diameter = Math.max(width, height);
		return super.getYInterval().stretch(diameter * 2.83);
	}

	@Override
	public String toString()
	{
		return "RotatingViewport{" +
				"rotation=" + this.rotation +
				'}';
	}
}

