package de.amr.demos.grid.pathfinding;

import static de.amr.demos.grid.pathfinding.PathFinderDemoApp.Tile.FREE;
import static de.amr.demos.grid.pathfinding.PathFinderDemoApp.Tile.WALL;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
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
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DijkstraSearch;
import de.amr.graph.pathfinder.impl.GraphSearch;
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
		int size = 32;
		EventQueue.invokeLater(() -> new PathFinderDemoApp(size, size, 800));
	}

	// model
	public enum Tile {
		FREE, WALL;
	}

	public enum PathFinderAlgorithm {
		BFS, Dijkstra, AStar;
	}

	private GridGraph2D<Tile, Integer> map;
	private PathFinderAlgorithm algorithm;
	private GraphSearch<Tile, Integer> pathFinder;
	private int source;
	private int target;
	private BitSet solution;

	// UI
	private int draggedCell;
	private int popupCell;
	private int cellSize;
	private JFrame window;
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

	private Action actionSelectAlgorithm = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent source = (JComponent) e.getSource();
			algorithm = (PathFinderAlgorithm) source.getClientProperty("algorithm");
			System.out.println("Selected " + algorithm);
			updatePath();
		}
	};

	private MouseListener mouseHandler = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent mouse) {
			if (mouse.getButton() == MouseEvent.BUTTON1) {
				int cell = cellAt(mouse.getX(), mouse.getY());
				setTile(cell, mouse.isShiftDown() ? FREE : WALL);
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
		map = new GridGraph<>(numCols, numRows, Top8.get(), v -> Tile.FREE, (u, v) -> getDistance(u, v),
				UndirectedEdge::new);
		map.fill();
		cellSize = canvasSize / numCols;
		source = map.cell(GridPosition.TOP_LEFT);
		target = map.cell(GridPosition.BOTTOM_RIGHT);
		popupCell = -1;
		draggedCell = -1;
		solution = new BitSet();
		createUI();
		updatePath();
		// GraphUtils.print(map, System.out);
	}

	private int getDistance(int u, int v) {
		return (int) round(10 * sqrt(map.euclidean2(u, v)));
	}

	private void createUI() {
		canvas = new GridCanvas(map, cellSize);
		canvas.pushRenderer(createRenderer());
		canvas.requestFocus();
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseMotionListener(mouseMotionHandler);
		popupMenu = new JPopupMenu();
		popupMenu.add(actionSetSource);
		popupMenu.add(actionSetTarget);
		popupMenu.addSeparator();
		popupMenu.add(actionResetScene);
		popupMenu.addSeparator();
		addAlgorithmItems();
		window = new JFrame("Pathfinder demo");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.add(canvas, BorderLayout.CENTER);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	private void addAlgorithmItems() {
		ButtonGroup bg = new ButtonGroup();
		for (PathFinderAlgorithm algorithm : PathFinderAlgorithm.values()) {
			JRadioButtonMenuItem rb = new JRadioButtonMenuItem(actionSelectAlgorithm);
			rb.putClientProperty("algorithm", algorithm);
			rb.setText(algorithm.name());
			rb.setSelected(algorithm == this.algorithm);
			bg.add(rb);
			popupMenu.add(rb);
		}
	}

	private ConfigurableGridRenderer createRenderer() {
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
					return new Color(180, 180, 180);
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
		r.fnTextFont = () -> new Font("Arial", Font.PLAIN, cellSize / 2);
		r.fnMinFontSize = () -> 4;
		r.fnPassageWidth = () -> cellSize - 1;
		r.fnPassageColor = (cell, dir) -> Color.WHITE;
		return r;
	}

	private String cellText(int cell) {
		if (pathFinder == null || pathFinder.getState(cell) == TraversalState.UNVISITED) {
			return "";
		}
		if (pathFinder instanceof AStarSearch) {
			AStarSearch<Tile, Integer> astar = (AStarSearch<Tile, Integer>) pathFinder;
			return String.format("%.0f", astar.getScore(cell));
		} else if (pathFinder instanceof BreadthFirstSearch) {
			BreadthFirstSearch<Tile, Integer> bfs = (BreadthFirstSearch<Tile, Integer>) pathFinder;
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
		pathFinder = null;
		solution.clear();
	}

	private void computePath() {
		switch (algorithm) {
		case AStar:
			pathFinder = new AStarSearch<>(map, i -> i, this::getDistance);
			break;
		case BFS:
			pathFinder = new BreadthFirstSearch<>(map);
			break;
		case Dijkstra:
			pathFinder = new DijkstraSearch<>(map, e -> e);
			break;
		}
		StopWatch watch = new StopWatch();
		watch.start();
		List<Integer> path = pathFinder.findPath(source, target);
		watch.stop();
		System.out.println(String.format("Path finding (%s): %.4f seconds", algorithm, watch.getSeconds()));
		solution = new BitSet(map.numVertices());
		path.forEach(solution::set);
		System.out.println(String.format("Path length: %d", path.size()));
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