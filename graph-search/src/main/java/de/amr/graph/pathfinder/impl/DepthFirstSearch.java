package de.amr.graph.pathfinder.impl;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.impl.queue.LIFOVertexQueue;

/**
 * Depth-first traversal of a graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch extends AbstractGraphSearch {

	public DepthFirstSearch(Graph<?, ?> graph) {
		super(graph);
		frontier = new LIFOVertexQueue();
	}
}