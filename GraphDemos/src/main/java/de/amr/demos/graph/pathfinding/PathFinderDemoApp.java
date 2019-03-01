package de.amr.demos.graph.pathfinding;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import java.awt.EventQueue;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.Result;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.ui.PathFinderUI;
import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.GridPosition;
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
 * Demo application for path finder algorithms.
 * 
 * @author Armin Reichert
 */
public class PathFinderDemoApp {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> new PathFinderDemoApp(25));
	}

	private GridGraph<Tile, Double> map;
	private Map<PathFinderAlgorithm, BreadthFirstSearch<Tile, Double>> pathFinders;
	private Map<PathFinderAlgorithm, Result> results;
	private PathFinderAlgorithm selectedAlgorithm;
	private int source;
	private int target;

	private static BreadthFirstSearch<Tile, Double> createPathFinder(PathFinderAlgorithm algorithm,
			GridGraph<Tile, Double> map, Integer target) {
		switch (algorithm) {
		case AStar:
			return new AStarSearch<>(map, e -> e, (u, v) -> 10 * map.euclidean(u, v));
		case BFS:
			return new BreadthFirstSearch<>(map, (u, v) -> 10 * map.euclidean(u, v));
		case Dijkstra:
			return new DijkstraSearch<>(map, e -> e);
		case GreedyBestFirst:
			return new BestFirstSearch<>(map, v -> 10 * map.euclidean(v, target),
					(u, v) -> 10 * map.euclidean(u, v));
		}
		throw new IllegalArgumentException();
	}

	public PathFinderDemoApp(int gridSize) {
		map = createMap(gridSize, gridSize, Top8.get());
		source = map.cell(GridPosition.TOP_LEFT);
		target = map.cell(GridPosition.BOTTOM_RIGHT);
		selectedAlgorithm = PathFinderAlgorithm.AStar;
		pathFinders = new EnumMap<>(PathFinderAlgorithm.class);
		results = new EnumMap<>(PathFinderAlgorithm.class);
		newPathFinders();
		runPathFinders();

		// create UI
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		PathFinderUI window = new PathFinderUI(this);
		window.initState();
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	private void newPathFinders() {
		for (PathFinderAlgorithm algorithm : PathFinderAlgorithm.values()) {
			pathFinders.put(algorithm, createPathFinder(algorithm, map, target));
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

	public void resetScene() {
		source = map.cell(GridPosition.TOP_LEFT);
		target = map.cell(GridPosition.BOTTOM_RIGHT);
		map.vertices().forEach(cell -> changeTile(cell, BLANK));
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

	private GridGraph<Tile, Double> createMap(int numCols, int numRows, Topology top) {
		GridGraph<Tile, Double> newMap = new GridGraph<>(numCols, numRows, top, v -> Tile.BLANK, (u, v) -> 10.0,
				UndirectedEdge::new);
		newMap.setDefaultEdgeLabel((u, v) -> 10 * newMap.euclidean(u, v));
		newMap.fill();
		if (map != null) {
			map.vertices().forEach(v -> {
				newMap.set(v, map.get(v));
				if (map.get(v) == WALL) {
					newMap.neighbors(v).forEach(neighbor -> {
						if (newMap.adjacent(v, neighbor)) {
							newMap.removeEdge(v, neighbor);
						}
					});
				} else {
					newMap.neighbors(v).filter(neighbor -> newMap.get(neighbor) != WALL).forEach(neighbor -> {
						if (!newMap.adjacent(v, neighbor)) {
							newMap.addEdge(v, neighbor);
						}
					});
				}
			});
		}
		return newMap;
	}

	public GridGraph<Tile, Double> getMap() {
		return map;
	}

	public PathFinderAlgorithm getSelectedAlgorithm() {
		return selectedAlgorithm;
	}

	public void setSelectedAlgorithm(PathFinderAlgorithm alg) {
		selectedAlgorithm = alg;
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
		newPathFinders();
	}

	public void setTopology(Topology topology) {
		if (topology != map.getTopology()) {
			map = createMap(map.numCols(), map.numRows(), topology);
			newPathFinders();
		}
	}

	public BreadthFirstSearch<Tile, Double> getSelectedPathFinder() {
		return pathFinders.get(selectedAlgorithm);
	}

	public Map<PathFinderAlgorithm, Result> getResults() {
		return results;
	}

	public Result getSelectedResult() {
		return results.get(selectedAlgorithm);
	}
}