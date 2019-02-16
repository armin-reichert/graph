package de.amr.graph.pathfinder.impl;

import java.util.Collections;
import java.util.List;
import java.util.Queue;

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
public class IDDFS<V, E> extends GraphSearch<V, E> {

	private DepthLimitedDFS<V, E> dls;

	public IDDFS(Graph<V, E> graph) {
		super(graph);
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

	// implemented only to be able to run DFS animation for this pathfinder:

	@Override
	protected Queue<Integer> createFrontier() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void addToFrontier(int v) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected int removeFromFrontier() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected boolean isFrontierEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean partOfFrontier(int v) {
		return dls != null && dls.partOfFrontier(v);
	}

	@Override
	public double getCost(int v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCost(int v, double value) {
		throw new UnsupportedOperationException();
	}
}
