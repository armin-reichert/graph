package de.amr.easy.graph.pathfinder.api;

import java.util.List;

public interface PathFinder {

	/**
	 * Traverses the graph starting from the given source until the target is reached.
	 * 
	 * @param source
	 *                 source vertex
	 * @param target
	 *                 target vertex
	 */
	void traverseGraph(int source, int target);

	/**
	 * @param target
	 *                 target vertex
	 * @return path from source to target vertex
	 */
	List<Integer> path(int target);
}