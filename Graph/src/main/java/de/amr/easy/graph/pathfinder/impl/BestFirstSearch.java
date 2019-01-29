package de.amr.easy.graph.pathfinder.impl;

import java.util.PriorityQueue;
import java.util.function.Function;

import de.amr.easy.graph.core.api.Graph;

/**
 * A heuristic variant of breadth-first-search, sorting the entire queue when a vertex is expanded.
 * The sorting order is defined by the specified vertex cost function.
 * <p>
 * From: Patrick Henry Winston, Artificial Intelligence, Addison-Wesley, 1984
 * 
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 * @param <C>
 *          vertex cost type
 * 
 * @author Armin Reichert
 */
public class BestFirstSearch<V, E, C extends Comparable<C>> extends BreadthFirstSearch<V, E> {

	/**
	 * Creates a best-first traversal instance for the given graph and vertex cost function.
	 * 
	 * @param graph
	 *                       a graph
	 * @param fnVertexCost
	 *                       vertex cost function. Queue is sorted by increasing cost.
	 */
	public BestFirstSearch(Graph<V, E> graph, Function<Integer, C> fnVertexCost) {
		super(graph, new PriorityQueue<>((u, v) -> fnVertexCost.apply(u).compareTo(fnVertexCost.apply(v))));
	}
}