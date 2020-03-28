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
}