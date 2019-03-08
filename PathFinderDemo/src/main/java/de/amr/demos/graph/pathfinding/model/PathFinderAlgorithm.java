package de.amr.demos.graph.pathfinding.model;

import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DijkstraSearch;

public enum PathFinderAlgorithm {
	BFS(BreadthFirstSearch.class),
	GreedyBestFirst(BestFirstSearch.class),
	Dijkstra(DijkstraSearch.class),
	AStar(AStarSearch.class);

	private final Class<?> implementation;

	private PathFinderAlgorithm(Class<?> implementation) {
		this.implementation = implementation;
	}

	public Class<?> getImplementation() {
		return implementation;
	}

	@Override
	public String toString() {
		switch (this) {
		case AStar:
			return "A*";
		case GreedyBestFirst:
			return "Greedy Best-First";
		case BFS:
			return "Breadth-First";
		default:
			return super.toString();
		}
	}
}