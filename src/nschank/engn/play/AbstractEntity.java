package nschank.engn.play;

import com.google.common.base.Function;
import nschank.collect.tuple.Pair;
import nschank.engn.play.io.Input;
import nschank.engn.play.io.InputFailureException;
import nschank.engn.play.io.Output;
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
 * @version 4.8
 */
public abstract class AbstractEntity extends AbstractDrawable implements Entity
{
	private final Map<String, Input> inputs;
	private final Map<String, Output> outputs;
	private final Map<String, Object> properties;

	public AbstractEntity()
	{
		super();
		this.properties = new HashMap<>();
		this.outputs = new HashMap<>();
		this.inputs = new HashMap<>();
		this.inputs.put("setProperty", new Input()
		{
			@Override
			public void run(final Map<String, Evaluator> args)
			{
				AbstractEntity.this.putProperties(NMaps.mapOverValues(args, new Function<Evaluator, Object>()
				{
					@Override
					public Object apply(Evaluator evaluator)
					{
						return evaluator.eval(args, AbstractEntity.this);
					}
				}));
			}
		});
		this.inputs.put("removeProperty", new Input()
		{
			@Override
			public void run(final Map<String, Evaluator> args)
			{
				String ofName = args.get("property").eval(args, AbstractEntity.this).toString();
				AbstractEntity.this.removeProperty(ofName);
			}
		});
		this.inputs.put("remove", new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				if(AbstractEntity.this.hasProperty("universe"))
					((Universe) AbstractEntity.this.getProperty("universe")).removeEntity(AbstractEntity.this);
				AbstractEntity.this.getOutput("onRemove").run();
			}
		});

		this.inputs.put("runOutput", new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				String target = args.get("target").eval(args, AbstractEntity.this).toString();
				args.remove("target");
				AbstractEntity.this.getOutput(target).run(args);
			}
		});

		this.inputs.put("errorCheckPrint", new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				for(String in : args.keySet())
					System.out.println(in + " -> " + args.get(in).eval(args, AbstractEntity.this));
			}
		});
	}

	public AbstractEntity(Map<String, Object> properties)
	{
		this();
		for(String t : properties.keySet())
			this.properties.put(t, properties.get(t));
	}

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
			this.getOutput("on" + inputType.substring(2)).run(arguments);
		}
	}

	@Override
	public void draw(Graphics2D g)
	{
		this.getOutput("onDraw").run();
	}

	@Override
	public Output getOutput(String activatingEvent)
	{
		if(this.outputs.containsKey(activatingEvent)) return this.outputs.get(activatingEvent);

		Output ret = new Output();
		ret.putArgument("!caller", new Constant(this));
		this.outputs.put(activatingEvent, ret);
		return ret;
	}

	@Override
	public Map<String, Object> getProperties()
	{
		return Collections.unmodifiableMap(this.properties);
	}

	@Override
	public Object getProperty(String ofName)
	{
		return this.properties.get(ofName);
	}

	@Override
	public boolean hasInput(String ofName)
	{
		return this.inputs.containsKey(ofName);
	}

	@Override
	public boolean hasProperty(String ofName)
	{
		return this.properties.containsKey(ofName);
	}

	@Override
	public void onTick(long nanosSinceLastTick)
	{
		this.getOutput("onTick").run(NMaps
				.of(Pair.tuple("nanosSinceLastTick", (Evaluator) new Constant(Float.valueOf(nanosSinceLastTick)))));
	}

	@Override
	public void putInput(String ofName, Input reaction)
	{
		this.inputs.put(ofName, reaction);
	}

	@Override
	public void putProperties(Map<String, Object> properties)
	{
		for(String input : properties.keySet())
			this.putProperty(input, properties.get(input));
	}

	@Override
	public void putProperty(String ofName, Object ofValue)
	{
		this.properties.put(ofName, ofValue);
	}

	@Override
	public void removeProperty(String ofName)
	{
		this.properties.remove(ofName);
	}
}
