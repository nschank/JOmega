package nschank.collect.dim;

/**
 * Created by Nicolas Schank for package nschank.collect.dim Created on 25 Feb, 2014
 * <p/>
 * It seems reasonable to expect Points to be in more than a 1-dimensional space, there doesn't seem like much utility
 * and it's likely unintentional.
 *
 * @author nschank, Brown University
 * @version 1.1
 * @since 2/25/14
 */
public class OneDimensionalPointException extends Error
{
	public OneDimensionalPointException()
	{
		super("There's not much point to a Point with fewer than two dimensions (AKA a \"number\").");
	}
}
