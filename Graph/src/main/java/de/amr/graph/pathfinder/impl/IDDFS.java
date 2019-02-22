package de.amr.graph.pathfinder.impl;

import java.util.Collections;
import java.util.List;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.api.Frontier;

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
public class IDDFS<V, E> extends GraphSearch<V, E> {

	private DepthLimitedDFS<V, E> dls;

	public IDDFS(Graph<V, E> graph) {
		super(graph);
	}

	@Override
	public Frontier frontier() {
		return dls != null ? dls.frontier() : null;
	}

	@Override
	public List<Integer> findPath(int source, int target) {
		for (int depth = 0; depth < graph.numVertices(); ++depth) {
			dls = new DepthLimitedDFS<>(graph, depth);
			observers.forEach(dls::addObserver);
			List<Integer> path = dls.findPath(source, target);
			observers.forEach(dls::removeObserver);
			if (!path.isEmpty()) {
				return path;
			}
		}
		return Collections.emptyList();
	}
}