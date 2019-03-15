package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;
import static java.util.Comparator.comparingDouble;

import java.util.Comparator;
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

	private final Comparator<Integer> vertexPushOrder;

	/**
	 * @param graph
	 *                       a graph
	 * @param fnVertexCost
	 *                       cost function for vertices
	 */
	public HillClimbingSearch(Graph<V, E> graph, ToDoubleFunction<Integer> fnVertexCost) {
		super(graph);
		// reversed because cheapest vertex has to be added to the frontier (stack) last
		vertexPushOrder = comparingDouble(fnVertexCost).reversed();
	}

	@Override
	protected void expand(int v) {
		/*@formatter:off*/
		graph.adj(v)
			.filter(child -> getState(child) == UNVISITED).boxed()
			.sorted(vertexPushOrder)
			.forEach(child -> {
				setState(child, VISITED);
				setParent(child, v);
				frontier.add(child);
			});
		/*@formatter:on*/
	}
}