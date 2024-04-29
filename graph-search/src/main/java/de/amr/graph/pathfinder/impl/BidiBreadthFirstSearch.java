package de.amr.graph.pathfinder.impl;

import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;

public class BidiBreadthFirstSearch extends BidiGraphSearch<BreadthFirstSearch,BreadthFirstSearch> {

	public BidiBreadthFirstSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(new BreadthFirstSearch(graph, fnEdgeCost), new BreadthFirstSearch(graph, fnEdgeCost));
	}
}