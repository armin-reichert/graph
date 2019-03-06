package de.amr.graph.pathfinder.api;

import java.util.List;

/**
 * Path finder in a graph.
 * 
 * @author Armin Reichert
 */
public interface PathFinder {

	Double INFINITE_COST = Double.MAX_VALUE;

	/**
	 * Finds a path between the given vertices.
	 * 
	 * @param source
	 *                 the source vertex
	 * @param target
	 *                 the target vertex
	 * @return the path as a list of vertices or an empty list if there is no path from source to
	 *         target.
	 * 
	 */
	List<Integer> findPath(int source, int target);
}