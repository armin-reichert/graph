package de.amr.graph.pathfinder.impl;

import de.amr.graph.core.api.Graph;

/**
 * Depth-first traversal of a graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch<V, E> extends GraphSearch<V, E> {

	public DepthFirstSearch(Graph<V, E> graph) {
		super(graph);
		frontier = new LIFOFrontier();
	}
}