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
		System.out.println("DLS with depth limit " + depthLimit);
	}

	@Override
	protected void expandFrontier(int v) {
		if (depth.get(v) < depthLimit) {
			super.expandFrontier(v);
		}
	}

	@Override
	protected void setParent(int child, int parent) {
		depth.put(child, parent != -1 ? depth.get(parent) + 1 : 0);
		super.setParent(child, parent);
	}

	@Override
	protected int removeFromFrontier() {
		int v = super.removeFromFrontier();
		System.out.println(String.format("Vertex %d ('%s') at depth %d", v, graph.get(v), depth.get(v)));
		return v;
	}
}