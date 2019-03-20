package de.amr.graph.pathfinder.api;

import java.util.OptionalInt;

/**
 * Common interface for queues used by graph search implementations.
 * 
 * @author Armin Reichert
 */
public interface VertexQueue {

	/**
	 * Adds the given vertex to this queue.
	 * 
	 * @param vertex
	 *                 vertex
	 */
	void add(int vertex);

	/**
	 * Removes and returns the next vertex from this queue.
	 * 
	 * @return the next vertex to be processed
	 */
	int poll();

	/**
	 * Returns the vertex that will be processed next.
	 * 
	 * @return the next vertexto be processed
	 */
	OptionalInt peek();

	/**
	 * Tells if the queue is empty.
	 * 
	 * @return {@code true} if the queue is empty
	 */
	boolean isEmpty();

	/**
	 * Tells if the given vertex is part of the queue.
	 * 
	 * @param vertex
	 *                 vertex
	 * @return <code>true</code> if the vertex is contained in the queue
	 */
	boolean contains(int vertex);

	/**
	 * Removes all vertices from the queue.
	 */
	void clear();
}