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
 * A Swing component for displaying a grid.
 * 
 * @author Armin Reichert
 */
public class GridCanvas extends JComponent {

	protected Deque<GridRenderer> renderStack = new ArrayDeque<>();

	protected GridGraph2D<?, ?> grid;

	protected BufferedImage buffer;

	private boolean bufferInvalid = false;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(getDrawingBuffer(), 0, 0, null);
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
		if (!bufferInvalid) {
			if (grid != null) {
				int cellSize = getCellSizeOrDefault(2);
				createDrawingBuffer(grid.numCols() * cellSize, grid.numRows() * cellSize);
			} else {
				createDrawingBuffer(getWidth(), getHeight());
			}
		}
		return buffer;
	}

	private int getCellSizeOrDefault(int defaultSize) {
		return renderStack.isEmpty() ? defaultSize : renderStack.peek().getModel().getCellSize();
	}

	private void createDrawingBuffer(int width, int height) {
		buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().createCompatibleImage(width, height);
		Dimension size = new Dimension(width, height);
		setMinimumSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
		setSize(size);
		bufferInvalid = true;
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
		return Optional.ofNullable(renderStack.peek());
	}

	public void pushRenderer(GridRenderer renderer) {
		if (!renderStack.isEmpty()
				&& renderStack.peek().getModel().getCellSize() != renderer.getModel().getCellSize()) {
			bufferInvalid = false;
		}
		renderStack.push(renderer);
	}

	public GridRenderer popRenderer() {
		if (renderStack.size() <= 1) {
			throw new IllegalStateException("Render stack need at least one element");
		}
		GridRenderer oldRenderer = renderStack.pop();
		if (oldRenderer.getModel().getCellSize() != renderStack.peek().getModel().getCellSize()) {
			bufferInvalid = false;
		}
		return oldRenderer;
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
			bufferInvalid = false;
		}
		this.grid = grid;
	}
}