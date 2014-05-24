package nschank.engn.gui.layer;

import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
import nschank.collect.tuple.Quintuple;
import nschank.engn.shape.Drawable;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Created by Nicolas Schank for package nschank.engn.gui.layer.
 * Created on 29 Sep 2013
 * Last updated on 24 May 2014
 *
 * An extension of an AbstractLayer which uses the drawing implementation of the AbstractLayer, but extends it to make
 * resizing automatic. Drawables can be added normally using add(Drawable), under which resizing will not occur except
 * manually; but in a RelativeLayer, there is also an add(Drawable, double, double, double, double) which allows a
 * Drawable to be matched to a relative width, height, and x and y positions (all between 0 and 1) based on the current
 * size of the Screen.
 *
 * @author Nicolas Schank
 * @version 2013 09 29
 * @since 2013 09 29 7:25 PM
 */
public class RelativeLayer extends AbstractLayer
{
	private Collection<Quintuple<Drawable, Double, Double, Double, Double>> shapes = new ArrayList<>();


	/**
	 * Adds a Drawable, along with the relative (0 to 1) width, height, x, and y positions that the Drawable should stay
	 * compared to the width and height of the Screen.
	 *
	 * @param index
	 * 		A Drawable to add onto the Layer
	 * @param width
	 * 		A double greater than 0 that is the width of this Drawable in comparison to the Screen's width.
	 * @param height
	 * 		A double greater than 0 that is the  height of this Drawable in comparison to the Screen's height.
	 * @param xpos
	 * 		A double that is the x-position of the center of this Drawable in comparison to the Screen's width, where
	 * 		0 is the far left side and 1 is the far right side.
	 * @param ypos
	 * 		A double that is the y-position of the center of this Drawable in comparison to the Screen's height, where
	 * 		0 is the bottom side and 1 is the top side.
	 */
	public final void add(Drawable index, double width, double height, double xpos, double ypos)
	{
		assert width > 0;
		assert height > 0;

		super.add(index);
		this.shapes.add(Quintuple.tuple(index, width, height, xpos, ypos));
	}

	/**
	 * Currently does nothing.
	 *
	 * @param nanosSinceLastTick
	 * 		The number of billionths of seconds since this method was last called.
	 */
	@Override
	public void onTick(long nanosSinceLastTick)
	{

	}

	/**
	 * Updates all elements given by the relative add method to have the relative sizes provided. If this method is
	 * overridden, it should call super.resize(newSize).
	 *
	 * @param newSize
	 * 		The new size of the Screen
	 */
	@Override
	public void resize(Dimensional newSize)
	{
		for(Quintuple<Drawable, Double, Double, Double, Double> s : this.shapes)
		{
			s.getA().setWidth(s.getB() * newSize.getCoordinate(0));
			s.getA().setHeight(s.getC() * newSize.getCoordinate(1));
			double x = s.getD() * newSize.getCoordinate(0);
			double y = s.getE() * newSize.getCoordinate(1);
			s.getA().setCenterPosition(new Vector(x, y));
		}
	}
}
