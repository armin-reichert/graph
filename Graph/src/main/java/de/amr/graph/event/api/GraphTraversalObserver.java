package de.amr.graph.event.api;

import de.amr.graph.pathfinder.api.TraversalState;

/**
 * Observer interface for graph traversals like DFS or BFS.
 * 
 * @author Armin Reichert
 */
public interface GraphTraversalObserver {

	/**
	 * Called when a vertex has been traversed.
	 * 
	 * @param v
	 *                   the traversed vertex
	 * @param oldState
	 *                   the old traversal state
	 * @param newState
	 *                   the new traversal state
	 */
	void vertexTraversed(int v, TraversalState oldState, TraversalState newState);

	/**
	 * Called when an edge has been traversed.
	 * 
	 * @param either
	 *                 either vertex of traversed edge
	 * @param other
	 *                 other vertex of traversed edge
	 */
	void edgeTraversed(int either, int other);
}
