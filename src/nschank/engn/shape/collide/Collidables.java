package nschank.engn.shape.collide;

import com.google.common.base.Optional;
import nschank.collect.dim.Dimensional;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicolas Schank for package nschank.engn.shape.collide;
 * Created on 28 May 2014
 * Last updated on 29 May 2014
 *
 * A Utility class meant to make working with Collidables and Collisions easier.
 *
 * @author nschank, Brown University
 * @version 1.2
 */
public final class Collidables
{
	/**
	 *
	 */
	private Collidables()
	{
		//Utility class
	}

	/**
	 * Returns the inverse of this Collision, still in an Optional, if one exists. If it does not exist, returns
	 * Optional.absent().
	 *
	 * @param invert
	 * 		A Collision to invert
	 *
	 * @return An Optional of the inverse of this Collision, if one exists
	 */
	public static Optional<Collidable.Collision> inverseOf(Optional<Collidable.Collision> invert)
	{
		if(invert.isPresent()) return Optional.of(invert.get().inverse());
		return Optional.absent();
	}

	/**
	 *
	 */
	public static void main(String[] args)
	{

	}

	/**
	 * Returns all of the given points that are within the object.
	 *
	 * @param object
	 * 		The Collidable which may have some of these points
	 * @param dims
	 * 		A collection of points
	 *
	 * @return All points {@code dims} that are within the {@code object}
	 */
	public static List<Dimensional> contained(final Collidable object, final Iterable<Dimensional> dims)
	{
		List<Dimensional> allContained = new ArrayList<>();
		for(Dimensional dim : dims)
			if(object.contains(dim)) allContained.add(dim);
		return allContained;
	}
}
