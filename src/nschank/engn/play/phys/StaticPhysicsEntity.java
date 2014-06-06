package nschank.engn.play.phys;

import nschank.collect.dim.Dimensional;
import nschank.collect.dim.Vector;
import nschank.collect.tuple.Pair;
import nschank.engn.play.io.eval.Constant;
import nschank.engn.play.io.eval.Evaluator;
import nschank.engn.play.univ.Universe;
import nschank.util.NMaps;

import java.util.Map;


/**
 * Created by Nicolas Schank for package nschank.engn.play.phys
 * Created on 14 Oct 2013
 * Last updated on 6 Jun 2014
 *
 * @author nschank, Brown University
 * @version 3.2
 */
public class StaticPhysicsEntity extends AbstractPhysicsEntity
{
	/**
	 * Creates a PhysicsEntity whose boundaries are assigned by the given Collidable, and
	 * which draws the same Collidable as itself.
	 *
	 * @param properties
	 * 		A descrpition of the shape and visual aspect of this PhysicsEntity
	 */
	public StaticPhysicsEntity(Universe universe, Map<String, Object> properties)
	{
		super(universe, NMaps.extend(properties, Pair.tuple("mass", 10000000.0)));
	}

	@Override
	public void react(PhysCollision collision, ReactionType reactionType)
	{
		boolean impulses = reactionType == ReactionType.FRICTION_AND_IMPULSE
				|| reactionType == ReactionType.IMPULSE_ONLY;
		boolean friction = reactionType == ReactionType.FRICTION_AND_IMPULSE
				|| reactionType == ReactionType.FRICTION_ONLY;

		this.doInput("doCollide", NMaps.of(Pair.tuple("!collision", (Evaluator) new Constant(collision)),
				Pair.tuple("!impulses", new Constant(impulses)), Pair.tuple("!dofriction", new Constant(friction)),
				Pair.tuple("!mtv", new Constant(collision.getMTV())),
				Pair.tuple("!impulse", new Constant(collision.getImpulse())),
				Pair.tuple("!friction", new Constant(collision.getSlidingFrictionalImpulse())),
				Pair.tuple("!collisionPoint", new Constant(collision.getCollisionPoint())),
				Pair.tuple("!collidingWith", new Constant(collision.getOther().getProperty("name").toString()))));
		if(collision.getMTV().mag2() == 0) return;
		collision.getOther().applyLocationChange(collision.getMTV().smult(-1));
	}

	@Override
	public void applyLocationChange(Vector change)
	{

	}

	@Override
	public void applyForceAt(Vector force, Dimensional position)
	{

	}

	@Override
	public void applyImpulseAt(Vector impulse, Dimensional position)
	{

	}

	@Override
	public Object getProperty(String ofName)
	{
		if("!static".equals(ofName)) return Boolean.TRUE;
		else return super.getProperty(ofName);
	}
}
