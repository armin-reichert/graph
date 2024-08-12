package de.amr.graph.grid.ui.rendering;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.function.DoubleSupplier;

import de.amr.graph.grid.api.GridGraph2D;

public class PearlsGridRenderer extends ConfigurableGridRenderer {

	private GridCellRenderer cellRenderer;

	public DoubleSupplier fnRelativePearlSize = () -> .66;

	private class DefaultCellRenderer implements GridCellRenderer {

		@Override
		public void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
			int cellSize = getCellSize();
			int x = grid.col(cell) * cellSize;
			int y = grid.row(cell) * cellSize;
			int pearlSize = getPearlSize();
			int offset = (cellSize - pearlSize) / 2;
			int arc = pearlSize / 2;
			g.translate(x + offset, y + offset);
			g.setColor(getCellBgColor(cell));
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.fillRoundRect(0, 0, pearlSize, pearlSize, arc, arc);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g.translate(-x - offset, -y - offset);
			drawCellContent(g, grid, cell);
		}
	}

	public PearlsGridRenderer() {
		cellRenderer = new DefaultCellRenderer();
	}

	public PearlsGridRenderer(GridCellRenderer cellRenderer) {
		this.cellRenderer = cellRenderer;
	}

	public int getPearlSize() {
		return Math.max(1, (int) Math.round(getCellSize() * fnRelativePearlSize.getAsDouble()));
	}

	@Override
	public GridCellRenderer getCellRenderer(int cell) {
		return cellRenderer;
	}

	@Override
	public void drawGrid(Graphics2D g, GridGraph2D<?, ?> grid) {
		grid.edges().forEach(edge -> drawPassage(g, grid, edge.either(), edge.other(), true));
		grid.vertices().forEach(cell -> getCellRenderer(cell).drawCell(g, grid, cell));
	}

	@Override
	public void drawPassage(Graphics2D g, GridGraph2D<?, ?> grid, int either, int other, boolean visible) {
		int cellSize = getCellSize();
		int pearlSize = getPearlSize();
		int x1 = grid.col(either) * cellSize + pearlSize / 2;
		int y1 = grid.row(either) * cellSize + pearlSize / 2;
		int x2 = grid.col(other) * cellSize + pearlSize / 2;
		int y2 = grid.row(other) * cellSize + pearlSize / 2;
		int offset = (cellSize - pearlSize) / 2;
		g.setColor(getPassageColor(either, grid.direction(either, other).get()));
		g.translate(offset, offset);
		g.setStroke(new BasicStroke(getPassageWidth(either, other)));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawLine(x1, y1, x2, y2);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g.translate(-offset, -offset);
	}

	protected void drawCellContent(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		final int cellX = grid.col(cell) * getCellSize();
		final int cellY = grid.row(cell) * getCellSize();
		final int passageWidth = getPassageWidth(cell, cell);// TODO
		final int offset = getCellSize() / 2 - passageWidth / 2;
		g.translate(cellX, cellY);
		g.setColor(getCellBgColor(cell));
		g.fillRect(offset, offset, passageWidth, passageWidth);
		drawCellText(g, grid, cell);
		g.translate(-cellX, -cellY);
	}

	protected void drawCellText(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		Font font = getTextFont(cell);
		if (font.getSize() < getMinFontSize()) {
			return;
		}
		String text = getText(cell);
		text = (text == null) ? "" : text.trim();
		if (text.length() == 0) {
			return;
		}
		g.setColor(getTextColor(cell));
		g.setFont(font);
		Rectangle textBox = g.getFontMetrics().getStringBounds(text, g).getBounds();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.drawString(text, (getCellSize() - textBox.width) / 2, (getCellSize() + textBox.height / 2) / 2);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
}