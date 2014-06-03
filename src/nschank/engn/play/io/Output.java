package nschank.engn.play.io;

import nschank.engn.play.io.eval.Evaluator;
import nschank.util.NMaps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Nicolas Schank for package nschank.engn.play.io
 * Created on 27 Oct 2013
 * Last updated on 3 Jun 2014
 *
 * @author nschank, Brown University
 * @version 2.4
 */
public final class Output
{
	private final List<Connection> connections;
	private final Map<String, Evaluator> args;

	/**
	 *
	 */
	public Output()
	{
		this.connections = new ArrayList<>();
		this.args = new HashMap<>();
	}

	/**
	 * @param I
	 * @param O
	 */
	public void putArgument(String I, Evaluator O)
	{
		this.args.put(I, O);
	}

	/**
	 * @param r
	 */
	public void connect(Connection r)
	{
		this.connections.add(r);
	}

	/**
	 *
	 */
	public void run()
	{
		try
		{
			for(Connection r : this.connections)
				r.run(this.args);
		} catch(Throwable t)
		{
			throw new OutputFailureException("An output failed to run correctly. " + t);
		}
	}

	/**
	 * @param additionalArgs
	 */
	public void run(Map<String, Evaluator> additionalArgs)
	{
		try
		{
			for(Connection r : this.connections)
				r.run(NMaps.union(this.args, additionalArgs));
		} catch(Throwable t)
		{
			throw new OutputFailureException("An output failed to run correctly. " + t);
		}
	}
}
