package de.amr.demos.graph.pathfinding.model;

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

	@Override
	public String toString() {
		switch (this) {
		case AStar:
			return "A* Search";
		case GreedyBestFirst:
			return "Greedy Best-First Search";
		case BFS:
			return "Breadth-First Search";
		default:
			return super.toString();
		}
	}
}