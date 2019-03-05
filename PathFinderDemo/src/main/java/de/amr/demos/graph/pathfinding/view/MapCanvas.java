package de.amr.demos.graph.pathfinding.view;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;

import de.amr.demos.graph.pathfinding.controller.PathFinderDemoController;
import de.amr.demos.graph.pathfinding.model.PathFinderDemoModel;
import de.amr.demos.graph.pathfinding.model.PathFinderResult;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.ui.animation.AbstractAnimation;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;

class MapCanvas extends GridCanvas {

	public class Animation extends AbstractAnimation implements GraphSearchObserver {

		@Override
		public void vertexAddedToFrontier(int v) {
			delayed(() -> drawGridCell(v));
		}

		@Override
		public void vertexRemovedFromFrontier(int v) {
			delayed(() -> drawGridCell(v));
		}

		@Override
		public void vertexStateChanged(int v, TraversalState oldState, TraversalState newState) {
			delayed(() -> drawGridCell(v));
		}

		@Override
		public void edgeTraversed(int either, int other) {
			delayed(() -> {
				drawGridPassage(either, other, true);
				drawGridCell(either);
				drawGridCell(other);
			});
		}
	}

	private MouseListener mouseHandler = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent mouse) {
			if (mouse.getButton() == MouseEvent.BUTTON1 && mouse.isShiftDown()) {
				int cell = cellAt(mouse.getX(), mouse.getY());
				controller.flipTileAt(cell);
			}
		}

		@Override
		public void mouseReleased(MouseEvent mouse) {
			if (draggedCell != -1) {
				// end dragging
				draggedCell = -1;
				if (controller.isAutoRunPathFinders()) {
					controller.runPathFinders();
				}
			} else if (mouse.isPopupTrigger()) {
				// open popup menu
				selectedCell = cellAt(mouse.getX(), mouse.getY());
				//TODO
//				boolean blank = model.getMap().get(selectedCell) == Tile.BLANK;
//				actionSetSource.setEnabled(blank);
//				actionSetTarget.setEnabled(blank);
				popupMenu.show(MapCanvas.this, mouse.getX(), mouse.getY());
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
				if (mouse.isShiftDown()) {
					controller.flipTileAt(cell);
				}
			}
		}
	};

	private PathFinderDemoModel model;
	private PathFinderDemoController controller;
	private RenderingStyle style;
	private boolean showCost;
	private Animation animation;
	private int draggedCell;
	private int selectedCell;
	private JPopupMenu popupMenu;

	public MapCanvas(GridGraph2D<?, ?> grid, int cellSize) {
		super(grid, cellSize);
		ConfigurableGridRenderer r = createMapRenderer(cellSize);
		pushRenderer(r);
		setBorder(BorderFactory.createLineBorder(r.getModel().getGridBgColor(), 1));
		animation = new Animation();
		selectedCell = -1;
		draggedCell = -1;
		addMouseListener(mouseHandler);
		addMouseMotionListener(mouseMotionHandler);
		popupMenu = new JPopupMenu();
	}
	
	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	public Animation getAnimation() {
		return animation;
	}

	public int getSelectedCell() {
		return selectedCell;
	}

	private int getCellSize() {
		return getRenderer().get().getModel().getCellSize();
	}

	public void setModel(PathFinderDemoModel model) {
		this.model = model;
	}

	public void setController(PathFinderDemoController controller) {
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

	private int cellAt(int x, int y) {
		int gridX = min(x / getCellSize(), model.getMap().numCols() - 1);
		int gridY = min(y / getCellSize(), model.getMap().numRows() - 1);
		return model.getMap().cell(gridX, gridY);
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
			if (isCellPartOfSolution(cell)) {
				return Color.RED.brighter();
			}
			BreadthFirstSearch<Tile, Double> pf = model.getPathFinder(controller.getSelectedAlgorithm());
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
			if (model.getMap().get(cell) == Tile.WALL) {
				return "";
			}
			BreadthFirstSearch<Tile, Double> pf = model.getPathFinder(controller.getSelectedAlgorithm());
			if (pf.getState(cell) == UNVISITED) {
				return "";
			}
			double cost = pf.getCost(cell);
			if (pf instanceof AStarSearch) {
				AStarSearch<Tile, Double> astar = (AStarSearch<Tile, Double>) pf;
				cost = astar.getScore(cell);
			}
			return cost == Double.MAX_VALUE ? "" : String.format("%.0f", cost);
		};
		r.fnTextColor = cell -> {
			if (cell == model.getSource() || cell == model.getTarget() || isCellPartOfSolution(cell)) {
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

	private boolean isCellPartOfSolution(int cell) {
		PathFinderResult result = model.getResult(controller.getSelectedAlgorithm());
		return result != null && result.solutionCells.get(cell);
	}
}
