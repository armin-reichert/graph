package de.amr.graph.pathfinder.impl;

import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;
import de.amr.graph.core.api.TraversalState;

public class BidiAStarSearch extends BidiGraphSearch<AStarSearch, AStarSearch> {

	public BidiAStarSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost,
			ToDoubleBiFunction<Integer, Integer> fnEstimatedCostForward,
			ToDoubleBiFunction<Integer, Integer> fnEstimatedCostBackwards) {
		super(new AStarSearch(graph, fnEdgeCost, fnEstimatedCostForward),
				new AStarSearch(graph, fnEdgeCost, fnEstimatedCostBackwards));
	}

	@Override
	public int getParent(int v) {
		return getForwardSearch().getState(v) != TraversalState.UNVISITED ? getForwardSearch().getParent(v)
				: getBackwardsSearch().getParent(v);
	}

	@Override
	public double getCost(int v) {
		return getForwardSearch().getState(v) != TraversalState.UNVISITED ? getForwardSearch().getCost(v)
				: getBackwardsSearch().getCost(v);
	}

	public double getScore(int v) {
		return getForwardSearch().getState(v) != TraversalState.UNVISITED ? getForwardSearch().getScore(v)
				: getBackwardsSearch().getScore(v);
	}

	public double getEstimatedCost(int v) {
		return getForwardSearch().getState(v) != TraversalState.UNVISITED ? getForwardSearch().getEstimatedCostToTarget(v)
				: getBackwardsSearch().getEstimatedCostToTarget(v);
	}
}