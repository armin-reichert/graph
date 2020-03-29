package de.amr.graph.pathfinder.impl;

import de.amr.graph.core.api.TraversalState;

/**
 * Info attached to vertices of a graph being searched.
 * 
 * @author Armin Reichert
 */
public class SearchInfo {

	public int parent;
	public TraversalState traversalState;
	public double cost;
	
	@Override
	public String toString() {
		return String.format("(parent:%d,state:%s,cost:%.2g)", parent, traversalState, cost);
	}
}