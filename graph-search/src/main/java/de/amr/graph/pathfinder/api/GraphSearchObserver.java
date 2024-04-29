package de.amr.graph.pathfinder.api;

import de.amr.graph.core.api.TraversalState;

/**
 * Observer interface for graph search algorithms like DFS or BFS.
 * 
 * @author Armin Reichert
 */
public interface GraphSearchObserver {

	default void vertexAddedToFrontier(int vertex) {
	}

	default void vertexRemovedFromFrontier(int vertex) {
	}

	default void vertexStateChanged(int vertex, TraversalState oldState, TraversalState newState) {
	}

	default void edgeTraversed(int either, int other) {
	}
}