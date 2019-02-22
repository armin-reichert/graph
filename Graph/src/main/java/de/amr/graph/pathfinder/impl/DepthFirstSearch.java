package de.amr.graph.pathfinder.impl;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.api.Frontier;

/**
 * Depth-first traversal of a graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch<V, E> extends GraphSearch<V, E> {

	private final LIFOFrontier frontier = new LIFOFrontier();

	public DepthFirstSearch(Graph<V, E> graph) {
		super(graph);
	}

	@Override
	public Frontier frontier() {
		return frontier;
	}
}