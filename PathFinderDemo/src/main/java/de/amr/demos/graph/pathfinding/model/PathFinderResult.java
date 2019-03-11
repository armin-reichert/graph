package de.amr.demos.graph.pathfinding.model;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import de.amr.graph.pathfinder.api.PathFinder;

/**
 * Result of a path finder run.
 * 
 * @author Armin Reichert
 */
public class PathFinderResult {

	public static PathFinderResult NONE = new PathFinderResult(Collections.emptyList(), 0,
			PathFinder.INFINITE_COST, 0, 0);

	private final List<Integer> path;
	private final BitSet pathCells;
	private final float runningTimeMillis;
	private final double cost;
	private final long numOpenVertices;
	private final long numClosedVertices;

	public PathFinderResult(List<Integer> path, float runningTimeMillis, double cost, long numOpenVertices,
			long numClosedVertices) {
		this.path = path;
		this.pathCells = new BitSet();
		path.forEach(pathCells::set);
		this.runningTimeMillis = runningTimeMillis;
		this.cost = cost;
		this.numOpenVertices = numOpenVertices;
		this.numClosedVertices = numClosedVertices;
	}

	public boolean pathContains(int cell) {
		return pathCells.get(cell);
	}

	public float getRunningTimeMillis() {
		return runningTimeMillis;
	}

	public int getPathLength() {
		return path.size() - 1;
	}

	public double getCost() {
		return cost;
	}

	public long getNumOpenVertices() {
		return numOpenVertices;
	}

	public long getNumClosedVertices() {
		return numClosedVertices;
	}
}