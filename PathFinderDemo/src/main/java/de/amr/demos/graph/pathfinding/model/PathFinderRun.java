package de.amr.demos.graph.pathfinding.model;

import java.util.BitSet;
import java.util.Collections;
import java.util.List;

import de.amr.graph.pathfinder.api.PathFinder;
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * Represents of a path finder run.
 * 
 * @author Armin Reichert
 */
public class PathFinderRun {

	private final GraphSearch<Tile, Double, ?> pathFinder;
	private final List<Integer> path;
	private final BitSet pathCells;
	private final float runningTimeMillis;
	private final double cost;
	private final long numOpenVertices;
	private final long numClosedVertices;

	PathFinderRun(GraphSearch<Tile, Double, ?> pathFinder) {
		this.pathFinder = pathFinder;
		this.path = Collections.emptyList();
		this.pathCells = new BitSet();
		this.runningTimeMillis = 0;
		this.cost = PathFinder.INFINITE_COST;
		this.numOpenVertices = 0;
		this.numClosedVertices = 0;
	}

	PathFinderRun(GraphSearch<Tile, Double, ?> pathFinder, List<Integer> path, float runningTimeMillis,
			double cost, long numOpenVertices, long numClosedVertices) {
		this.pathFinder = pathFinder;
		this.path = path;
		this.pathCells = new BitSet();
		path.forEach(pathCells::set);
		this.runningTimeMillis = runningTimeMillis;
		this.cost = cost;
		this.numOpenVertices = numOpenVertices;
		this.numClosedVertices = numClosedVertices;
	}

	public GraphSearch<Tile, Double, ?> getPathFinder() {
		return pathFinder;
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