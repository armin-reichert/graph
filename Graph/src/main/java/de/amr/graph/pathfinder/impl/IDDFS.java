package de.amr.graph.pathfinder.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.TraversalState;

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
	public void exploreGraph(int source, int target) {
		Set<GraphSearchObserver> observersCopy = new HashSet<>(observers);
		removeAllObservers();
		for (int depth = 0; depth < graph.numVertices(); ++depth) {
			dls = new DepthLimitedDFS<>(graph, depth);
			observersCopy.forEach(dls::addObserver);
			stateMap.clear();
			parentMap.clear();
			List<Integer> path = dls.findPath(source, target);
			observersCopy.forEach(dls::removeObserver);
			if (!path.isEmpty()) {
				return;
			}
		}
	}

	@Override
	public TraversalState getState(int v) {
		return dls.getState(v);
	}

	@Override
	public int getParent(int v) {
		return dls.getParent(v);
	}

	@Override
	public List<Integer> buildPath(int source, int target) {
		return dls.buildPath(source, target);
	}
}