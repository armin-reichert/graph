package de.amr.graph.pathfinder.impl;

import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;

public class BidiDijkstraSearch extends BidiGraphSearch<DijkstraSearch, DijkstraSearch> {

	public BidiDijkstraSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		super(new DijkstraSearch(graph, fnEdgeCost), new DijkstraSearch(graph, fnEdgeCost));
	}
}