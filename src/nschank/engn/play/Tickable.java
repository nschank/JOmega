package nschank.engn.play;

/**
 * Created by Nicolas Schank for package nschank.engn.play
 * Created on 1 Oct 2013
 * Last updated on 29 May 2014
 *
 * One of the central interfaces of the nschank.engn.play class, representing an object which interacts with the world
 * through time.
 *
 * @author nschank, Brown University
 * @version 1.1
 */
public interface Tickable
{
	/**
	 * An action to be performed continuously. In order to allow for time-related actions to be performed correctly, the
	 * amount of time between the end of the last 'tick' and the beginning of the current 'tick' is provided.
	 *
	 * @param nanosSinceLastTick
	 * 		The number of nanoseconds between the end of the last tick and the beginning of the current tick.
	 */
	public void onTick(long nanosSinceLastTick);
}
