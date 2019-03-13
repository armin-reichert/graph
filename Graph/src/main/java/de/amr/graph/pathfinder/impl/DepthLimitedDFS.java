package de.amr.graph.pathfinder.impl;

import java.util.HashMap;
import java.util.Map;

import de.amr.graph.core.api.Graph;

/**
 * Depth-limited depth-first search (DLS).
 * 
 * @author Armin Reichert
 *
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 */
public class DepthLimitedDFS<V, E> extends DepthFirstSearch<V, E> {

	private int depthLimit;
	private Map<Integer, Integer> depth = new HashMap<>();

	public DepthLimitedDFS(Graph<V, E> graph, int depthLimit) {
		super(graph);
		this.depthLimit = depthLimit;
		// System.out.println("DLS with depth limit " + depthLimit);
	}

	@Override
	protected void expand(int v, int source, int target) {
		if (depth.get(v) < depthLimit) {
			super.expand(v, source, target);
		}
	}

	@Override
	protected void setParent(int child, int parent) {
		depth.put(child, parent != -1 ? depth.get(parent) + 1 : 0);
		super.setParent(child, parent);
	}
}