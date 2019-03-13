package de.amr.demos.graph.pathfinding.model;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.api.Topology;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DijkstraSearch;
import de.amr.graph.pathfinder.impl.GraphSearch;
import de.amr.util.StopWatch;

/**
 * Model for path finder demo app.
 * 
 * @author Armin Reichert
 */
public class PathFinderModel {

	private static final PathFinderRun NO_RUN = new PathFinderRun(null);

	private final Map<PathFinderAlgorithm, PathFinderRun> runs = new EnumMap<>(PathFinderAlgorithm.class);
	private GridGraph<Tile, Double> map;
	private int source;
	private int target;

	public PathFinderModel() {
		this(10, Top8.get());
	}

	public PathFinderModel(int mapSize, Topology topology) {
		newMap(mapSize, topology);
		source = map.cell(mapSize / 4, mapSize / 2);
		target = map.cell(mapSize * 3 / 4, mapSize / 2);
		newRuns();
	}

	private void newMap(int mapSize, Topology topology) {
		GridGraph<Tile, Double> oldMap = map;
		map = new GridGraph<>(mapSize, mapSize, topology, v -> Tile.BLANK, this::distance, UndirectedEdge::new);
		map.fill();
		if (oldMap == null) {
			return;
		}

		float scalingFactor = (float) map.numCols() / oldMap.numCols();
		// copy walls into map, keep aspect ratio
		for (int oldCell = 0; oldCell < oldMap.numVertices(); ++oldCell) {
			int oldRow = oldMap.row(oldCell), oldCol = oldMap.col(oldCell);
			int row = scaledCoord(oldRow, scalingFactor), col = scaledCoord(oldCol, scalingFactor);
			if (map.isValidRow(row) && map.isValidCol(col)) {
				setTile(map.cell(col, row), oldMap.get(oldCell));
			}
		}

		boolean mapped = false;
		for (GridPosition pos : GridPosition.values()) {
			if (pos == GridPosition.CENTER) {
				continue;
			}
			if (source == oldMap.cell(pos)) {
				source = map.cell(pos);
				mapped = true;
				break;
			}
		}
		if (!mapped) {
			int sourceCol = scaledCoord(oldMap.col(source), scalingFactor),
					sourceRow = scaledCoord(oldMap.row(source), scalingFactor);
			if (map.isValidCol(sourceCol) && map.isValidRow(sourceRow)) {
				source = map.cell(sourceCol, sourceRow);
			} else {
				source = 0;
			}
		}

		mapped = false;
		for (GridPosition pos : GridPosition.values()) {
			if (pos == GridPosition.CENTER) {
				continue;
			}
			if (target == oldMap.cell(pos)) {
				target = map.cell(pos);
				mapped = true;
				break;
			}
		}
		if (!mapped) {
			int targetCol = scaledCoord(oldMap.col(target), scalingFactor),
					targetRow = scaledCoord(oldMap.row(target), scalingFactor);
			if (map.isValidCol(targetCol) && map.isValidRow(targetRow)) {
				target = map.cell(targetCol, targetRow);
			} else {
				target = map.numVertices() - 1;
			}
		}

		setTile(source, Tile.BLANK);
		setTile(target, Tile.BLANK);
	}

	private static int scaledCoord(int coord, float scaling) {
		return (int) (scaling * coord);
	}

	public void resizeMap(int size) {
		if (size != map.numRows()) {
			newMap(size, map.getTopology());
			newRuns();
		}
	}

	public void clearMap() {
		map.vertices().forEach(cell -> setTile(cell, Tile.BLANK));
	}

	public void setTile(int cell, Tile tile) {
		if (map.get(cell) == tile) {
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

	public double distance(int u, int v) {
		return map.euclidean(u, v);
	}

	private GraphSearch<Tile, Double, ?> newPathFinder(PathFinderAlgorithm algorithm) {
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
		throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
	}

	public GraphSearch<Tile, Double, ?> getPathFinder(PathFinderAlgorithm algorithm) {
		return runs.get(algorithm).getPathFinder();
	}

	public void newRuns() {
		for (PathFinderAlgorithm algorithm : PathFinderAlgorithm.values()) {
			newRun(algorithm);
		}
	}

	public void newRun(PathFinderAlgorithm algorithm) {
		runs.put(algorithm, new PathFinderRun(newPathFinder(algorithm)));
	}

	public void runAllPathFinders() {
		newRuns();
		for (PathFinderAlgorithm algorithm : PathFinderAlgorithm.values()) {
			runPathFinder(algorithm);
		}
	}

	public void runPathFinder(PathFinderAlgorithm algorithm) {
		GraphSearch<Tile, Double, ?> pf = getPathFinder(algorithm);
		StopWatch watch = new StopWatch();
		watch.start();
		Path path = Path.computePath(source, target, pf);
		watch.stop();
		long numOpenVertices = map.vertices().filter(v -> pf.getState(v) == TraversalState.VISITED).count();
		long numClosedVertices = map.vertices().filter(v -> pf.getState(v) == TraversalState.COMPLETED).count();
		runs.put(algorithm, new PathFinderRun(pf, path, watch.getNanos() / 1_000_000f, pf.getCost(target),
				numOpenVertices, numClosedVertices));
	}

	public PathFinderRun getRun(PathFinderAlgorithm algorithm) {
		return Optional.ofNullable(runs.get(algorithm)).orElse(NO_RUN);
	}

	public GridGraph<Tile, Double> getMap() {
		return map;
	}

	public int getMapSize() {
		return map.numRows();
	}

	public void setMapSize(int mapSize) {
		if (mapSize != map.numRows()) {
			newMap(mapSize, map.getTopology());
		}
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
			newMap(map.numRows(), topology);
		}
	}
}