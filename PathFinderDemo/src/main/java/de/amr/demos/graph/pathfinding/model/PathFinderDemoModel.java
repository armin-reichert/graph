package de.amr.demos.graph.pathfinding.model;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import java.util.BitSet;
import java.util.EnumMap;
import java.util.Map;

import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.Topology;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DijkstraSearch;
import de.amr.util.StopWatch;

/**
 * Model for path finder demo app.
 * 
 * @author Armin Reichert
 */
public class PathFinderDemoModel {

	private final Map<PathFinderAlgorithm, BreadthFirstSearch<Tile, Double>> pathFinders;
	private final Map<PathFinderAlgorithm, PathFinderResult> results;
	private GridGraph<Tile, Double> map;
	private int mapSize;
	private int source;
	private int target;

	public PathFinderDemoModel() {
		this(10, Top8.get());
	}

	public PathFinderDemoModel(int mapSize, Topology topology) {
		pathFinders = new EnumMap<>(PathFinderAlgorithm.class);
		results = new EnumMap<>(PathFinderAlgorithm.class);
		this.mapSize = 10;
		newMap(topology);
	}

	private void newMap(Topology topology) {
		GridGraph<Tile, Double> oldMap = map;
		map = new GridGraph<>(mapSize, mapSize, topology, v -> Tile.BLANK, this::distance, UndirectedEdge::new);
		if (oldMap == null) {
			map.fill();
			return;
		}
		for (int row = 0; row < map.numRows(); ++row) {
			for (int col = 0; col < map.numCols(); ++col) {
				if (!oldMap.isValidRow(row) || !oldMap.isValidCol(col)) {
					continue;
				}
				int cell = map.cell(col, row);
				Tile tile = oldMap.get(oldMap.cell(col, row));
				map.set(cell, tile);
				if (tile == WALL) {
					map.neighbors(cell).forEach(neighbor -> {
						if (map.adjacent(cell, neighbor)) {
							map.removeEdge(cell, neighbor);
						}
					});
				} else {
					map.neighbors(cell).filter(neighbor -> map.get(neighbor) != WALL).forEach(neighbor -> {
						if (!map.adjacent(cell, neighbor)) {
							map.addEdge(cell, neighbor);
						}
					});
				}
			}
		}
	}

	public void resizeMap(int size) {
		if (size != mapSize) {
			mapSize = size;
			int sourceCol = map.col(source), sourceRow = map.row(source);
			int targetCol = map.col(target), targetRow = map.row(target);
			newMap(map.getTopology());
			if (!map.isValidCol(sourceCol) || !map.isValidRow(sourceRow)) {
				source = 0;
			} else {
				source = map.cell(sourceCol, sourceRow);
			}
			if (!map.isValidCol(targetCol) || !map.isValidRow(targetRow)) {
				target = map.numVertices() - 1;
			} else {
				target = map.cell(targetCol, targetRow);
			}
		}
	}

	public void changeTile(int cell, Tile tile) {
		if (cell == source || cell == target || map.get(cell) == tile) {
			return;
		}
		map.set(cell, tile);
		map.neighbors(cell).filter(neighbor -> map.get(neighbor) != WALL).forEach(neighbor -> {
			if (tile == BLANK) {
				if (!map.adjacent(cell, neighbor)) {
					map.addEdge(cell, neighbor);
				}
			} else {
				if (map.adjacent(cell, neighbor)) {
					map.removeEdge(cell, neighbor);
				}
			}
		});
	}

	private double distance(int u, int v) {
		return 10 * map.euclidean(u, v);
	}

	private BreadthFirstSearch<Tile, Double> createPathFinder(PathFinderAlgorithm algorithm) {
		switch (algorithm) {
		case AStar:
			return new AStarSearch<>(map, e -> e, this::distance);
		case BFS:
			return new BreadthFirstSearch<>(map, this::distance);
		case Dijkstra:
			return new DijkstraSearch<>(map, e -> e);
		case GreedyBestFirst:
			return new BestFirstSearch<>(map, v -> distance(v, target), this::distance);
		}
		throw new IllegalArgumentException();
	}

	public void newPathFinders() {
		for (PathFinderAlgorithm algorithm : PathFinderAlgorithm.values()) {
			pathFinders.put(algorithm, createPathFinder(algorithm));
		}
		results.clear();
	}

	public void runPathFinders() {
		for (PathFinderAlgorithm algorithm : PathFinderAlgorithm.values()) {
			runPathFinder(algorithm);
		}
	}

	public void runPathFinder(PathFinderAlgorithm algorithm) {
		BreadthFirstSearch<Tile, Double> pathFinder = pathFinders.get(algorithm);
		PathFinderResult r = new PathFinderResult();
		StopWatch watch = new StopWatch();
		watch.start();
		r.path = pathFinder.findPath(source, target);
		watch.stop();
		r.solutionCells = new BitSet(map.numVertices());
		r.path.forEach(r.solutionCells::set);
		r.pathLength = r.path.size() - 1;
		r.pathCost = pathFinder.getCost(target);
		r.runningTimeMillis = watch.getNanos() / 1_000_000;
		r.numVisitedVertices = map.vertices().filter(v -> pathFinder.getState(v) != TraversalState.UNVISITED)
				.count();
		results.put(algorithm, r);
	}

	public Map<PathFinderAlgorithm, BreadthFirstSearch<Tile, Double>> getPathFinders() {
		return pathFinders;
	}

	public BreadthFirstSearch<Tile, Double> getPathFinder(PathFinderAlgorithm algorithm) {
		return pathFinders.get(algorithm);
	}

	public Map<PathFinderAlgorithm, PathFinderResult> getResults() {
		return results;
	}

	public GridGraph<Tile, Double> getMap() {
		return map;
	}

	public void setMapSize(int size) {
		if (mapSize != size) {
			mapSize = size;
			newMap(map.getTopology());
		}
	}

	public int getMapSize() {
		return mapSize;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public Topology getTopology() {
		return map.getTopology();
	}

	public void setTopology(Topology topology) {
		if (topology != map.getTopology()) {
			newMap(topology);
			newPathFinders();
		}
	}
}