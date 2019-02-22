package de.amr.graph.pathfinder.api;

public interface Frontier {

	/**
	 * Adds the given vertex to the frontier.
	 * 
	 * @param v
	 *            vertex
	 */
	void add(int v);

	/**
	 * Removes and returns the next vertex from the frontier.
	 * 
	 * @return next vertex
	 */
	int next();

	/**
	 * Tells if the frontier is empty.
	 * 
	 * @return {@code true} if the frontier is empty
	 */
	boolean isEmpty();

	/**
	 * Tells if the given vertex is part of the frontier.
	 * 
	 * @param v
	 *            vertex
	 * @return <code>true</code> if the vertex is contained in the frontier
	 */
	boolean contains(int v);

	/**
	 * Removes all vertices from the frontier.
	 */
	void clear();

}
