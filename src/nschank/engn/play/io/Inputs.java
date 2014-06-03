package nschank.engn.play.io;

import com.google.common.base.Function;
import nschank.engn.play.Entity;
import nschank.engn.play.io.eval.Evaluator;
import nschank.engn.play.univ.Universe;
import nschank.util.NMaps;

import java.util.Map;


/**
 * Created by Nicolas Schank for package nschank.engn.play.io
 * Created on 4 Nov 2013
 * Last updated on 30 May 2014
 *
 * @author nschank, Brown University
 * @version 2.5
 */
public final class Inputs
{
	public static Input nothing()
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{

			}
		};
	}

	public static Input doAll(final Input... runnable)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				for(Input i : runnable)
					i.run(args);
			}
		};
	}

	/**
	 * A basic implementation of the setProperty {@code Input} as defined in the {@code Entity} interface.
	 *
	 * @param entity
	 * 		The {@code Entity} whose setProperty {@code Input} is being made
	 *
	 * @return The {@code Input} that has been created
	 */
	public static Input setProperty(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(final Map<String, Evaluator> args)
			{
				entity.putProperties(NMaps.mapOverValues(args, new Function<Evaluator, Object>()
				{
					@Override
					public Object apply(Evaluator evaluator)
					{
						return evaluator.eval(args, entity);
					}
				}));
			}
		};
	}

	/**
	 * A basic implementation of the removeProperty {@code Input} as defined in the {@code Entity} interface.
	 *
	 * @param entity
	 * 		The {@code Entity} whose removeProperty {@code Input} is being made
	 *
	 * @return The {@code Input} that has been created
	 */
	public static Input removeProperty(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(final Map<String, Evaluator> args)
			{
				String ofName = args.get("property").eval(args, entity).toString();
				entity.removeProperty(ofName);
			}
		};
	}

	/**
	 * A basic implementation of the doRemove {@code Input} as defined in the {@code Entity} interface.
	 *
	 * @param entity
	 * 		The {@code Entity} whose doRemove {@code Input} is being made
	 *
	 * @return The {@code Input} that has been created
	 */
	public static Input doRemove(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				((Universe) entity.getProperty("!universe")).removeEntity(entity);
			}
		};
	}

	/**
	 * A basic implementation of the runOutput {@code Input} as defined in the {@code Entity} interface.
	 *
	 * @param entity
	 * 		The {@code Entity} whose runOutput {@code Input} is being made
	 *
	 * @return The {@code Input} that has been created
	 */
	public static Input runOutput(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				String target = args.get("target").eval(args, entity).toString();
				args.remove("target");
				entity.runOutput(target, args);
			}
		};
	}

	/**
	 * A basic implementation of the errorCheckPrint {@code Input} as defined in the {@code Entity} interface.
	 *
	 * @param entity
	 * 		The {@code Entity} whose errorCheckPrint {@code Input} is being made
	 *
	 * @return The {@code Input} that has been created
	 */
	public static Input errorCheckPrint(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				for(String in : args.keySet())
					System.out.println(in + " -> " + args.get(in).eval(args, entity));
			}
		};
	}

	private Inputs()
	{

	}
}
