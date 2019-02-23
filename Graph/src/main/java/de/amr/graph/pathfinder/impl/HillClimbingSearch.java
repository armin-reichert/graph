package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;
import static java.util.Comparator.comparingDouble;

import java.util.function.ToDoubleFunction;

import de.amr.graph.core.api.Graph;

/**
 * Heuristic depth-first search ("Hill Climbing") where the children of the current vertex are
 * expanded by increasing cost.
 * <p>
 * From: Patrick Henry Winston, Artificial Intelligence, Addison-Wesley, 1984
 * 
 * @author Armin Reichert
 * 
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 */
public class HillClimbingSearch<V, E> extends DepthFirstSearch<V, E> {

	private final ToDoubleFunction<Integer> fnVertexCost;

	/**
	 * @param graph
	 *                       a graph
	 * @param fnVertexCost
	 *                       cost function for vertices
	 */
	public HillClimbingSearch(Graph<V, E> graph, ToDoubleFunction<Integer> fnVertexCost) {
		super(graph);
		this.fnVertexCost = fnVertexCost;
	}

	@Override
	protected void expandFrontier(int current) {
		/*@formatter:off*/
		graph.adj(current)
			.filter(child -> getState(child) == UNVISITED).boxed()
			// sort decreasing such that cheapest vertex will be on top of stack
			.sorted(comparingDouble(fnVertexCost).reversed())
			.forEach(child -> {
				setState(child, VISITED);
				setParent(child, current);
				frontier.add(child);
			});
		/*@formatter:on*/
	}
}