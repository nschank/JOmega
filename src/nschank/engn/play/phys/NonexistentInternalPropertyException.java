package nschank.engn.play.phys;

/**
 * Created by Nicolas Schank for package nschank.engn.play.phys
 * Created on 3 Jun 2014
 * Last updated on 3 Jun 2014
 *
 * A {@code RuntimeException} thrown whenever a property is set or gotten, and starts with a colon (:), but does not
 * have a real internal effect.
 *
 * @author nschank, Brown University
 * @version 1.2
 */
public class NonexistentInternalPropertyException extends RuntimeException
{
	/**
	 * Creates an exception due to the given, non-internal property
	 *
	 * @param noninternalProperty
	 * 		A property which starts with a colon (:), but is not a real internal property.
	 */
	public NonexistentInternalPropertyException(String noninternalProperty)
	{
		super("The property " + noninternalProperty
				+ " is formatted as an internal property, but will have no internal effect.");
	}
}
