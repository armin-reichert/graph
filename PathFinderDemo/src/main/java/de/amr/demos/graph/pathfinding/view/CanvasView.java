package de.amr.demos.graph.pathfinding.view;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;

import de.amr.demos.graph.pathfinding.controller.Controller;
import de.amr.demos.graph.pathfinding.model.Model;
import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderResult;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.ui.animation.AbstractAnimation;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.PathFinder;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * View showing map and path finder animations.
 * 
 * @author Armin Reichert
 */
public class CanvasView extends GridCanvas {

	class Animation extends AbstractAnimation implements GraphSearchObserver {

		@Override
		public void vertexAddedToFrontier(int v) {
			// state change handles this
		}

		@Override
		public void vertexRemovedFromFrontier(int v) {
			// state change handles this
		}

		@Override
		public void vertexStateChanged(int v, TraversalState oldState, TraversalState newState) {
			delayed(() -> drawGridCell(v));
		}

		@Override
		public void edgeTraversed(int either, int other) {
			delayed(() -> {
				drawGridPassage(either, other, true);
				// TODO fixme
				if (style == RenderingStyle.PEARLS) {
					drawGridCell(either);
					drawGridCell(other);
				}
			});
		}
	}

	private class PathFinderAnimationTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			PathFinderAlgorithm algorithm = controller.getSelectedAlgorithm();
			model.clearResult(algorithm);
			model.newPathFinder(algorithm);
			drawGrid();
			model.getPathFinder(algorithm).addObserver(animation);
			model.runPathFinder(algorithm);
			model.getPathFinder(algorithm).removeObserver(animation);
			return null;
		}

		@Override
		protected void done() {
			drawGrid(); // redraw to highlight solution
		}
	}

	private class MouseHandler extends MouseAdapter {

		private int draggedCell;

		private int cellAt(MouseEvent e) {
			int col = min(e.getX() / getCellSize(), model.getMap().numCols() - 1);
			int row = min(e.getY() / getCellSize(), model.getMap().numRows() - 1);
			return model.getMap().cell(col, row);
		}

		public MouseHandler() {
			draggedCell = -1;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				int cell = cellAt(e);
				controller.flipTileAt(cell);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (draggedCell != -1) {
				// end dragging
				draggedCell = -1;
				if (controller.isAutoRunPathFinders()) {
					controller.runPathFinders();
				}
			} else if (e.isPopupTrigger()) {
				// open popup menu
				selectedCell = cellAt(e);
				showContextMenu(e.getX(), e.getY());
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int cell = cellAt(e);
			if (cell != draggedCell) {
				// drag enters new cell
				draggedCell = cell;
				if (e.isShiftDown()) {
					controller.flipTileAt(cell);
				}
			}
		}
	}

	private Action actionSetSource = new AbstractAction("Start Search Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.setSource(selectedCell);
		}
	};

	private Action actionSetTarget = new AbstractAction("End Search Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.setTarget(selectedCell);
		}
	};

	private Action actionResetScene = new AbstractAction("Reset Scene") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.resetScene();
		}
	};

	private Model model;
	private Controller controller;
	private RenderingStyle style;
	private boolean showCost;
	private Animation animation;
	private JPopupMenu contextMenu;
	private int selectedCell;

	public CanvasView(GridGraph2D<?, ?> grid, int cellSize) {
		super(grid, cellSize);
		ConfigurableGridRenderer r = createMapRenderer(cellSize);
		pushRenderer(r);
		setBorder(BorderFactory.createLineBorder(r.getModel().getGridBgColor(), 1));
		animation = new Animation();
		selectedCell = -1;
		MouseHandler mouse = new MouseHandler();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		contextMenu = new JPopupMenu();
		contextMenu.add(actionSetSource);
		contextMenu.add(actionSetTarget);
		contextMenu.addSeparator();
		contextMenu.add(actionResetScene);
	}

	public Animation getAnimation() {
		return animation;
	}

	public void runPathFinderAnimation() {
		new PathFinderAnimationTask().execute();
	}

	private int getCellSize() {
		return getRenderer().get().getModel().getCellSize();
	}

	@Override
	public void setCellSize(int cellSize) {
		popRenderer();
		pushRenderer(createMapRenderer(cellSize));
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void setStyle(RenderingStyle style) {
		this.style = style;
		clear();
		int cellSize = getCellSize();
		popRenderer();
		pushRenderer(createMapRenderer(cellSize));
		drawGrid();
	}

	public void setShowCost(boolean showCost) {
		this.showCost = showCost;
		drawGrid();
	}

	public void showContextMenu(int x, int y) {
		boolean blank = model.getMap().get(selectedCell) == Tile.BLANK;
		actionSetSource.setEnabled(blank);
		actionSetTarget.setEnabled(blank);
		contextMenu.show(this, x, y);
	}

	private ConfigurableGridRenderer createMapRenderer(int cellSize) {
		ConfigurableGridRenderer r = style == RenderingStyle.BLOCKS ? new WallPassageGridRenderer()
				: new PearlsGridRenderer();
		r.fnGridBgColor = () -> new Color(160, 160, 160);
		r.fnCellSize = () -> cellSize;
		r.fnCellBgColor = cell -> {
			if (model.getMap().get(cell) == Tile.WALL) {
				return new Color(139, 69, 19);
			}
			if (cell == model.getSource()) {
				return Color.BLUE;
			}
			if (cell == model.getTarget()) {
				return Color.GREEN.darker();
			}
			if (partOfSolution(cell)) {
				return Color.RED.brighter();
			}
			GraphSearch<Tile, Double> pf = model.getPathFinder(controller.getSelectedAlgorithm());
			if (pf.getState(cell) == COMPLETED) {
				return Color.ORANGE;
			}
			if (pf.getState(cell) == VISITED) {
				return Color.YELLOW;
			}
			return Color.WHITE;
		};
		r.fnText = cell -> {
			if (!showCost) {
				return "";
			}
			BreadthFirstSearch<Tile, Double> pf = model.getPathFinder(controller.getSelectedAlgorithm());
			double cost = pf.getCost(cell);
			if (pf instanceof AStarSearch) {
				AStarSearch<Tile, Double> astar = (AStarSearch<Tile, Double>) pf;
				cost = astar.getScore(cell);
			}
			return cost != PathFinder.INFINITE_COST ? String.format("%.0f", cost) : "";
		};
		r.fnTextColor = cell -> {
			if (cell == model.getSource() || cell == model.getTarget() || partOfSolution(cell)) {
				return Color.WHITE;
			}
			return Color.BLUE;
		};
		r.fnTextFont = () -> new Font("Arial", Font.PLAIN,
				style == RenderingStyle.PEARLS ? cellSize * 30 / 100 : cellSize * 50 / 100);
		r.fnMinFontSize = () -> 4;
		r.fnPassageWidth = (u, v) -> style == RenderingStyle.PEARLS ? 1 : cellSize - 1;
		r.fnPassageColor = (cell, dir) -> Color.WHITE;
		return r;
	}

	private boolean partOfSolution(int cell) {
		PathFinderResult result = model.getResult(controller.getSelectedAlgorithm());
		return result.solutionCells.get(cell);
	}
}