package de.amr.graph.pathfinder.api;

import java.util.List;

/**
 * Finds a path between two vertices of a graph.
 * 
 * @author Armin Reichert
 */
public interface PathFinder {

	/**
	 * A path between the given vertices or an empty list if there is none.
	 */
	List<Integer> path(int source, int target);
}