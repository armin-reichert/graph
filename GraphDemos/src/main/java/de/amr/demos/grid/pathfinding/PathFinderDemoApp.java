package de.amr.demos.grid.pathfinding;

import static de.amr.demos.grid.pathfinding.PathFinderDemoApp.Tile.FREE;
import static de.amr.demos.grid.pathfinding.PathFinderDemoApp.Tile.WALL;
import static java.lang.Math.min;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.BitSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.api.Topology;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.TraversalState;
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
	private int source;
	private int target;
	private BitSet solution;

	// UI
	private int draggedCell;
	private int popupCell;
	private int cellSize;
	private PathFinderUI window;
	private GridCanvas canvas;
	private JPopupMenu popupMenu;

	private Action actionSetSource = new AbstractAction("Set Source Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			setSource(popupCell);
			popupCell = -1;
			updatePath();
		}
	};

	private Action actionSetTarget = new AbstractAction("Set Target Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			setTarget(popupCell);
			popupCell = -1;
			updatePath();
		}
	};

	private Action actionResetScene = new AbstractAction("Reset Scene") {

		@Override
		public void actionPerformed(ActionEvent e) {
			resetScene();
			updatePath();
		}
	};

	private MouseListener mouseHandler = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent mouse) {
			if (mouse.getButton() == MouseEvent.BUTTON1) {
				int cell = cellAt(mouse.getX(), mouse.getY());
				setTile(cell, map.get(cell) == WALL ? FREE : WALL);
				updatePath();
			}
		}

		@Override
		public void mouseReleased(MouseEvent mouse) {
			if (draggedCell != -1) {
				// dragging ends
				draggedCell = -1;
				updatePath();
			} else if (mouse.isPopupTrigger()) {
				popupCell = cellAt(mouse.getX(), mouse.getY());
				popupMenu.show(canvas, mouse.getX(), mouse.getY());
			}
		}
	};

	private MouseMotionListener mouseMotionHandler = new MouseMotionAdapter() {

		@Override
		public void mouseDragged(MouseEvent mouse) {
			int cell = cellAt(mouse.getX(), mouse.getY());
			if (cell != draggedCell) {
				// drag enters new cell
				draggedCell = cell;
				setTile(cell, mouse.isShiftDown() ? FREE : WALL);
				updatePath();
			}
		}
	};

	public PathFinderDemoApp(int numCols, int numRows, int canvasSize) {
		algorithm = PathFinderAlgorithm.AStar;
		topology = Top8.get();
		createMap(numCols, numRows, Top8.get());
		map.fill();
		cellSize = canvasSize / numCols;
		passageWidthPct = 95;
		source = map.cell(GridPosition.TOP_LEFT);
		target = map.cell(GridPosition.BOTTOM_RIGHT);
		popupCell = -1;
		draggedCell = -1;
		solution = new BitSet();
		createUI();
		newPathFinder();
		updatePath();
		// GraphUtils.print(map, System.out);
	}

	public void setAlgorithm(PathFinderAlgorithm alg) {
		algorithm = alg;
		newPathFinder();
		updatePath();
	}

	public PathFinderAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setTopology(Topology topology) {
		this.topology = topology;
		createMap(map.numCols(), map.numRows(), topology);
		newPathFinder();
		updatePath();
	}

	public Topology getTopology() {
		return topology;
	}

	private void createUI() {
		canvas = new GridCanvas(map, cellSize);
		canvas.pushRenderer(createRenderer());
		canvas.requestFocus();
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseMotionListener(mouseMotionHandler);
		canvas.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		popupMenu = new JPopupMenu();
		popupMenu.add(actionSetSource);
		popupMenu.add(actionSetTarget);
		popupMenu.addSeparator();
		popupMenu.add(actionResetScene);

		window = new PathFinderUI();
		window.setApp(this);
		window.add(canvas, BorderLayout.CENTER);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
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

	private ConfigurableGridRenderer createRenderer() {
		// ConfigurableGridRenderer r = new PearlsGridRenderer();
		ConfigurableGridRenderer r = new WallPassageGridRenderer();
		r.fnCellSize = () -> cellSize;
		r.fnCellBgColor = cell -> {
			if (cell == source) {
				return Color.GREEN.darker();
			}
			if (cell == target) {
				return Color.BLUE;
			}
			if (solution != null && solution.get(cell)) {
				return Color.RED.brighter();
			}
			if (pathFinder != null) {
				if (pathFinder.getState(cell) == TraversalState.COMPLETED) {
					return new Color(160, 160, 160);
				}
				if (pathFinder.getState(cell) == TraversalState.VISITED) {
					return new Color(220, 220, 220);
				}
			}
			if (map.get(cell) == WALL) {
				return new Color(139, 69, 19);
			}
			return Color.WHITE;
		};
		r.fnText = this::cellText;
		r.fnTextColor = cell -> {
			if (cell == source || cell == target) {
				return Color.WHITE;
			}
			if (solution != null && solution.get(cell)) {
				return Color.WHITE;
			}
			return Color.BLUE;

		};
		r.fnTextFont = () -> new Font("Arial Narrow", Font.BOLD, cellSize * 40 / 100);
		r.fnMinFontSize = () -> 4;
		r.fnPassageWidth = (u, v) -> cellSize * passageWidthPct / 100;
		r.fnPassageColor = (cell, dir) -> Color.WHITE;
		return r;
	}

	private String cellText(int cell) {
		if (pathFinder == null || pathFinder.getState(cell) == TraversalState.UNVISITED) {
			return "";
		}
		if (pathFinder instanceof AStarSearch) {
			AStarSearch<Tile, Double> astar = (AStarSearch<Tile, Double>) pathFinder;
			return String.format("%.0f", astar.getScore(cell));
		} else if (pathFinder instanceof BreadthFirstSearch) {
			BreadthFirstSearch<Tile, Double> bfs = pathFinder;
			return String.format("%.0f", bfs.getCost(cell));
		}
		return "";
	}

	private void setSource(int cell) {
		source = cell;
	}

	private void setTarget(int cell) {
		target = cell;
	}

	private void resetScene() {
		source = map.cell(GridPosition.TOP_LEFT);
		target = map.cell(GridPosition.BOTTOM_RIGHT);
		map.vertices().forEach(cell -> setTile(cell, FREE));
		pathFinder.findPath(source, target);
		solution.clear();
	}

	private void newPathFinder() {
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

	private void computePath() {
		StopWatch watch = new StopWatch();
		watch.start();
		List<Integer> path = pathFinder.findPath(source, target);
		watch.stop();
		System.out.println(String.format("Path finding (%s): %.4f seconds", algorithm, watch.getSeconds()));
		solution = new BitSet(map.numVertices());
		path.forEach(solution::set);
		System.out.println(String.format("Path length: %d", path.size() - 1));
		System.out.println(String.format("Path cost: %.2f", pathFinder.getCost(target)));
	}

	private void updatePath() {
		computePath();
		canvas.drawGrid();
	}

	private int cellAt(int x, int y) {
		int gridX = min(x / cellSize, map.numCols() - 1), gridY = min(y / cellSize, map.numRows() - 1);
		return map.cell(gridX, gridY);
	}

	private void setTile(int cell, Tile tile) {
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
}