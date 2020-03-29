package de.amr.graph.pathfinder.impl;

import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.impl.queue.MinPQ_VertexQueue;

/**
 * A heuristic variant of breadth-first-search, sorting the entire queue when a
 * vertex is expanded. The sorting order is defined by the specified vertex cost
 * function.
 * <p>
 * From: Patrick Henry Winston, Artificial Intelligence, Addison-Wesley, 1984
 * 
 * @author Armin Reichert
 */
public class BestFirstSearch extends AbstractGraphSearch<MinPQ_VertexQueue, BasicSearchInfo> {

	private final ToDoubleFunction<Integer> fnEstimatedCost;

	/**
	 * Creates a best-first traversal instance for the given graph and vertex cost
	 * estimation and uniform edge cost.
	 * 
	 * @param graph           a graph
	 * @param fnEstimatedCost estimated vertex cost. Queue will always be sorted by
	 *                        increasing priority.
	 */
	public BestFirstSearch(Graph<?, ?> graph, ToDoubleFunction<Integer> fnEstimatedCost) {
		this(graph, fnEstimatedCost, (u, v) -> 1);
	}

	/**
	 * Creates a best-first traversal instance for the given graph and vertex cost
	 * estimation.
	 * 
	 * @param graph           a graph
	 * @param fnEstimatedCost vertex priority function. Queue will always be sorted
	 *                        by increasing priority.
	 * @param fnEdgeCost      edge cost function
	 */
	public BestFirstSearch(Graph<?, ?> graph, ToDoubleFunction<Integer> fnEstimatedCost,
			ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(graph, fnEdgeCost, new MinPQ_VertexQueue(fnEstimatedCost));
		this.fnEstimatedCost = fnEstimatedCost;
	}

	/**
	 * Returns the estimated cost for a vertex.
	 * 
	 * @param v a vertex
	 * @return the estimated cost (e.g. an heuristic value)
	 */
	public double getEstimatedCost(int v) {
		return fnEstimatedCost.applyAsDouble(v);
	}
}