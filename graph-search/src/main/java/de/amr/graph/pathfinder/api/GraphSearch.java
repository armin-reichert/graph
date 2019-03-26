package de.amr.graph.pathfinder.api;

import java.util.Optional;
import java.util.OptionalInt;

import de.amr.graph.core.api.TraversalState;

public interface GraphSearch {

	/**
	 * Explores the graph starting from the given source vertex until all reachable vertices have been
	 * visited.
	 * 
	 * @param source
	 *                 source vertex
	 */
	default void exploreGraph(int source) {
		exploreGraph(source, -1);
	}

	/**
	 * Explores the graph starting from the given source vertex until the given target vertex has been
	 * found or all reachable vertices have been visited.
	 * 
	 * @param source
	 *                 source vertex
	 * @param target
	 *                 target vertex
	 * 
	 * @return {@code true} if the target has been found
	 */
	default boolean exploreGraph(int source, int target) {
		start(source, target);
		while (canExplore()) {
			if (exploreVertex()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tells if there is some vertex left to explore.
	 * 
	 * @return {@code true} if there is some vertex left to explore
	 */
	boolean canExplore();

	/**
	 * Explores the next vertex.
	 * 
	 * @return {@code true} if the target has been found
	 */
	boolean exploreVertex();

	/**
	 * Start the search. Subclasses may modify this.
	 */
	void start(int source, int target);

	/**
	 * Returns the source vertex of this search.
	 * 
	 * @return the source vertex
	 */
	int getSource();

	/**
	 * Returns the target vertex of this search.
	 * 
	 * @return the target vertex
	 */
	int getTarget();

	/**
	 * Returns the vertex currently being processed. This vertex has already been removed from the
	 * frontier.
	 * 
	 * @return the current vertex
	 */
	int getCurrentVertex();

	/**
	 * Returns the vertex that will be processed next (if frontier is not changed).
	 * 
	 * @return the current vertex
	 */
	OptionalInt getNextVertex();

	/**
	 * Returns the traversal state of the given vertex. The default state is
	 * {@link TraversalState#UNVISITED}.
	 * 
	 * @param v
	 *            vertex
	 * @return vertex state
	 */
	TraversalState getState(int v);

	/**
	 * Returns the parent vertex for the given vertex. Default is <code>-1</code>.
	 * 
	 * @param v
	 *            vertex
	 * @return parent vertex or <code>-1</code>
	 */
	int getParent(int v);

	/**
	 * Set the parent vertex for the given vertex.
	 * 
	 * @param child
	 *                child vertex
	 * @param p
	 *                parent vertex
	 */
	void setParent(int child, int parent);

	/**
	 * Returns the cost ("g"-value) of the given vertex.
	 * 
	 * @param v
	 *            vertex
	 * @return vertex cost
	 */
	double getCost(int v);

	/**
	 * Sets the cost for the given vertex.
	 * 
	 * @param v
	 *                vertex
	 * @param value
	 *                cost value
	 */
	void setCost(int v, double value);

	/**
	 * Returns the maximum cost/distance of any vertex reachable from the source.
	 * 
	 * @return the maximum distance
	 */
	double getMaxCost();

	/**
	 * Returns a vertex with maximum distance encountered in this traversal.
	 * 
	 * @return a vertex with maximum distance or empty
	 */
	Optional<Integer> getMaxCostVertex();

}