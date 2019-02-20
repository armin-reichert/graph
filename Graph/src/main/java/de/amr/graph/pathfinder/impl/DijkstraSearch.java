package de.amr.graph.pathfinder.impl;

import java.util.function.ToDoubleFunction;

import de.amr.graph.core.api.Graph;

/**
 * The Uniform-Cost Search (Dijkstra) can be seen as A* without heuristics. Only the cost-so-far is
 * used for deciding which vertex to expand next.
 * 
 * @author Armin Reichert
 *
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 */
public class DijkstraSearch<V, E> extends AStarSearch<V, E> {

	public DijkstraSearch(Graph<V, E> graph, ToDoubleFunction<E> fnEdgeCost) {
		super(graph, fnEdgeCost, (u, v) -> 0);
	}
}