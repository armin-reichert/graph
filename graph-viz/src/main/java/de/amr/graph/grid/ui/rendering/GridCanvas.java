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
import de.amr.graph.grid.impl.Grid4Topology;
import de.amr.graph.grid.impl.GridGraph;

/**
 * A Swing component for displaying a grid. Maintains a stack of grid renderers.
 * 
 * @author Armin Reichert
 */
public class GridCanvas extends JComponent {

	private static final GridGraph2D<?, ?> DEFAULT_GRID = new GridGraph<>(5, 5, Grid4Topology.get(), v -> null,
			(u, v) -> null, UndirectedEdge::new);

	// data
	private GridGraph2D<?, ?> grid;
	private int cellSize;

	// rendering
	private boolean centered;
	private BufferedImage buffer;
	private WallPassageGridRenderer defaultRenderer;
	private final Deque<GridRenderer> rendererStack = new ArrayDeque<>();

	/**
	 * Constructs a default grid canvas.
	 */
	public GridCanvas() {
		this(DEFAULT_GRID, 32);
	}

	/**
	 * Constructs a grid canvas rendering the given grid at the given cell size.
	 * 
	 * @param grid
	 *                   grid to be rendered
	 * @param cellSize
	 *                   grid cell size in pixel
	 */
	public GridCanvas(GridGraph2D<?, ?> grid, int cellSize) {
		this.grid = Objects.requireNonNull(grid);
		if (cellSize <= 0) {
			throw new IllegalArgumentException("Grid cell size must be positive");
		}
		this.cellSize = cellSize;
		setDoubleBuffered(false); // TODO is this useful?
		setOpaque(true);
		setBackground(Color.BLACK);
		createBuffer(cellSize * grid.numCols(), cellSize * grid.numRows());
		createDefaultRenderer();
		// avoid initial rendering for large grids
		if (grid.numVertices() < 1000) {
			defaultRenderer.drawGrid(getDrawGraphics(), grid);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		int gridWidth = grid.numCols() * cellSize;
		int gridHeight = grid.numRows() * cellSize;
		int dx = (getWidth() - gridWidth) / 2;
		int dy = (getHeight() - gridHeight) / 2;
		if (centered) {
			g.translate(dx, dy);
		}
		g.drawImage(buffer, 0, 0, null);
		// TODO fixme
		g.setColor(getRenderer().getModel().getGridBgColor());
		g.drawRect(0, 0, gridWidth, gridWidth);
		if (centered) {
			g.translate(-dx, -dy);
		}
	}

	private void createDefaultRenderer() {
		defaultRenderer = new WallPassageGridRenderer();
		defaultRenderer.fnCellSize = () -> getCellSize();
		defaultRenderer.fnPassageWidth = (u, v) -> getCellSize() - 1;
		defaultRenderer.fnText = cell -> String.valueOf(cell);
		defaultRenderer.fnTextColor = cell -> Color.BLACK;
		rendererStack.push(defaultRenderer);
	}

	private void createBuffer(int width, int height) {
		if (width == 0) {
			throw new GridCanvasException("Buffer width must be greater than 0");
		}
		if (height == 0) {
			throw new GridCanvasException("Buffer height must be greater than 0");
		}
		// System.out.println("Creating new drawing buffer, width=" + width + ", height=" + height);
		buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().createCompatibleImage(width, height);
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setSize(size);
	}

	public int getCellSize() {
		return cellSize;
	}

	public boolean isCentered() {
		return centered;
	}

	public void setCentered(boolean centered) {
		this.centered = centered;
	}

	public void setCellSize(int newCellSize) {
		setCellSize(newCellSize, true);
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
		}
		firePropertyChange("cellSize", oldCellSize, newCellSize);
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
		}
		firePropertyChange("grid", oldGrid, newGrid);
	}

	public void resize(GridGraph<?, ?> newGrid, int newCellSize) {
		if (newGrid == null) {
			throw new GridCanvasException("Grid must not be NULL");
		}
		if (newCellSize < 2) {
			throw new GridCanvasException("Cell size must be at least 2");
		}
		GridGraph2D<?, ?> oldGrid = grid;
		int oldCellSize = cellSize;
		this.grid = newGrid;
		this.cellSize = newCellSize;
		createBuffer(cellSize * grid.numCols(), cellSize * grid.numRows());
		firePropertyChange("grid", oldGrid, newGrid);
		firePropertyChange("cellSize", oldCellSize, newCellSize);
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
		GridRenderer gr = getRenderer();
		gr.drawGrid(getDrawGraphics(), grid);
		repaint();
	}
}