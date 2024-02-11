package de.amr.graph.pathfinder.impl;

import java.util.Objects;
import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.impl.queue.FIFOVertexQueue;

/**
 * Breadth-first graph search.
 * 
 * @author Armin Reichert
 */
public class BreadthFirstSearch extends AbstractGraphSearch {

	public BreadthFirstSearch(Graph<?, ?> graph) {
		this(graph, (u, v) -> 1);
	}

	public BreadthFirstSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(graph);
		this.frontier = new FIFOVertexQueue();
		this.fnEdgeCost = Objects.requireNonNull(fnEdgeCost);
	}
}