package de.amr.graph.pathfinder.impl;

import java.util.function.Function;

import de.amr.graph.core.api.Graph;

/**
 * The Dijkstra algorithm is just A* without heuristics. Only the cost from the source is used when
 * the current vertex is expanded.
 * 
 * @author Armin Reichert
 *
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 */
public class DijkstraSearch<V, E> extends AStarSearch<V, E> {

	public DijkstraSearch(Graph<V, E> graph, Function<E, Integer> fnEdgeCost) {
		super(graph, fnEdgeCost, (u, v) -> 0);
	}
}
