package de.amr.graph.pathfinder.api;

/**
 * States of a graph vertex with regards to graph traversal.
 * 
 * @author Armin Reichert
 */
public enum TraversalState {
	/** Vertex has not been visited yet. */
	UNVISITED,
	/** Vertex has been visited. */
	VISITED,
	/** Vertex has been visited and will not be touched again. */
	COMPLETED;
}