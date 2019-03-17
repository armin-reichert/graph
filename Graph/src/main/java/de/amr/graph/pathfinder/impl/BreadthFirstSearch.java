package de.amr.graph.pathfinder.impl;

import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.impl.queue.FIFO_VertexQueue;

/**
 * Breadth-first graph search.
 * 
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 * 
 * @author Armin Reichert
 */
public class BreadthFirstSearch<V, E> extends GraphSearch<V, E, FIFO_VertexQueue> {

	public BreadthFirstSearch(Graph<V, E> graph) {
		super(graph, (u, v) -> 1, new FIFO_VertexQueue());
	}

	public BreadthFirstSearch(Graph<V, E> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(graph, fnEdgeCost, new FIFO_VertexQueue());
	}
}