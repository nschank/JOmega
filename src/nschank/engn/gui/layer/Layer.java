package nschank.engn.gui.layer;

import nschank.collect.dim.Dimensional;

import java.awt.Graphics2D;


/**
 * Created by Nicolas Schank for package nschank.engn.gui
 * Created on 09 May 2014
 * Last updated on 09 May 2014
 *
 * A Layer which is able to draw itself
 *
 * @author nschank, Brown University
 * @version 1.1
 */
public interface Layer
{
	public void onDraw(Graphics2D g);
	public void onTick(long nanosSinceLatTick);
	public void resize(Dimensional size);
}
