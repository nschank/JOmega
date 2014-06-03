package nschank.engn.play.io;

import nschank.engn.play.io.eval.Evaluator;

import java.util.Map;


/**
 * Created for package nschank.engn.play.io
 * Created on 27 Oct 2013
 * Last updated on 3 Jun 2014
 *
 * @author nschank, Brown University
 * @version 1.4
 */
public interface Input
{
	/**
	 * @param args
	 *
	 * @throws InputFailureException
	 */
	public void run(Map<String, Evaluator> args) throws InputFailureException;
}
