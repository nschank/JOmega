package nschank.collect.dim;

import nschank.util.DerivativeList;

import java.util.Collections;
import java.util.LinkedList;


/**
 * Created by Nicolas Schank for package nschank.engn.play.phys
 * Created on 30 Sep 2013
 * Moved to package nschank.collect.dim on 2 Jun 2014
 * Last updated on 2 Jun 2014
 *
 * @author nschank, Brown University
 * @version 3.1
 */
public class VectorDimensionalList implements DerivativeList<Vector>
{
	private LinkedList<Vector> dimensionals;

	public VectorDimensionalList(Vector... vectors)
	{
		this.dimensionals = new LinkedList<Vector>();
		Collections.addAll(this.dimensionals, vectors);
	}

	@Override
	public Vector getDerivative(int deriv)
	{
		if(deriv < 0) throw new IllegalArgumentException(
				"Despite the existence of absement and so on, VectorDimensionalList does not support them.");
		else if(deriv < this.dimensionals.size()) return this.dimensionals.get(deriv);
		else return Vector.ZERO_2D;
	}

	@Override
	public void setDerivative(int deriv, Vector newDeriv)
	{
		if(deriv < 0) throw new IllegalArgumentException(
				"Despite the existence of absement and so on, VectorDimensionalList does not support them.");
		else if(deriv < this.dimensionals.size()) this.dimensionals.set(deriv, newDeriv);
		else
		{
			while(deriv > this.dimensionals.size()) this.dimensionals.addLast(Vector.ZERO_2D);
			this.dimensionals.addLast(newDeriv);
		}
	}


	@Override
	public void step(long nanosecondStepAmount)
	{
		double seconds = (double) nanosecondStepAmount / 1_000_000_000d;
		for(int i = this.dimensionals.size() - 1; i >= 0; i--)
			this.dimensionals.set(i, this.dimensionals.get(i).plus(this.getDerivative(i + 1).smult(seconds)));
	}

	@Override
	public String toString()
	{
		return "VectorDimensionalList{" +
				"dimensionals=" + this.dimensionals +
				'}';
	}
}
