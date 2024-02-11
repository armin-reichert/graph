package de.amr.graph.pathfinder.impl;

import de.amr.graph.core.api.TraversalState;

/**
 * Info attached to vertices of a graph being searched.
 * 
 * @author Armin Reichert
 */
public class BasicSearchInfo {

	public int parent;
	public TraversalState state;
	public double cost;

	@Override
	public String toString() {
		return String.format("[parent: %d, state: %s, cost: %.2f]", parent, state, cost);
	}
}