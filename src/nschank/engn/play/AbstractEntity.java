package nschank.engn.play;

import nschank.collect.tuple.Pair;
import nschank.engn.play.io.*;
import nschank.engn.play.io.eval.Constant;
import nschank.engn.play.io.eval.Evaluator;
import nschank.engn.play.univ.Universe;
import nschank.engn.shape.AbstractDrawable;
import nschank.util.NMaps;

import java.awt.Graphics2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Nicolas Schank for package nschank.engn.play.io
 * Created on 7 Oct 2013
 * Last updated on 29 May 2014
 *
 * @author nschank, Brown University
 * @version 5.1
 */
public abstract class AbstractEntity extends AbstractDrawable implements Entity
{
	protected final Map<String, Input> inputs;
	protected final Map<String, Output> outputs;
	protected final Map<String, Object> properties;

	/**
	 * Creates an {@code Entity} that exists within the given {@code Universe}
	 *
	 * @param owner
	 * 		The {@code Universe} where this Entity exists
	 */
	protected AbstractEntity(Universe owner)
	{
		super();
		this.properties = new HashMap<>();
		this.outputs = new HashMap<>();
		this.inputs = new HashMap<>();

		//Default inputs, as per Entity
		this.inputs.put("!setProperty", Inputs.setProperty(this));
		this.inputs.put("!removeProperty", Inputs.removeProperty(this));
		this.inputs.put("!doRemove", Inputs.doRemove(this));
		this.inputs.put("!runOutput", Inputs.runOutput(this));
		this.inputs.put("!errorCheckPrint", Inputs.errorCheckPrint(this));

		//Default properties, as per Entity
		this.properties.put("!self", this);
		this.properties.put("!universe", owner);
	}

	/**
	 * Creates an {@code Entity} which exists in the given {@code Universe} and with the given {@code properties}
	 *
	 * @param owner
	 * 		The {@code Universe} where this Entity exists
	 * @param properties
	 * 		Any properties this {@code Entity} should have
	 */
	protected AbstractEntity(Universe owner, Map<String, Object> properties)
	{
		this(owner);
		this.properties.remove("!self");
		this.properties.remove("!universe");
		for(String t : properties.keySet())
			this.properties.put(t, properties.get(t));
	}

	/**
	 * Causes an input to be performed, if it has been registered with the object and the "enabled" argument is not false.
	 * Otherwise, does nothing.
	 *
	 * @param inputType
	 * 		The name. Often starts with "do" and, if it does, an output will be fired.
	 * @param arguments
	 * 		A mapping of arguments from argument name to that value
	 */
	@Override
	public void doInput(String inputType, Map<String, Evaluator> arguments)
	{
		if(arguments.containsKey("enabled") && !(Boolean) arguments.get("enabled").eval(arguments, this)) return;

		if(this.inputs.containsKey(inputType))
		{
			try
			{
				this.inputs.get(inputType).run(arguments);
			} catch(Throwable t)
			{
				throw new InputFailureException("The input " + inputType + " failed to run correctly. " + t);
			}
		}
		if("do".equals(inputType.substring(0, 2)))
		{
			this.runOutput("on" + inputType.substring(2), arguments);
		}
	}

	/**
	 * Calls the !onDraw Output, with no arguments. Any overriding methods should call {@code super.draw(g)}, or
	 * (barring that) call the onDraw Output itself.
	 *
	 * @param g
	 * 		The {@code Graphics2D} object on which this {@code Entity} should be drawn
	 */
	@Override
	public void draw(Graphics2D g)
	{
		this.runOutput("!onDraw", new HashMap<String, Evaluator>());
	}

	/**
	 * @return All properties of this Entity, in a mapping of name to value
	 */
	@Override
	public Map<String, Object> getProperties()
	{
		return Collections.unmodifiableMap(this.properties);
	}

	/**
	 * @param ofName
	 * 		The name of a property
	 *
	 * @return The value of that property
	 */
	@Override
	public Object getProperty(String ofName)
	{
		return this.properties.get(ofName);
	}

	/**
	 * @param ofName
	 * 		The name of a possible Input
	 *
	 * @return Whether that {@code Input} is registered on this Entity
	 */
	@Override
	public boolean hasInput(String ofName)
	{
		return this.inputs.containsKey(ofName);
	}

	/**
	 * @param ofName
	 * 		The name of a possible property
	 *
	 * @return Whether that property has been assigned a value
	 */
	@Override
	public boolean hasProperty(String ofName)
	{
		return this.properties.containsKey(ofName);
	}

	/**
	 * Calls the !onTick Output, with the single argument "nanosSinceLastTick"
	 *
	 * @param nanosSinceLastTick
	 * 		Billionths of a second since the last onTick was called
	 */
	@Override
	public void onTick(long nanosSinceLastTick)
	{
		this.runOutput("!onTick", NMaps.of(
				Pair.tuple("nanosSinceLastTick", (Evaluator) new Constant(Float.valueOf(nanosSinceLastTick)))));
		//todo Evaluators.constant()
	}

	/**
	 * Registers an input with this {@code Entity}. If that {@code Input} is internal (starts with an exclamation mark),
	 * does nothing. The empty string is also an invalid name.
	 *
	 * @param ofName
	 * 		The name of an {@code Input}
	 * @param reaction
	 * 		A reaction which should occur, when {@code doInput} is called with name {@code ofName}
	 */
	@Override
	public void putInput(String ofName, Input reaction)
	{
		if(ofName.isEmpty() || ofName.charAt(0) == '!') return;
		this.inputs.put(ofName, reaction);
	}

	/**
	 * Puts all properties in the given map into this {@code Entity}'s property list. Ignores any property names starting
	 * with an exclamation mark, or the empty string.
	 *
	 * @param properties
	 * 		A mapping from property names to property values
	 */
	@Override
	public void putProperties(Map<String, Object> properties)
	{
		for(String input : properties.keySet())
			this.putProperty(input, properties.get(input));
	}

	/**
	 * Puts a single property into this {@code Entity}'s property list.
	 *
	 * @param ofName
	 * 		The name of a property, which cannot start with an exclamation mark, and cannot be the empty string
	 * @param ofValue
	 * 		The value this property should take
	 */
	@Override
	public void putProperty(String ofName, Object ofValue)
	{
		if(ofName.isEmpty() || ofName.charAt(0) == '!') return;
		this.properties.put(ofName, ofValue);
	}

	/**
	 * Removes a single property from this {@code Entity}'s property list.
	 *
	 * @param ofName
	 * 		The name of a property, which cannot start with an exclamation mark, and cannot be the empty string
	 */
	@Override
	public void removeProperty(String ofName)
	{
		if(ofName.isEmpty() || ofName.charAt(0) == '!') return;
		this.properties.remove(ofName);
	}

	/**
	 * Connects the given {@code Output} by name to the Connection given
	 *
	 * @param output
	 * 		The name of an {@code Output}
	 * @param conn
	 * 		A {@code Connection} to attach to the given output
	 */
	@Override
	public void connect(final String output, final Connection conn)
	{
		if(this.outputs.containsKey(output))
		{
			Output anOutput = new Output();
			anOutput.connect(conn);
			this.outputs.put(output, anOutput);
		} else this.outputs.get(output).connect(conn);
	}

	public Connection connect(final String output, final Entity anEntity, final String input)
	{
		Connection c = new Connection(anEntity, input);
		this.connect(output, c);
		return c;
	}

	/**
	 * Runs an {@code Output}, given by the name {@code ofName} and given arguments with which to run it.
	 *
	 * @param ofName
	 * 		The name of an Output
	 * @param args
	 * 		The arguments of this {@code Output} when run
	 */
	@Override
	public void runOutput(final String ofName, final Map<String, Evaluator> args)
	{
		if(this.outputs.containsKey(ofName)) this.outputs.get(ofName).run(args);
	}
}
