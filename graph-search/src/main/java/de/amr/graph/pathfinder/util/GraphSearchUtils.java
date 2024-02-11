package de.amr.graph.pathfinder.util;

import de.amr.graph.core.api.Graph;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;

public interface GraphSearchUtils {

	/**
	 * Checks if the given cells are connected by some path.
	 * 
	 * @param u a cell
	 * @param v a cell
	 * @return {@code true} if there exists a path connecting the given cells
	 */
	public static <V, E> boolean areConnected(Graph<V, E> graph, int u, int v) {
		return new BreadthFirstSearch(graph).findPath(u, v) != Path.NULL;
	}

	public static <V, E> boolean isConnectedGraph(Graph<V, E> graph) {
		BreadthFirstSearch bfs = new BreadthFirstSearch(graph);
		bfs.exploreGraph(0);
		return graph.vertices().filter(v -> bfs.getState(v) == TraversalState.UNVISITED).count() == 0;
	}
}