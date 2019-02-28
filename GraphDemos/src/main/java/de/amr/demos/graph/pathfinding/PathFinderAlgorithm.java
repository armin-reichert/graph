package de.amr.demos.graph.pathfinding;

import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DijkstraSearch;

public enum PathFinderAlgorithm {
	AStar(AStarSearch.class),
	Dijkstra(DijkstraSearch.class),
	GreedyBestFirst(BestFirstSearch.class),
	BFS(BreadthFirstSearch.class);

	private final Class<?> implementation;

	private PathFinderAlgorithm(Class<?> implementation) {
		this.implementation = implementation;
	}

	public Class<?> getImplementation() {
		return implementation;
	}
}