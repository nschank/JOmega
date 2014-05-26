package nschank.engn.shape;

import cs195n.Vec2f;
import nschank.util.Interval;

import java.awt.Color;
import java.awt.Graphics2D;


/**
 * An object that can be drawn onto a Graphics2D object. The more general interface that OmegaShapes follow.
 *
 * @author Nicolas Schank
 * @version 2013 09 15
 * @since 2013 09 15 2:40 PM
 */
public interface Drawable
{
	public static final Drawable NOTHING = NoShape.NO_SHAPE;

	public void draw(Graphics2D g);
	public Vec2f getCenterPosition();
	public void setCenterPosition(Vec2f centerPosition);
	public float getHeight();
	public void setHeight(float h);
	public float getWidth();
	public void setWidth(float w);
	public Color getColor();
	public void setColor(Color c);

	public Interval xInterval();
	public Interval yInterval();
}
