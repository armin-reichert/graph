package de.amr.demos.graph.pathfinding.view;

import static de.amr.graph.pathfinder.api.Path.INFINITE_COST;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.BasicStroke;
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
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;

import de.amr.demos.graph.pathfinding.controller.Controller;
import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.impl.GridGraph;
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
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * View showing map and path finder animations.
 * 
 * @author Armin Reichert
 */
public class CanvasView extends GridCanvas {

	class PathFinderAnimation extends AbstractAnimation implements GraphSearchObserver {

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
			model.newRun(algorithm);
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

		// grid cell index associated with event
		private int getEventLocation(MouseEvent e) {
			int col = max(0, min(e.getX() / getCellSize(), model.getMap().numCols() - 1));
			int row = max(0, min(e.getY() / getCellSize(), model.getMap().numRows() - 1));
			return model.getMap().cell(col, row);
		}

		public MouseHandler() {
			draggedCell = -1;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				int cell = getEventLocation(e);
				controller.flipTileAt(cell);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (draggedCell != -1) {
				// dragging ends
				draggedCell = -1;
				if (controller.isAutoRunPathFinders()) {
					controller.runAllPathFinders();
				}
			} else if (e.isPopupTrigger()) {
				// open popup menu
				selectedCell = getEventLocation(e);
				boolean blankCellSelected = model.getMap().get(selectedCell) == Tile.BLANK;
				actionSetSource.setEnabled(blankCellSelected);
				actionSetTarget.setEnabled(blankCellSelected);
				contextMenu.show(CanvasView.this, e.getX(), e.getY());
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			int cell = getEventLocation(e);
			if (cell != draggedCell) {
				// dragging into new cell
				draggedCell = cell;
				controller.setTileAt(cell, e.isShiftDown() ? Tile.BLANK : Tile.WALL);
			}
		}
	}

	private Action actionSetSource = new AbstractAction("Search From Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent trigger = (JComponent) e.getSource();
			GridPosition position = (GridPosition) trigger.getClientProperty("position");
			if (position != null) {
				controller.setSource(model.getMap().cell(position));
			} else {
				controller.setSource(selectedCell);
			}
		}
	};

	private Action actionSetTarget = new AbstractAction("Search To Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComponent trigger = (JComponent) e.getSource();
			GridPosition position = (GridPosition) trigger.getClientProperty("position");
			if (position != null) {
				controller.setTarget(model.getMap().cell(position));
			} else {
				controller.setTarget(selectedCell);
			}
		}
	};

	private Action actionResetScene = new AbstractAction("Reset Scene") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.resetScene();
		}
	};

	private PathFinderModel model;
	private Controller controller;
	private RenderingStyle style;
	private boolean showCost;
	private boolean showParent = false;
	private PathFinderAnimation animation;
	private JPopupMenu contextMenu;
	private int selectedCell;
	private int fixedHeight;

	public CanvasView(GridGraph2D<?, ?> grid, int initialHeight) {
		super(grid);
		this.fixedHeight = initialHeight;
		style = RenderingStyle.BLOCKS;
		animation = new PathFinderAnimation();
		selectedCell = -1;
		MouseHandler mouse = new MouseHandler();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		createContextMenu();
	}

	private void createContextMenu() {
		contextMenu = new JPopupMenu();
		contextMenu.add(actionSetSource);
		JMenu sourceMenu = new JMenu("Search From");
		for (GridPosition position : GridPosition.values()) {
			JMenuItem item = sourceMenu.add(actionSetSource);
			item.setText(position.toString());
			item.putClientProperty("position", position);
		}
		contextMenu.add(sourceMenu);
		contextMenu.addSeparator();
		contextMenu.add(actionSetTarget);
		JMenu targetMenu = new JMenu("Search To");
		for (GridPosition position : GridPosition.values()) {
			JMenuItem item = targetMenu.add(actionSetTarget);
			item.setText(position.toString());
			item.putClientProperty("position", position);
		}
		contextMenu.add(targetMenu);
		contextMenu.addSeparator();
		contextMenu.add(actionResetScene);
	}

	public void init(PathFinderModel model, Controller controller) {
		this.model = model;
		this.controller = controller;
		ConfigurableGridRenderer r = createMapRenderer(fixedHeight / model.getMapSize());
		pushRenderer(r);
	}

	public PathFinderAnimation getAnimation() {
		return animation;
	}

	public void runPathFinderAnimation() {
		new PathFinderAnimationTask().execute();
	}

	public void fixHeight(int fixedHeight) {
		this.fixedHeight = fixedHeight;
	}

	private int getCellSize() {
		return getRenderer().get().getModel().getCellSize();
	}

	private void replaceRenderer(int cellSize) {
		if (renderStack.size() > 0) {
			renderStack.pop();
		}
		pushRenderer(createMapRenderer(cellSize));
		clear();
		drawGrid();
	}

	@Override
	public void setGrid(GridGraph<?, ?> grid) {
		super.setGrid(grid);
		int cellSize = (int) Math.floor((float) fixedHeight / grid.numCols());
		replaceRenderer(cellSize);
	}

	public void setStyle(RenderingStyle style) {
		this.style = style;
		replaceRenderer(getCellSize());
	}

	public void setShowCost(boolean showCost) {
		this.showCost = showCost;
		drawGrid();
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
		TraversalState cellState = model.getPathFinder(controller.getSelectedAlgorithm()).getState(cell);
		if (cellState == TraversalState.COMPLETED) {
			return Color.ORANGE;
		}
		if (cellState == TraversalState.VISITED) {
			return Color.YELLOW;
		}
		return Color.WHITE;
	}

	private boolean partOfSolution(int cell) {
		return model.getRun(controller.getSelectedAlgorithm()).pathContains(cell);
	}

	private class BlockCellRenderer implements GridCellRenderer {

		final Font font = new Font("Arial Narrow", Font.PLAIN, 12);
		final int inset;
		final int cellSize;

		public BlockCellRenderer(int cellSize) {
			this.cellSize = cellSize;
			this.inset = cellSize / 10;
		}

		private String formatValue(double value) {
			// display real value multiplied by 10
			return value == INFINITE_COST ? "" : String.format("%.0f", 10 * value);
		}

		private void textSizePct(Graphics2D g, int percent) {
			g.setFont(font.deriveFont(Font.PLAIN, cellSize * percent / 100));
		}

		private Rectangle2D box(Graphics2D g, String text) {
			return g.getFontMetrics().getStringBounds(text, g);
		}

		@SuppressWarnings("unchecked")
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
			if (!showCost || model.getMap().get(cell) == Tile.WALL) {
				return;
			}

			GraphSearch<Tile, Double, ?> pf = model.getPathFinder(controller.getSelectedAlgorithm());

			// cell text color
			if (cell == model.getSource() || cell == model.getTarget() || partOfSolution(cell)) {
				g.setColor(Color.WHITE);
			} else if (pf.getState(cell) == TraversalState.UNVISITED) {
				g.setColor(Color.LIGHT_GRAY);
			} else {
				g.setColor(Color.BLUE);
			}

			g.translate(cellX, cellY);

			// cell text
			g.setFont(font);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			Rectangle2D box;
			if (pf.getClass() == AStarSearch.class) {
				// G-value
				String gCost = formatValue(pf.getCost(cell));
				textSizePct(g, 30);
				box = box(g, gCost);
				g.drawString(gCost, inset, (int) box.getHeight());
				// H-value
				String hCost = formatValue(model.distance(cell, model.getTarget()));
				textSizePct(g, 30);
				box = box(g, hCost);
				g.drawString(hCost, (int) (cellSize - box.getWidth() - inset), (int) box.getHeight());
				// F-value
				AStarSearch<Tile, Double> astar = (AStarSearch<Tile, Double>) pf;
				String fCost = formatValue(astar.getScore(cell));
				textSizePct(g, 50);
				box = box(g, fCost);
				g.drawString(fCost, (int) (cellSize - box.getWidth()) / 2, cellSize - inset);
			} else if (pf.getClass() == BestFirstSearch.class) {
				// H-value
				String hCost = formatValue(model.distance(cell, model.getTarget()));
				textSizePct(g, 30);
				box = box(g, hCost);
				g.drawString(hCost, inset, (int) box.getHeight());
				// G-value
				String gCost = formatValue(pf.getCost(cell));
				textSizePct(g, 50);
				box = box(g, gCost);
				g.drawString(gCost, (int) (cellSize - box.getWidth()) / 2, cellSize - inset);
			} else {
				// G-value
				String gCost = formatValue(pf.getCost(cell));
				textSizePct(g, 50);
				box = box(g, gCost);
				g.drawString(gCost, (int) (cellSize - box.getWidth()) / 2,
						cellSize / 2 + g.getFontMetrics().getDescent());
			}
			g.translate(-cellX, -cellY);

			// parent
			if (showParent) {
				int parent = pf.getParent(cell);
				if (parent != -1) {
					int parentX = grid.col(parent) * getCellSize();
					int parentY = grid.row(parent) * getCellSize();
					int offset = getCellSize() / 2;
					g.setColor(Color.BLACK);
					g.setStroke(new BasicStroke(2));
					g.drawLine(cellX + offset, cellY + offset, parentX + offset, parentY + offset);
				}
			}
		}
	}

	private ConfigurableGridRenderer createMapRenderer(int cellSize) {
		ConfigurableGridRenderer r;
		if (style == RenderingStyle.BLOCKS) {
			r = new WallPassageGridRenderer(new BlockCellRenderer(cellSize));
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