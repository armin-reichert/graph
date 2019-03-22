package de.amr.graph.pathfinder.impl;

import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.impl.queue.FIFO_VertexQueue;

/**
 * Breadth-first graph search.
 * 
 * @author Armin Reichert
 */
public class BreadthFirstSearch extends AbstractGraphSearch<FIFO_VertexQueue> {

	public BreadthFirstSearch(Graph<?, ?> graph) {
		super(graph, (u, v) -> 1, new FIFO_VertexQueue());
	}

	public BreadthFirstSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(graph, fnEdgeCost, new FIFO_VertexQueue());
	}
}