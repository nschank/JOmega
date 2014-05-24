package nschank.engn.gui.layer;

import nschank.collect.dim.Dimensional;
import nschank.engn.gui.AbstractScreen;


/**
 * Created by Nicolas Schank for package nschank.engn.gui.layer
 * Created on 21 Oct 2014
 * Last modified on 20 May 2014
 *
 * An interesting type of Layer useful in certain gaming situations. This Layer, for which a subclass must implement the
 * drawing method, will only appear for a given period of time. After that period of time, this Layer will remove itself
 * completely from its parent AbstractScreen.
 *
 * Note that the parent must be an AbstractScreen because otherwise the Layering implementation may not be followed as
 * expected, so the invariants of this Layer (namely, that it will appear for exactly the number of nanoseconds given,
 * after which no ticking or drawing will occur) cannot be ensured by the implementation itself.
 *
 * @author Nicolas Schank
 * @version 1.1
 */
public abstract class OneTimeLayer implements Layer
{
	private final AbstractScreen inside;
	private long nanosUntilRemove;

	/**
	 * Creates a Layer which will only exist for the given number of nanoseconds before being removed completely from the
	 * AbstractScreen given.
	 *
	 * @param inside
	 * 		This Layer's parent AbstractScreen
	 * @param nanosUntilRemove
	 * 		The number of billionths of seconds that this Layer can be allowed to exist
	 */
	public OneTimeLayer(final AbstractScreen inside, final long nanosUntilRemove)
	{
		super();
		this.inside = inside;
		this.nanosUntilRemove = nanosUntilRemove;
	}

	/**
	 * Counts down to this Layer's destruction
	 *
	 * @param nanosSinceLastTick
	 * 		The number of billionths of seconds that have passed
	 */
	@Override
	public final void onTick(long nanosSinceLastTick)
	{
		this.nanosUntilRemove -= nanosSinceLastTick;
		if(this.nanosUntilRemove < 0L) if(!this.inside.removeLayer(this))
			throw new RuntimeException("A OneTimeLayer could not be removed because the AbstractScreen provided was not its parent.");
	}

	/**
	 * Currently, does nothing. Override to make this Layer react to a change in size.
	 *
	 * @param newSize
	 * 		The new size of the Screen.
	 */
	@Override
	public void resize(Dimensional newSize)
	{

	}
}
