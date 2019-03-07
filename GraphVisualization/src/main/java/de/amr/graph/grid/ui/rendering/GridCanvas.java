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

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.GridGraph;

/**
 * A Swing component for displaying a grid.
 * 
 * @author Armin Reichert
 */
public class GridCanvas extends JComponent {

	protected final Deque<GridRenderer> rendererStack = new ArrayDeque<>();
	protected GridGraph2D<?, ?> grid;
	protected BufferedImage buffer;

	public GridCanvas(GridGraph2D<?, ?> grid, int cellSize) {
		if (grid == null) {
			throw new IllegalArgumentException("No grid specified");
		}
		this.grid = grid;
		setDoubleBuffered(false);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		resizeTo(cellSize);
	}

	public GridGraph2D<?, ?> getGrid() {
		return grid;
	}

	public void setGrid(GridGraph<?, ?> grid) {
		if (grid == null) {
			throw new IllegalArgumentException("No grid specified");
		}
		this.grid = grid;
	}

	public BufferedImage getDrawingBuffer() {
		return buffer;
	}

	public Graphics2D getDrawGraphics() {
		return buffer.createGraphics();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(buffer, 0, 0, null);
	}

	public void clear() {
		getRenderer().ifPresent(r -> fill(r.getModel().getGridBgColor()));
		repaint();
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

	protected void resizeTo(int cellSize) {
		int width = grid.numCols() * cellSize, height = grid.numRows() * cellSize;
		buffer = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration().createCompatibleImage(width, height);
		Dimension dimension = new Dimension(width, height);
		setMinimumSize(dimension);
		setMaximumSize(dimension);
		setPreferredSize(dimension);
		setSize(dimension);
	}

	public void setCellSize(int cellSize) {
		resizeTo(cellSize);
		repaint();
	}

	public Optional<GridRenderer> getRenderer() {
		return Optional.ofNullable(rendererStack.peek());
	}

	public void pushRenderer(GridRenderer newRenderer) {
		getRenderer().ifPresent(oldRenderer -> {
			if (oldRenderer.getModel().getCellSize() != newRenderer.getModel().getCellSize()) {
				resizeTo(newRenderer.getModel().getCellSize());
			}
			repaint();
		});
		rendererStack.push(newRenderer);
	}

	public GridRenderer popRenderer() {
		if (rendererStack.isEmpty()) {
			throw new IllegalStateException("Cannot remove last renderer");
		}
		GridRenderer oldRenderer = rendererStack.pop();
		getRenderer().ifPresent(newRenderer -> {
			if (oldRenderer.getModel().getCellSize() != newRenderer.getModel().getCellSize()) {
				resizeTo(newRenderer.getModel().getCellSize());
			}
			repaint();
		});
		return oldRenderer;
	}
}