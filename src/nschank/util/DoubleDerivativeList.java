package nschank.util;


import java.util.Collections;
import java.util.LinkedList;


/**
 * Created by Nicolas Schank for package nschank.engn.play.phys
 * Created on 30 Sep 2013
 * Moved to package nschank.util on 2 Jun 2014
 * Last updated on 2 Jun 2014
 *
 * A {@code DerivativeList} using doubles.
 *
 * @author nschank, Brown University
 * @version 2.1
 */
public class DoubleDerivativeList implements DerivativeList<Double>
{
	private LinkedList<Double> doubles;

	/**
	 * Creates a derivative list of numbers.
	 *
	 * @param derivs
	 * 		A list of derivatives, as a number
	 */
	public DoubleDerivativeList(Double... derivs)
	{
		this.doubles = new LinkedList<>();
		Collections.addAll(this.doubles, derivs);
	}

	/**
	 * @param deriv
	 * 		The derivative to find within this list; a nonnegative integer
	 *
	 * @return The {@code deriv}th derivative of the list; always 0 (or equivalent) if above the maximum derivative
	 */
	@Override
	public Double getDerivative(int deriv)
	{
		if(deriv < 0) throw new IllegalArgumentException(
				"Despite the existence of absement and so on, DoubleDerivativeList does not support them.");
		else if(deriv < this.doubles.size()) return this.doubles.get(deriv);
		else return 0.0;
	}

	/**
	 * @param deriv
	 * 		The derivative to set within this list; a nonnegative integer
	 * @param newDeriv
	 * 		The value to set to that derivative
	 */
	@Override
	public void setDerivative(int deriv, Double newDeriv)
	{
		if(deriv < 0) throw new IllegalArgumentException(
				"Despite the existence of absement and so on, DoubleDerivativeList does not support them.");
		else if(deriv < this.doubles.size()) this.doubles.set(deriv, newDeriv);
		else
		{
			while(deriv > this.doubles.size()) this.doubles.addLast(0.0);
			this.doubles.addLast(newDeriv);
		}
	}

	/**
	 * Update all derivatives in Eulerian symplectic order
	 *
	 * @param nanosecondsSinceLastStep
	 * 		The number of billionths of seconds since the previous step
	 */
	@Override
	public void step(long nanosecondsSinceLastStep)
	{
		double seconds = (double) nanosecondsSinceLastStep / 1_000_000_000d;
		for(int i = this.doubles.size() - 1; i >= 0; i--)
			this.doubles.set(i, this.doubles.get(i) + (this.getDerivative(i + 1) * seconds));
	}

	@Override
	public String toString()
	{
		return doubles.toString();
	}
}
