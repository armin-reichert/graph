package de.amr.demos.grid.pathfinding;

import static de.amr.demos.grid.pathfinding.PathFinderDemoApp.Tile.FREE;
import static de.amr.demos.grid.pathfinding.PathFinderDemoApp.Tile.WALL;
import static java.lang.Math.min;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.BitSet;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.api.Topology;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DijkstraSearch;
import de.amr.util.StopWatch;

/**
 * Demo application for A* algorithm.
 * 
 * @author Armin Reichert
 */
public class PathFinderDemoApp {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		int gridSize = 20;
		int windowSize = Toolkit.getDefaultToolkit().getScreenSize().height * 90 / 100;
		EventQueue.invokeLater(() -> new PathFinderDemoApp(gridSize, gridSize, windowSize));
	}

	// model
	public enum Tile {
		FREE, WALL;
	}

	public enum PathFinderAlgorithm {
		BFS, Dijkstra, AStar;
	}

	private GridGraph2D<Tile, Double> map;
	private BreadthFirstSearch<Tile, Double> pathFinder;
	private PathFinderAlgorithm algorithm;
	private Topology topology;
	private int passageWidthPct;
	private int cellSize;
	private int source;
	private int target;
	private final BitSet solution;

	private PathFinderUI window;

	public PathFinderDemoApp(int numCols, int numRows, int canvasSize) {
		algorithm = PathFinderAlgorithm.AStar;
		topology = Top8.get();
		cellSize = canvasSize / numCols;
		passageWidthPct = 95;

		createMap(numCols, numRows, Top8.get());
		map.fill();
		source = map.cell(GridPosition.TOP_LEFT);
		target = map.cell(GridPosition.BOTTOM_RIGHT);

		solution = new BitSet(map.numVertices());
		createPathFinder();
		createUI();
		updatePath();
		// GraphUtils.print(map, System.out);
	}

	private void createUI() {
		window = new PathFinderUI();
		window.setApp(this);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	private void createPathFinder() {
		switch (algorithm) {
		case AStar:
			pathFinder = new AStarSearch<>(map, e -> e, (u, v) -> 10 * map.euclidean(u, v));
			break;
		case BFS:
			pathFinder = new BreadthFirstSearch<>(map, (u, v) -> 10 * map.euclidean(u, v));
			break;
		case Dijkstra:
			pathFinder = new DijkstraSearch<>(map, e -> e);
			break;
		}
	}

	private void createMap(int numCols, int numRows, Topology top) {
		GridGraph<Tile, Double> newMap = new GridGraph<>(numCols, numRows, top, v -> Tile.FREE, (u, v) -> 10.0,
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
		map = newMap;
	}

	public void setAlgorithm(PathFinderAlgorithm alg) {
		algorithm = alg;
		createPathFinder();
		updatePath();
	}

	public PathFinderAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
		createMap(map.numCols(), map.numRows(), topology);
		createPathFinder();
		updatePath();
	}

	public Topology getTopology() {
		return topology;
	}

	public void setPassageWidthPct(int passageWidthPct) {
		this.passageWidthPct = passageWidthPct;
	}

	public int getPassageWidthPct() {
		return passageWidthPct;
	}

	public BreadthFirstSearch<Tile, Double> getPathFinder() {
		return pathFinder;
	}

	public BitSet getSolution() {
		return solution;
	}

	public GridGraph2D<Tile, Double> getMap() {
		return map;
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
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

	public void setTile(int cell, Tile tile) {
		if (cell == source || cell == target || map.get(cell) == tile) {
			return;
		}
		map.set(cell, tile);
		map.neighbors(cell).filter(neighbor -> map.get(neighbor) != WALL).forEach(neighbor -> {
			if (tile == FREE) {
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

	public void resetScene() {
		source = map.cell(GridPosition.TOP_LEFT);
		target = map.cell(GridPosition.BOTTOM_RIGHT);
		map.vertices().forEach(cell -> setTile(cell, FREE));
		pathFinder.findPath(source, target);
		solution.clear();
	}

	private void computePath() {
		StopWatch watch = new StopWatch();
		watch.start();
		List<Integer> path = pathFinder.findPath(source, target);
		watch.stop();
		System.out.println(String.format("Path finding (%s): %.4f seconds", algorithm, watch.getSeconds()));
		solution.clear();
		path.forEach(solution::set);
		System.out.println(String.format("Path length: %d", path.size() - 1));
		System.out.println(String.format("Path cost: %.2f", pathFinder.getCost(target)));
	}

	public void updatePath() {
		computePath();
		window.redraw(false);
	}

	public int cellAt(int x, int y) {
		int gridX = min(x / cellSize, map.numCols() - 1), gridY = min(y / cellSize, map.numRows() - 1);
		return map.cell(gridX, gridY);
	}
}