package nschank.engn.gui.layer;

import nschank.collect.dim.Dimensional;
import nschank.engn.shape.Drawable;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicolas Schank for package nschank.engn.gui.layer
 * Created on 12 Sep 2013
 * Last updated on 24 May 2014
 *
 * A partial implementation of the Layer interface. Abstracts out the drawing of objects onscreen: all Drawable elements
 * given to the Layer by the add method are drawn in the order they were given. The coordinates, width, and height of
 * the Drawable element are used to determine how to draw it.
 *
 * @author nschank, Brown University
 * @version 1.3
 */
public abstract class AbstractLayer implements Layer
{
	private List<Drawable> elements;

	/**
	 * Creates an AbstractLayer
	 */
	protected AbstractLayer()
	{
		this.elements = new ArrayList<Drawable>();
	}

	/**
	 * Adds a visible element to be drawn as part of this layer. No elements given will automatically be ticked.
	 *
	 * @param element
	 * 		A visible piece of this Layer
	 */
	public final void add(final Drawable element)
	{
		this.elements.add(element);
	}

	/**
	 * Tells each added Drawable to draw itself in turn. The later the Drawable on the list, the further up the layers
	 * it is drawn.
	 *
	 * @param g
	 * 		A Graphics object to draw onto
	 */
	@Override
	public final void draw(final Graphics2D g)
	{
		for(Drawable o : this.elements)
			o.draw(g);
	}

	/**
	 * Removes an element already given to the AbstractLayer. Returns whether the element was in the list already.
	 *
	 * @param element
	 * 		A Drawable element to take out of this Layer
	 *
	 * @return Whether that element was in the list of drawn elements.
	 */
	public final boolean remove(final Drawable element)
	{
		return this.elements.remove(element);
	}

	/**
	 * Called every time that the Screen changes size. This includes the first time the Screen appears, so no initialization
	 * by size is really necessary.
	 *
	 * Does nothing in the AbstractLayer class.
	 *
	 * @param size
	 * 		The new size of the entire window, in pixels (x,y)
	 */
	@Override
	public void resize(final Dimensional size)
	{
		//Does nothing
	}

	/**
	 * @return a string representation of this Layer
	 */
	@Override
	public String toString()
	{
		return "AbstractLayer{" +
				"elements=" + this.elements +
				'}';
	}
}
