package nschank.engn.play.io;

/**
 * Created by Nicolas Schank for package nschank.engn.play.io
 * Created on 7 Nov 2013
 * Last updated on 3 Jun 2014
 *
 * @author nschank, Brown University
 * @version 1.2
 */
public class OutputFailureException extends RuntimeException
{
	/**
	 * @param s
	 */
	public OutputFailureException(String s)
	{
		super("An Output failed to run correctly.\n" + s);
	}
}
