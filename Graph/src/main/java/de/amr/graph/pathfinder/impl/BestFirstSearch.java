package de.amr.graph.pathfinder.impl;

import static java.util.Comparator.comparingDouble;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.ToDoubleFunction;

import de.amr.graph.core.api.Graph;

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
 * 
 * @author Armin Reichert
 */
public class BestFirstSearch<V, E> extends BreadthFirstSearch<V, E> {

	private final ToDoubleFunction<Integer> fnVertexCost;

	/**
	 * Creates a best-first traversal instance for the given graph and vertex cost function.
	 * 
	 * @param graph
	 *                       a graph
	 * @param fnVertexCost
	 *                       vertex cost function. Queue will always be sorted by increasing cost.
	 */
	public BestFirstSearch(Graph<V, E> graph, ToDoubleFunction<Integer> fnVertexCost) {
		super(graph);
		this.fnVertexCost = fnVertexCost;
	}

	@Override
	protected Queue<Integer> createFrontier() {
		return new PriorityQueue<>(comparingDouble(fnVertexCost));
	}
}