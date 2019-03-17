package de.amr.graph.pathfinder.impl;

import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;

/**
 * The Uniform-Cost Search (Dijkstra) can be seen as A* without heuristics. Only the cost-so-far is
 * used for deciding which vertex to expand next.
 * 
 * @author Armin Reichert
 */
public class DijkstraSearch extends AStarSearch {

	public DijkstraSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(graph, fnEdgeCost, (u, v) -> 0);
	}
}