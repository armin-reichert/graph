package de.amr.easy.graph.pathfinder.impl;

import java.util.PriorityQueue;
import java.util.function.Function;

import de.amr.easy.graph.core.api.Graph;

/**
 * A heuristic variant of breadth-first traversal which sorts the entire queue when the current
 * element is expanded. The sorting order is defined by the vertex cost function.
 * <p>
 * From: Patrick Henry Winston, Artificial Intelligence, Addison-Wesley, 1984
 * 
 * @author Armin Reichert
 * 
 * @param <C>
 *          vertex cost type
 */
public class BestFirstSearchPathFinder<V, E, C extends Comparable<C>> extends BreadthFirstSearchPathFinder<V, E> {

	/**
	 * Creates a best-first traversal instance for the given graph and vertex cost function.
	 * 
	 * @param graph
	 *                 a graph
	 * @param fnCost
	 *                 vertex cost function. Queue is always sorted by increasing cost.
	 */
	public BestFirstSearchPathFinder(Graph<V, E> graph, Function<Integer, C> fnCost) {
		this.graph = graph;
		this.q = new PriorityQueue<>((u, v) -> fnCost.apply(u).compareTo(fnCost.apply(v)));
		this.distFromSource = new int[graph.numVertices()];
	}
}