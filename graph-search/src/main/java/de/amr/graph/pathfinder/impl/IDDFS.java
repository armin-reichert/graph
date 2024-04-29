package de.amr.graph.pathfinder.impl;

import java.util.HashSet;
import java.util.Set;

import de.amr.graph.core.api.Graph;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.Path;

/**
 * Iterative deepening depth-first search (IDDFS).
 * 
 * @author Armin Reichert
 */
public class IDDFS extends AbstractGraphSearch {

	private DepthLimitedDFS dls;

	public IDDFS(Graph<?, ?> graph) {
		super(graph);
	}

	@Override
	public Path findPath(int sourceVertex, int targetVertex) {
		Set<GraphSearchObserver> observersCopy = new HashSet<>(observers);
		removeAllObservers();
		for (int depth = 0; depth < graph.numVertices(); ++depth) {
			dls = new DepthLimitedDFS(graph, depth);
			observersCopy.forEach(dls::addObserver);
			vertexInfoMap.clear();
			Path path = dls.findPath(sourceVertex, targetVertex);
			observersCopy.forEach(dls::removeObserver);
			if (path.numVertices() != 0) {
				return path;
			}
		}
		return Path.NULL;
	}

	@Override
	public TraversalState getState(int v) {
		return dls.getState(v);
	}

	@Override
	public int getParent(int v) {
		return dls.getParent(v);
	}
}