package nschank.engn.gui.layer;

import nschank.collect.dim.Dimensional;


/**
 * Created by Nicolas Schank for package nschank.engn.gui.layer
 * Created on 3 Dec 2013
 * Last modified 20 May 2014
 *
 * A Layer which does nothing except draw added shapes in their original positions. Resizing and ticking both have no
 * effect.
 *
 * @author nschank, Brown University
 * @version 1.0
 */
public class DefaultLayer extends AbstractLayer
{
	@Override
	public void onTick(long nanosSinceLastTick)
	{

	}

	@Override
	public void resize(Dimensional newSize)
	{

	}
}
