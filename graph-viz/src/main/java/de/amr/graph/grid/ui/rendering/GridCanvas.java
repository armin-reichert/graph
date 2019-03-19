package de.amr.graph.grid.ui.rendering;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import javax.swing.JComponent;

import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.GridGraph;

/**
 * A Swing component for displaying a grid. Maintains a stack of grid renderers.
 * 
 * @author Armin Reichert
 */
public class GridCanvas extends JComponent {

	private Deque<GridRenderer> rendererStack = new ArrayDeque<>();

	protected GridGraph2D<?, ?> grid;

	private BufferedImage buffer;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.drawImage(getDrawingBuffer(), 0, 0, null);
		g2.dispose();
	}

	public GridCanvas() {
		setDoubleBuffered(false);
	}

	public GridCanvas(GridGraph2D<?, ?> grid) {
		if (grid == null) {
			throw new IllegalArgumentException("No grid specified");
		}
		this.grid = grid;
		setDoubleBuffered(false);
	}

	public Graphics2D getDrawGraphics() {
		return getDrawingBuffer().createGraphics();
	}

	public BufferedImage getDrawingBuffer() {
		if (buffer != null) {
			return buffer;
		}
		if (grid != null) {
			int cellSize = getCurrentCellSizeOrDefault(2);
			createDrawingBuffer(grid.numCols() * cellSize, grid.numRows() * cellSize);
		}
		else {
			createDrawingBuffer(getWidth(), getHeight());
		}
		return buffer;
	}

	private void createDrawingBuffer(int width, int height) {
		buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().createCompatibleImage(width, height);
		Dimension size = new Dimension(width, height);
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
		setSize(size);
	}

	private int getCurrentCellSizeOrDefault(int defaultSize) {
		return rendererStack.isEmpty() ? defaultSize : rendererStack.peek().getModel().getCellSize();
	}

	public void clear() {
		getRenderer().ifPresent(r -> fill(r.getModel().getGridBgColor()));
	}

	public void fill(Color bgColor) {
		Graphics2D g = getDrawGraphics();
		g.setColor(bgColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		repaint();
	}

	public void drawGridCell(int cell) {
		getRenderer().ifPresent(r -> {
			Graphics2D g = getDrawGraphics();
			r.getCellRenderer(cell).drawCell(g, grid, cell);
			repaint();
		});
	}

	public void drawGridPassage(int either, int other, boolean visible) {
		getRenderer().ifPresent(r -> {
			Graphics2D g = getDrawGraphics();
			r.drawPassage(g, grid, either, other, visible);
			repaint();
		});
	}

	public void drawGrid() {
		getRenderer().ifPresent(r -> {
			Graphics2D g = getDrawGraphics();
			r.drawGrid(g, grid);
			repaint();
		});
	}

	public Optional<GridRenderer> getRenderer() {
		return Optional.ofNullable(rendererStack.peek());
	}

	public boolean hasRenderer() {
		return rendererStack.size() > 0;
	}

	public void pushRenderer(GridRenderer renderer) {
		if (!rendererStack.isEmpty()
				&& rendererStack.peek().getModel().getCellSize() != renderer.getModel().getCellSize()) {
			buffer = null;
		}
		rendererStack.push(renderer);
	}

	public GridRenderer popRenderer() {
		GridRenderer renderer = rendererStack.pop();
		if (rendererStack.isEmpty()
				|| renderer.getModel().getCellSize() != rendererStack.peek().getModel().getCellSize()) {
			buffer = null;
		}
		return renderer;
	}

	public GridGraph2D<?, ?> getGrid() {
		return grid;
	}

	public void setGrid(GridGraph<?, ?> grid) {
		if (grid == null) {
			throw new IllegalArgumentException("Grid must not be NULL");
		}
		GridGraph2D<?, ?> oldGrid = this.grid;
		if (oldGrid != null && (oldGrid.numCols() != grid.numCols() || oldGrid.numRows() != grid.numRows())) {
			buffer = null;
		}
		this.grid = grid;
	}
}