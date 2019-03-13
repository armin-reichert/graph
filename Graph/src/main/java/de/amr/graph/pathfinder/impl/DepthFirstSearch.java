package de.amr.graph.pathfinder.impl;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.impl.queue.LIFO_VertexQueue;

/**
 * Depth-first traversal of a graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch<V, E> extends GraphSearch<V, E, LIFO_VertexQueue> {

	public DepthFirstSearch(Graph<V, E> graph) {
		super(graph);
		frontier = new LIFO_VertexQueue();
	}
}