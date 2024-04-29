package de.amr.graph.pathfinder.impl;

import java.util.HashMap;
import java.util.Map;

import de.amr.graph.core.api.Graph;

/**
 * Depth-limited depth-first search (DLS).
 * 
 * @author Armin Reichert
 */
public class DepthLimitedDFS extends DepthFirstSearch {

	private int depthLimit;
	private Map<Integer, Integer> depth = new HashMap<>();

	public DepthLimitedDFS(Graph<?, ?> graph, int depthLimit) {
		super(graph);
		this.depthLimit = depthLimit;
	}

	@Override
	protected void expand(int v) {
		if (depth.get(v) < depthLimit) {
			super.expand(v);
		}
	}

	@Override
	public void setParent(int child, int parent) {
		depth.put(child, parent != Graph.NO_VERTEX ? depth.get(parent) + 1 : 0);
		super.setParent(child, parent);
	}
}