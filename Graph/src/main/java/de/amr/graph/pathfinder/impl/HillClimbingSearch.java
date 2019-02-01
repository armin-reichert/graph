package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import java.util.Comparator;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;

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
	protected void expand(int current) {
		// sort children by decreasing cost such that cheapest vertex will be on top of stack
		/*@formatter:off*/
		IntStream verticesByDecreasingCost = graph.adj(current)
				.filter(neighbor -> getState(neighbor) == UNVISITED)
				.boxed()
				.sorted(Comparator.comparingDouble(fnVertexCost).reversed())
				.mapToInt(Integer::intValue);
		/*@formatter:on*/
		verticesByDecreasingCost.forEach(neighbor -> {
			stack.push(neighbor);
			setState(neighbor, VISITED);
			setParent(neighbor, current);
		});
	}
}