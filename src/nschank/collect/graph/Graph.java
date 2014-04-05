package nschank.collect.graph;

import java.util.Collection;


/**
 * Created by Nicolas Schank for package nschank.collect.graph
 * Created 1 Mar 2013
 * Last updated 25 Mar 2014
 *
 * A Graph is a Collection of items that can be connected to any other item in
 * the Graph.
 *
 * @param <T>
 * 		Any type
 *
 * @author Nicolas Schank
 * @version 0.5
 * @since 2013 Mar 01
 */
public interface Graph<T> extends Collection<T>
{
	/**
	 * Given two elements, this procedure must return whether or not the two are
	 * connected in the Graph. To support unidirectionality, connectedTo(a,b)
	 * does not necessarily need to be equivalent to connectedTo(b,a).
	 *
	 * @param from
	 * 		The element in the graph a connection must start from, in
	 * 		order for this procedure to return true.
	 * @param to
	 * 		The element in the graph a connection must reach, in order for
	 * 		this procedure to return true.
	 *
	 * @return Whether such a connection exists.
	 */
	public boolean connectedTo(T from, T to);

	/**
	 * @param element
	 * 		A single element in this Graph.
	 *
	 * @return all elements that can be reached from this element using a single
	 * connection.
	 */
	public Collection<T> allConnectedTo(T element);
}
