package de.amr.graph.grid.ui.rendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import javax.swing.JComponent;

import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top4;

/**
 * A Swing component for displaying a grid. Maintains a stack of grid renderers.
 * 
 * @author Armin Reichert
 */
public class GridCanvas extends JComponent {

	private BufferedImage buffer;
	private Deque<GridRenderer> rendererStack = new ArrayDeque<>();
	private GridGraph2D<?, ?> grid;
	private int cellSize;
	private WallPassageGridRenderer defaultRenderer;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(buffer, 0, 0, null);
	}

	public GridCanvas(GridGraph2D<?, ?> grid) {
		this.grid = Objects.requireNonNull(grid);
		cellSize = 2;
		setDoubleBuffered(false);
		setOpaque(true);
		setBackground(Color.BLACK);
		createBuffer(cellSize * grid.numCols(), cellSize * grid.numRows());
		createDefaultRenderer();
		// performance issue
		if (grid.numVertices() < 1000) {
			defaultRenderer.drawGrid(getDrawGraphics(), grid);
		}
	}

	public GridCanvas() {
		this(new GridGraph<>(5, 5, Top4.get(), v -> null, (u, v) -> null, UndirectedEdge::new));
	}

	private void createDefaultRenderer() {
		defaultRenderer = new WallPassageGridRenderer();
		defaultRenderer.fnCellSize = () -> cellSize;
		defaultRenderer.fnPassageWidth = (u, v) -> cellSize - 1;
		defaultRenderer.fnText = cell -> String.valueOf(cell);
		rendererStack.push(defaultRenderer);
	}

	private void createBuffer(int width, int height) {
		if (width == 0) {
			throw new GridCanvasException("Buffer width must be greater than 0");
		}
		if (height == 0) {
			throw new GridCanvasException("Buffer height must be greater than 0");
		}
		buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().createCompatibleImage(width, height);
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setSize(size);
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int newCellSize) {
		setCellSize(cellSize, true);
	}

	public void setCellSize(int newCellSize, boolean redrawGrid) {
		if (newCellSize < 2) {
			throw new GridCanvasException("Cell size must be at least 2");
		}
		int oldCellSize = cellSize;
		if (newCellSize != oldCellSize) {
			this.cellSize = newCellSize;
			createBuffer(newCellSize * grid.numCols(), newCellSize * grid.numRows());
			if (redrawGrid) {
				clear();
				drawGrid();
			}
			firePropertyChange("cellSize", oldCellSize, newCellSize);
		}
	}

	public GridGraph2D<?, ?> getGrid() {
		return grid;
	}

	public void setGrid(GridGraph<?, ?> newGrid) {
		setGrid(newGrid, true);
	}

	public void setGrid(GridGraph<?, ?> newGrid, boolean redrawGrid) {
		if (newGrid == null) {
			throw new GridCanvasException("Grid must not be NULL");
		}
		GridGraph2D<?, ?> oldGrid = grid;
		if (oldGrid != newGrid) {
			this.grid = newGrid;
			if (oldGrid.numCols() != newGrid.numCols() || oldGrid.numRows() != newGrid.numRows()) {
				createBuffer(cellSize * newGrid.numCols(), cellSize * newGrid.numRows());
			}
			if (redrawGrid) {
				clear();
				drawGrid();
			}
			firePropertyChange("grid", oldGrid, newGrid);
		}
	}

	public Graphics2D getDrawGraphics() {
		return buffer.createGraphics();
	}

	public BufferedImage getDrawingBuffer() {
		return buffer;
	}

	public GridRenderer getRenderer() {
		return rendererStack.peek();
	}

	public void pushRenderer(GridRenderer renderer) {
		if (renderer == null) {
			throw new GridCanvasException("Renderer is NULL");
		}
		if (renderer.getModel() == null) {
			throw new GridCanvasException("Renderer has no model");
		}
		rendererStack.push(renderer);
	}

	public GridRenderer popRenderer() {
		if (rendererStack.isEmpty()) {
			throw new IllegalStateException("Renderer stack is empty");
		}
		GridRenderer r = rendererStack.pop();
		if (rendererStack.isEmpty()) {
			rendererStack.push(defaultRenderer);
		}
		return r;
	}

	public void replaceRenderer(GridRenderer r) {
		rendererStack.pop();
		rendererStack.push(r);
	}

	public void clear() {
		fill(getRenderer().getModel().getGridBgColor());
	}

	public void fill(Color fillColor) {
		Graphics2D g = getDrawGraphics();
		g.setColor(fillColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		repaint();
	}

	public void drawGridCell(int cell) {
		Graphics2D g = getDrawGraphics();
		GridCellRenderer cr = getRenderer().getCellRenderer(cell);
		if (cr == null) {
			throw new IllegalStateException("No cell renderer");
		}
		cr.drawCell(g, grid, cell);
		repaint();
	}

	public void drawGridPassage(int either, int other, boolean visible) {
		getRenderer().drawPassage(getDrawGraphics(), grid, either, other, visible);
		repaint();
	}

	public void drawGrid() {
		getRenderer().drawGrid(getDrawGraphics(), grid);
		repaint();
	}
}