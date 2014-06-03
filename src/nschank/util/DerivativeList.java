package nschank.util;

/**
 * Created by Nicolas Schank for package nschank.util
 * Created on 02 Jun 2014
 * Last updated on 02 Jun 2014
 *
 * A list of derivatives, from zero up to the maximum derivative that is not zero. On each {@code step}, the list should
 * update in Eulerian symplectic order (from the highest to the lowest derivative).
 *
 * @author nschank, Brown University
 * @version 1.1
 */
public interface DerivativeList<T>
{
	/**
	 * @param deriv
	 * 		The derivative to find within this list; a nonnegative integer
	 *
	 * @return The {@code deriv}th derivative of the list; always 0 if above the maximum derivative
	 */
	T getDerivative(int deriv);
	/**
	 * @param deriv
	 * 		The derivative to set within this list; a nonnegative integer
	 * @param set
	 * 		The value to set to that derivative
	 */
	void setDerivative(int deriv, T set);
	/**
	 * Update all derivatives in Eulerian symplectic order
	 *
	 * @param nanosecondsSinceLastStep
	 * 		The number of billionths of seconds since the previous step
	 */
	void step(long nanosecondsSinceLastStep);
}
