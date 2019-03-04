package de.amr.graph.pathfinder.api;

/**
 * Observer interface for graph search algorithms like DFS or BFS.
 * 
 * @author Armin Reichert
 */
public interface GraphSearchObserver {

	void vertexAddedToFrontier(int vertex);

	void vertexRemovedFromFrontier(int vertex);

	void vertexStateChanged(int vertex, TraversalState oldState, TraversalState newState);

	void edgeTraversed(int either, int other);
}