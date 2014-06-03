package nschank.engn.play.io;

/**
 * Created by Nicolas Schank for package nschank.engn.play.io
 * Created on 7 Nov 2013
 * Last updated on 3 Jun 2014
 *
 * @author nschank, Brown University
 * @version 1.1
 */
public class InputFailureException extends RuntimeException
{
	/**
	 * @param s
	 */
	public InputFailureException(String s)
	{
		super(s);
	}
}
