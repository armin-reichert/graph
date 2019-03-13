package de.amr.graph.pathfinder.api;

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
	 *            vertex
	 */
	void add(int vertex);

	/**
	 * Removes and returns the next vertex from this queue.
	 * 
	 * @return next vertex
	 */
	int next();

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
	 *            vertex
	 * @return <code>true</code> if the vertex is contained in the queue
	 */
	boolean contains(int vertex);

	/**
	 * Removes all vertices from the queue.
	 */
	void clear();
}