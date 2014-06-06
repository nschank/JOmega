package nschank.engn.play.phys;

/**
 * Created by Nicolas Schank for package nschank.engn.play.phys
 * Created on 30 May 2014
 * Last updated on 6 Jun 2014
 *
 * The way in which a collision should affect a {@code PhysicsEntity}. The same type of reaction should occur on both
 * sides of a collision; in other words.
 *
 * @author nschank, Brown University
 * @version 1.2
 */
public enum ReactionType
{
	/**
	 * Collisions do not result in a momentum change of any kind, and the collision does not result in any friction.
	 * Collisions only result in objects no longer overlapping.
	 */
	OVERLAP_ONLY,
	/**
	 * Collisions result in the objects no longer overlapping, and momentum change is applied in a natural way.
	 */
	IMPULSE_ONLY,
	/**
	 * Collisions result in the objects no longer overlapping, and friction reacts, but impulse does not. Not a very
	 * useful reaction type.
	 */
	FRICTION_ONLY,
	/**
	 * As close to realistic reactions as possible. Overlaps, impulses, and friction are all applied.
	 */
	FRICTION_AND_IMPULSE
}
