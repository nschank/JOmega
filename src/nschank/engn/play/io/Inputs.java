package nschank.engn.play.io;

import com.google.common.base.Function;
import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
import nschank.engn.play.Entity;
import nschank.engn.play.io.eval.Evaluator;
import nschank.engn.play.phys.PhysicsEntity;
import nschank.engn.play.univ.Universe;
import nschank.util.NMaps;

import java.util.Collection;
import java.util.Map;


/**
 * Created by Nicolas Schank for package nschank.engn.play.io
 * Created on 4 Nov 2013
 * Last updated on 2 Jun 2014
 *
 * A Utility class containing implementations of many default {@code Input}s required by {@code Entity} and
 * {@code PhysicsEntity}. Also a few basic utility methods.
 *
 * @author nschank, Brown University
 * @version 2.6
 */
public final class Inputs
{
	private Inputs()
	{

	}

	/**
	 * An implementation of the !addToBoundary {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !addToBoundary {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !addToBoundary
	 */
	public static Input addToBoundary(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				double group = ((Double) args.get("group").eval(args, entity));
				Collection<Double> n = (Collection<Double>) entity.getProperty(":boundaryGroups");
				n.add(group);
				entity.putProperty(":boundaryGroups", n);
			}
		};
	}

	/**
	 * An implementation of the !addToCollisionGroup {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !addToCollisionGroup {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !addToCollisionGroup
	 */
	public static Input addToCollisionGroup(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				double group = ((Double) args.get("group").eval(args, entity));
				Collection<Double> n = (Collection<Double>) entity.getProperty(":collisionGroups");
				n.add(group);
				entity.putProperty(":collisionGroups", n);
			}
		};
	}

	/**
	 * An implementation of the !addToForceGroup {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !addToForceGroup {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !addToForceGroup
	 */
	public static Input addToForceGroup(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				double group = ((Double) args.get("group").eval(args, entity));
				Collection<Double> n = (Collection<Double>) entity.getProperty(":forceGroups");
				n.add(group);
				entity.putProperty(":forceGroups", n);
			}
		};
	}

	/**
	 * An implementation of the !addToRayGroup {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !addToRayGroup {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !addToRayGroup
	 */
	public static Input addToRayGroup(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				double group = ((Double) args.get("group").eval(args, entity));
				Collection<Double> n = (Collection<Double>) entity.getProperty(":rayGroups");
				n.add(group);
				entity.putProperty(":rayGroups", n);
			}
		};
	}

	/**
	 * Creates an {@code Input} which performs all of the given {@code Input}s in the given order
	 *
	 * @param runnable
	 * 		Any number of {@code Input}s
	 *
	 * @return An {@code Input} which will run all of the given {@code Input}s in order
	 */
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
	 * An implementation of the !doApplyForce {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !doApplyForce {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !doApplyForce
	 */
	public static Input doApplyForce(final PhysicsEntity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				Dimensional position = entity.getCenterPosition();
				if(args.containsKey("position")) position = (Dimensional) args.get("position").eval(args, entity);

				entity.applyForceAt(new Vector((Dimensional) args.get("force").eval(args, entity)), position);
			}
		};
	}

	/**
	 * An implementation of the !doApplyImpulse {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !doApplyImpulse {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !doApplyImpulse
	 */
	public static Input doApplyImpulse(final PhysicsEntity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				Dimensional position = entity.getCenterPosition();
				if(args.containsKey("position")) position = (Dimensional) args.get("position").eval(args, entity);

				entity.applyForceAt(new Vector((Dimensional) args.get("impulse").eval(args, entity)), position);
			}
		};
	}

	/**
	 * An implementation of the !doPhysicalRemove {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !doPhysicalRemove {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !doPhysicalRemove
	 */
	public static Input doPhysicalRemove(final PhysicsEntity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				Universe myUniverse = (Universe) entity.getProperty("!universe");
				myUniverse.removePhysicsEntity(entity);
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
	 * An implementation of the !doRotate {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !doRotate {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !doRotate
	 */
	public static Input doRotate(final PhysicsEntity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				double theta = (Double) args.get("theta").eval(args, entity);
				entity.rotate(theta);
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

	/**
	 * @return An {@code Input} which does nothing.
	 */
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

	/**
	 * An implementation of the !removeFromBoundary {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !removeFromBoundary {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !removeFromBoundary
	 */
	public static Input removeFromBoundary(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				double group = ((Double) args.get("group").eval(args, entity));
				Collection<Double> n = (Collection<Double>) entity.getProperty(":boundaryGroups");
				n.remove(group);
				entity.putProperty(":boundaryGroups", n);
			}
		};
	}

	/**
	 * An implementation of the !removeFromCollisionGroup {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !removeFromCollisionGroup {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !removeFromCollisionGroup
	 */
	public static Input removeFromCollisionGroup(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				double group = ((Double) args.get("group").eval(args, entity));
				Collection<Double> n = (Collection<Double>) entity.getProperty(":collisionGroups");
				n.remove(group);
				entity.putProperty(":collisionGroups", n);
			}
		};
	}

	/**
	 * An implementation of the !removeFromForceGroup {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !removeFromForceGroup {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !removeFromForceGroup
	 */
	public static Input removeFromForceGroup(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				double group = ((Double) args.get("group").eval(args, entity));
				Collection<Double> n = (Collection<Double>) entity.getProperty(":forceGroups");
				n.remove(group);
				entity.putProperty(":forceGroups", n);
			}
		};
	}

	/**
	 * An implementation of the !removeFromRayGroup {@code Input}, as required by {@code PhysicsEntity}.
	 *
	 * @param entity
	 * 		The {@code Entity} whose !removeFromRayGroup {@code Input} is being made
	 *
	 * @return The {@code Input} to register under !removeFromRayGroup
	 */
	public static Input removeFromRayGroup(final Entity entity)
	{
		return new Input()
		{
			@Override
			public void run(Map<String, Evaluator> args)
			{
				double group = ((Double) args.get("group").eval(args, entity));
				Collection<Double> n = (Collection<Double>) entity.getProperty(":rayGroups");
				n.remove(group);
				entity.putProperty(":rayGroups", n);
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
}
