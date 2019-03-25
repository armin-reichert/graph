package de.amr.graph.pathfinder.impl;

import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;

public class BidiAStarSearch extends BidiGraphSearch<AStarSearch, AStarSearch> {

	public BidiAStarSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost,
			ToDoubleBiFunction<Integer, Integer> fnEstimatedCostForward,
			ToDoubleBiFunction<Integer, Integer> fnEstimatedCostBackwards) {
		super(new AStarSearch(graph, fnEdgeCost, fnEstimatedCostForward),
				new AStarSearch(graph, fnEdgeCost, fnEstimatedCostBackwards));
	}
}