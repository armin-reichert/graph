package de.amr.graph.pathfinder.impl;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.impl.queue.LIFO_VertexQueue;

/**
 * Depth-first traversal of a graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch extends AbstractGraphSearch<LIFO_VertexQueue> {

	public DepthFirstSearch(Graph<?, ?> graph) {
		super(graph, new LIFO_VertexQueue());
	}
}