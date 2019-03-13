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

	protected final Deque<GridRenderer> renderStack = new ArrayDeque<>();
	protected GridGraph2D<?, ?> grid;
	protected BufferedImage buffer;
	private boolean readyForDrawing;

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(getDrawingBuffer(), 0, 0, null);
	}

	public GridCanvas(GridGraph2D<?, ?> grid) {
		if (grid == null) {
			throw new IllegalArgumentException("No grid specified");
		}
		this.grid = grid;
		readyForDrawing = false;
		setDoubleBuffered(false); // canvas implements double-buffering itself
	}

	public Graphics2D getDrawGraphics() {
		return getDrawingBuffer().createGraphics();
	}

	public BufferedImage getDrawingBuffer() {
		if (!readyForDrawing) {
			createDrawingBuffer();
		}
		return buffer;
	}

	private void createDrawingBuffer() {
		if (renderStack.isEmpty()) {
			throw new IllegalStateException("Cannot create drawing buffer, no renderer available");
		}
		int cellSize = renderStack.peek().getModel().getCellSize();
		Dimension dimension = new Dimension(grid.numCols() * cellSize, grid.numRows() * cellSize);
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);
		setSize(dimension);
		buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().createCompatibleImage(dimension.width, dimension.height);
		readyForDrawing = true;
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

	public void pushRenderer(GridRenderer newRenderer) {
		if (!renderStack.isEmpty()
				&& renderStack.peek().getModel().getCellSize() != newRenderer.getModel().getCellSize()) {
			readyForDrawing = false;
		}
		renderStack.push(newRenderer);
	}

	public GridRenderer popRenderer() {
		if (renderStack.size() <= 1) {
			throw new IllegalStateException("Cannot remove last renderer from stack");
		}
		GridRenderer oldRenderer = renderStack.pop();
		if (oldRenderer.getModel().getCellSize() != renderStack.peek().getModel().getCellSize()) {
			readyForDrawing = false;
		}
		return oldRenderer;
	}

	public GridGraph2D<?, ?> getGrid() {
		return grid;
	}

	public void setGrid(GridGraph<?, ?> grid) {
		if (grid == null) {
			throw new IllegalArgumentException("No grid specified");
		}
		GridGraph2D<?, ?> oldGrid = this.grid;
		if (oldGrid != null && (oldGrid.numCols() != grid.numCols() || oldGrid.numRows() != grid.numRows())) {
			readyForDrawing = false;
		}
		this.grid = grid;
	}
}