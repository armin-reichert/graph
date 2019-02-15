package de.amr.graph.pathfinder.impl;

import java.util.List;

import de.amr.graph.core.api.Graph;

/**
 * Iterative deepening depth-first search (IDDFS).
 * 
 * @author Armin Reichert
 *
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 */
public class IterativeDeepeningSearch<V, E> extends DepthFirstSearch<V, E> {

	private DepthLimitedDFS<V, E> dls;

	public IterativeDeepeningSearch(Graph<V, E> graph) {
		super(graph);
	}

	@Override
	public List<Integer> findPath(int source, int target) {
		int depth = 0;
		while (true) {
			dls = new DepthLimitedDFS<>(graph, depth);
			List<Integer> path = dls.findPath(source, target);
			if (!path.isEmpty()) {
				return path;
			}
			++depth;
		}
	}
}
