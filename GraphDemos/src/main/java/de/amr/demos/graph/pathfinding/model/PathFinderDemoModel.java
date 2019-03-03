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

public class PathFinderDemoModel {

	private final Map<PathFinderAlgorithm, BreadthFirstSearch<Tile, Double>> pathFinders;
	private final Map<PathFinderAlgorithm, Result> results;
	private GridGraph<Tile, Double> map;
	private PathFinderAlgorithm selectedAlgorithm;
	private int source;
	private int target;
	private int mapSize;
	private Topology topology;

	public PathFinderDemoModel() {
		pathFinders = new EnumMap<>(PathFinderAlgorithm.class);
		results = new EnumMap<>(PathFinderAlgorithm.class);
		mapSize = 10;
		topology = Top8.get();
		newMap();
	}

	private void newMap() {
		GridGraph<Tile, Double> oldMap = map;
		map = new GridGraph<>(mapSize, mapSize, topology, v -> Tile.BLANK, this::distance, UndirectedEdge::new);
		if (oldMap == null) {
			map.fill();
		} else {
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
	}

	public void resizeMap(int size) {
		if (size != mapSize) {
			mapSize = size;
			newMap();
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
	}

	public void runPathFinders() {
		for (PathFinderAlgorithm algorithm : PathFinderAlgorithm.values()) {
			BreadthFirstSearch<Tile, Double> pathFinder = pathFinders.get(algorithm);
			Result r = new Result();
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
	}

	public Map<PathFinderAlgorithm, BreadthFirstSearch<Tile, Double>> getPathFinders() {
		return pathFinders;
	}

	public Map<PathFinderAlgorithm, Result> getResults() {
		return results;
	}

	public GridGraph<Tile, Double> getMap() {
		return map;
	}

	public void setMapSize(int size) {
		if (mapSize != size) {
			mapSize = size;
			newMap();
		}
	}

	public int getMapSize() {
		return mapSize;
	}

	public PathFinderAlgorithm getSelectedAlgorithm() {
		return selectedAlgorithm;
	}

	public void setSelectedAlgorithm(PathFinderAlgorithm selectedAlgorithm) {
		this.selectedAlgorithm = selectedAlgorithm;
	}

	public BreadthFirstSearch<Tile, Double> getSelectedPathFinder() {
		return pathFinders.get(selectedAlgorithm);
	}

	public Result getSelectedResult() {
		return results.get(selectedAlgorithm);
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
		return topology;
	}

	public void setTopology(Topology topology) {
		if (topology != this.topology) {
			this.topology = topology;
			newMap();
			newPathFinders();
			runPathFinders();
		}
	}
}