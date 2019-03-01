package de.amr.demos.graph.pathfinding.model;

import java.util.BitSet;
import java.util.List;

public class Result {

	public List<Integer> path;
	public BitSet solutionCells;
	public float runningTimeMillis;
	public int pathLength;
	public double pathCost;
	public long numVisitedVertices;
}