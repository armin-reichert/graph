package de.amr.demos.graph.pathfinding.model;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

public class PathFinderResult {

	public List<Integer> path;
	public BitSet solutionCells;
	public float runningTimeMillis;
	public int pathLength;
	public double pathCost;
	public long numVisitedVertices;
	
	
	public PathFinderResult() {
		clear();
	}
	
	public void clear() {
		path = Collections.emptyList();
		solutionCells = new BitSet();
		runningTimeMillis = 0;
		pathLength = -1;
		pathCost = -1;
		numVisitedVertices = 0;
	}
}