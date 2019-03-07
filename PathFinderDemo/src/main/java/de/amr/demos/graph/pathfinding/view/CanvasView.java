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
import de.amr.demos.graph.pathfinding.model.PathFinderResult;
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
		style = RenderingStyle.BLOCKS;
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

	private Color getGridCellBackground(int cell) {
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

	private String formatCost(double value) {
		return value == INFINITE_COST ? "" : String.format("%.0f", value);
	}

	private boolean partOfSolution(int cell) {
		PathFinderResult result = model.getResult(controller.getSelectedAlgorithm());
		return result.solutionCells.get(cell);
	}

	private ConfigurableGridRenderer createMapRenderer(int cellSize) {

		GridCellRenderer cellRenderer = new GridCellRenderer() {

			private void drawString(Graphics2D g, String s, double dx, double dy) {
				g.translate(dx, dy);
				g.drawString(s, 0, 0);
				g.translate(-dx, -dy);
			}

			@Override
			public void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
				final int cellX = grid.col(cell) * getCellSize();
				final int cellY = grid.row(cell) * getCellSize();
				g.translate(cellX, cellY);
				g.setColor(getGridCellBackground(cell));
				g.fillRect(0, 0, cellSize, cellSize);
				g.setColor(new Color(160, 160, 160));
				g.drawRect(0, 0, cellSize, cellSize);
				BreadthFirstSearch<Tile, Double> pf = model.getPathFinder(controller.getSelectedAlgorithm());
				if (showCost && pf.getState(cell) != TraversalState.UNVISITED) {
					g.setColor(Color.BLUE);
					if (cell == model.getSource() || cell == model.getTarget() || partOfSolution(cell)) {
						g.setColor(Color.WHITE);
					}
					Font font = new Font("Arial Narrow", Font.PLAIN, cellSize * 33 / 100);
					g.setFont(font);
					g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
					int inset = 3;
					if (pf.getClass() == AStarSearch.class) {
						AStarSearch<Tile, Double> astar = (AStarSearch<Tile, Double>) pf;
						String gCost = formatCost(pf.getCost(cell));
						Rectangle2D box = g.getFontMetrics().getStringBounds(gCost, g);
						drawString(g, gCost, inset, box.getHeight());
						String hCost = formatCost(model.distance(cell, model.getTarget()));
						box = g.getFontMetrics().getStringBounds(hCost, g);
						drawString(g, hCost, cellSize - box.getWidth() - inset, box.getHeight());
						String fCost = formatCost(astar.getScore(cell));
						g.setFont(font.deriveFont(Font.BOLD, cellSize * 50 / 100));
						box = g.getFontMetrics().getStringBounds(fCost, g);
						drawString(g, fCost, (cellSize - box.getWidth()) / 2, cellSize - inset);
					} else {
						String gCost = formatCost(pf.getCost(cell));
						g.setFont(font.deriveFont(Font.BOLD, cellSize * 50 / 100));
						Rectangle2D box = g.getFontMetrics().getStringBounds(gCost, g);
						drawString(g, gCost, (cellSize - box.getWidth()) / 2,
								cellSize / 2 + g.getFontMetrics().getDescent());
					}
				}
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
				g.translate(-cellX, -cellY);
			}
		};

		if (style == RenderingStyle.BLOCKS) {
			ConfigurableGridRenderer r = new WallPassageGridRenderer(cellRenderer);
			r.fnGridBgColor = () -> new Color(160, 160, 160);
			r.fnCellSize = () -> cellSize;
			r.fnPassageWidth = (u, v) -> style == RenderingStyle.PEARLS ? 1 : cellSize - 1;
			r.fnPassageColor = (cell, dir) -> Color.WHITE;
			return r;
		}

		if (style == RenderingStyle.PEARLS) {
			ConfigurableGridRenderer r = new PearlsGridRenderer();
			r.fnGridBgColor = () -> new Color(160, 160, 160);
			r.fnCellSize = () -> cellSize;
			r.fnCellBgColor = this::getGridCellBackground;
			r.fnPassageWidth = (u, v) -> style == RenderingStyle.PEARLS ? 1 : cellSize - 1;
			r.fnPassageColor = (cell, dir) -> Color.WHITE;
			return r;
		}

		throw new IllegalArgumentException();
	}
}