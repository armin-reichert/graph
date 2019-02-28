package de.amr.demos.graph.pathfinding;

import static de.amr.demos.graph.pathfinding.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.Tile.WALL;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.demos.graph.pathfinding.ui.PathFinderUI;
import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.api.Topology;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DijkstraSearch;

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
	private PathFinderAlgorithm algorithm;
	private int source;
	private int target;
	private final BitSet solutionCells;

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
		solutionCells = new BitSet(map.numVertices());
		createPathFinders();
		algorithm = PathFinderAlgorithm.AStar;

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

	private void createPathFinders() {
		pathFinders = new LinkedHashMap<>();
		Arrays.stream(PathFinderAlgorithm.values()).forEach(algorithm -> {
			pathFinders.put(algorithm, createPathFinder(algorithm, map, target));
		});
	}

	public BreadthFirstSearch<Tile, Double> getSelectedPathFinder() {
		return pathFinders.get(algorithm);
	}

	public List<Integer> computePath() {
		List<Integer> path = getSelectedPathFinder().findPath(source, target);
		solutionCells.clear();
		path.forEach(solutionCells::set);
		return path;
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

	public PathFinderAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(PathFinderAlgorithm alg) {
		algorithm = alg;
		createPathFinders();
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
		createPathFinders();
	}

	public void setTopology(Topology topology) {
		if (topology != map.getTopology()) {
			map = createMap(map.numCols(), map.numRows(), topology);
			createPathFinders();
		}
	}

	public BitSet getSolution() {
		return solutionCells;
	}
}