package nschank.engn.play.io;


import nschank.engn.play.Entity;
import nschank.engn.play.io.eval.Evaluator;
import nschank.util.NMaps;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Nicolas Schank for package nschank.engn.play.io
 * Created on 27 Oct 2013
 * Last updated on 3 Jun 2014
 *
 * A {@code Connection} is a way to connect
 *
 * @author nschank, Brown University
 * @version 1.5
 */
public final class Connection
{
	private final Entity target;
	private final String action;
	private final Map<String, Evaluator> args;

	/**
	 * @param target
	 * @param action
	 */
	public Connection(Entity target, String action)
	{
		this.target = target;
		this.action = action;
		this.args = new HashMap<>();
	}

	/**
	 * @param target
	 * @param action
	 * @param args
	 */
	public Connection(Entity target, String action, Map<String, Evaluator> args)
	{
		this.target = target;
		this.action = action;
		this.args = new HashMap<>(args);
	}

	/**
	 * @param argumentName
	 * @param argumentValue
	 */
	public void addArgument(String argumentName, Evaluator argumentValue)
	{
		this.args.put(argumentName, argumentValue);
	}

	/**
	 * @param additionalArgs
	 */
	public void run(Map<String, Evaluator> additionalArgs)
	{
		this.target.doInput(this.action, NMaps.union(additionalArgs, this.args));
	}
}
