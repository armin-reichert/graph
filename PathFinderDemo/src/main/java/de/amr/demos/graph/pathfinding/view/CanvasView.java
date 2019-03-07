package de.amr.demos.graph.pathfinding.view;

import static de.amr.graph.pathfinder.api.PathFinder.INFINITE_COST;
import static java.lang.Math.min;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;

import de.amr.demos.graph.pathfinding.controller.Controller;
import de.amr.demos.graph.pathfinding.model.Model;
import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.ui.animation.AbstractAnimation;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.GridCellRenderer;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BestFirstSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * View showing map and path finder animations.
 * 
 * @author Armin Reichert
 */
public class CanvasView extends GridCanvas {

	class PathFinderAnimation extends AbstractAnimation implements GraphSearchObserver {

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
	private PathFinderAnimation animation;
	private JPopupMenu contextMenu;
	private int selectedCell;

	public CanvasView(GridGraph2D<?, ?> grid, int cellSize) {
		super(grid, cellSize);
		style = RenderingStyle.BLOCKS;
		ConfigurableGridRenderer r = createMapRenderer(cellSize);
		pushRenderer(r);
		setBorder(BorderFactory.createLineBorder(r.getModel().getGridBgColor(), 1));
		animation = new PathFinderAnimation();
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

	public PathFinderAnimation getAnimation() {
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

	public void init(Model model, Controller controller) {
		this.model = model;
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

	// Renderer

	private Color getCellBackground(int cell) {
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
		if (pf.getState(cell) == TraversalState.COMPLETED) {
			return Color.ORANGE;
		}
		if (pf.getState(cell) == TraversalState.VISITED) {
			return Color.YELLOW;
		}
		return Color.WHITE;
	}

	private String formatValue(double value) {
		return value == INFINITE_COST ? "" : String.format("%.0f", value);
	}

	private boolean partOfSolution(int cell) {
		return model.getResult(controller.getSelectedAlgorithm()).solutionCells.get(cell);
	}

	private ConfigurableGridRenderer createMapRenderer(int cellSize) {

		GridCellRenderer cellBlockRenderer = new GridCellRenderer() {

			final int inset = 4;
			final Font font = new Font("Arial Narrow", Font.PLAIN, 12);

			private void scaleFont(Graphics2D g, int percent) {
				g.setFont(font.deriveFont(Font.PLAIN, cellSize * percent / 100));
			}

			private Rectangle2D box(Graphics2D g, String text) {
				return g.getFontMetrics().getStringBounds(text, g);
			}

			@Override
			public void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {

				int cellX = grid.col(cell) * getCellSize();
				int cellY = grid.row(cell) * getCellSize();

				// cell square
				g.setColor(getCellBackground(cell));
				g.fillRect(cellX, cellY, cellSize, cellSize);
				g.setColor(new Color(160, 160, 160));
				g.drawRect(cellX, cellY, cellSize, cellSize);

				// check if text gets drawn
				BreadthFirstSearch<Tile, Double> pf = model.getPathFinder(controller.getSelectedAlgorithm());
				if (!showCost || pf.getState(cell) == TraversalState.UNVISITED) {
					return;
				}

				// cell text color
				if (cell == model.getSource() || cell == model.getTarget() || partOfSolution(cell)) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.BLUE);
				}

				g.setFont(font);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.translate(cellX, cellY);

				// cell text
				Rectangle2D box;
				if (pf.getClass() == AStarSearch.class) {
					// G-value
					String gCost = formatValue(pf.getCost(cell));
					scaleFont(g, 30);
					box = box(g, gCost);
					g.drawString(gCost, inset, (int) box.getHeight());
					// H-value
					String hCost = formatValue(model.distance(cell, model.getTarget()));
					scaleFont(g, 30);
					box = box(g, hCost);
					g.drawString(hCost, (int) (cellSize - box.getWidth() - inset), (int) box.getHeight());
					// F-value
					AStarSearch<Tile, Double> astar = (AStarSearch<Tile, Double>) pf;
					String fCost = formatValue(astar.getScore(cell));
					scaleFont(g, 50);
					box = box(g, fCost);
					g.drawString(fCost, (int) (cellSize - box.getWidth()) / 2, cellSize - inset);
				} else if (pf.getClass() == BestFirstSearch.class) {
					// H-value
					String hCost = formatValue(model.distance(cell, model.getTarget()));
					scaleFont(g, 30);
					box = box(g, hCost);
					g.drawString(hCost, inset, (int) box.getHeight());
					// G-value
					String gCost = formatValue(pf.getCost(cell));
					scaleFont(g, 50);
					box = box(g, gCost);
					g.drawString(gCost, (int) (cellSize - box.getWidth()) / 2, cellSize - inset);
				} else {
					// G-value
					String gCost = formatValue(pf.getCost(cell));
					scaleFont(g, 50);
					box = box(g, gCost);
					g.drawString(gCost, (int) (cellSize - box.getWidth()) / 2,
							cellSize / 2 + g.getFontMetrics().getDescent());
				}
				g.translate(-cellX, -cellY);
			}
		};

		ConfigurableGridRenderer r;
		if (style == RenderingStyle.BLOCKS) {
			r = new WallPassageGridRenderer(cellBlockRenderer);
			r.fnPassageWidth = (u, v) -> cellSize - 1;
		} else if (style == RenderingStyle.PEARLS) {
			r = new PearlsGridRenderer();
			r.fnCellBgColor = this::getCellBackground;
			r.fnPassageWidth = (u, v) -> 1;
		} else {
			throw new IllegalArgumentException();
		}
		r.fnGridBgColor = () -> new Color(160, 160, 160);
		r.fnCellSize = () -> cellSize;
		r.fnPassageColor = (cell, dir) -> Color.WHITE;
		return r;
	}

}