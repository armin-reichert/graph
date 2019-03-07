package de.amr.graph.grid.ui.rendering;

import static java.lang.Math.ceil;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import de.amr.graph.grid.api.GridGraph2D;

public class PearlsGridRenderer extends ConfigurableGridRenderer {

	private class DefaultGridCellRenderer implements GridCellRenderer {

		@Override
		public void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
			int cs = getCellSize();
			int x = grid.col(cell) * cs;
			int y = grid.row(cell) * cs;
			int ps = Math.max(1, getCellSize() / 2); // TODO
			int offset = cs / 4;
			int arc = ps / 2;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.translate(x + offset, y + offset);
			g.setColor(getCellBgColor(cell));
			// g.fillOval(0, 0, ps, ps);
			g.fillRoundRect(0, 0, ps, ps, arc, arc);
			g.translate(-x - offset, -y - offset);
			drawCellContent(g, grid, cell);
		}
	}

	private GridCellRenderer cellRenderer;

	public PearlsGridRenderer() {
		cellRenderer = new DefaultGridCellRenderer();
	}

	public PearlsGridRenderer(GridCellRenderer cellRenderer) {
		this.cellRenderer = cellRenderer;
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
		int cs = getCellSize();
		int ps = Math.max(1, getCellSize() / 2); // TODO
		int x1 = grid.col(either) * cs + ps / 2;
		int y1 = grid.row(either) * cs + ps / 2;
		int x2 = grid.col(other) * cs + ps / 2;
		int y2 = grid.row(other) * cs + ps / 2;
		int offset = cs / 4;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(getPassageColor(either, grid.direction(either, other).getAsInt()));
		g.translate(offset, offset);
		g.drawLine(x1, y1, x2, y2);
		g.translate(-offset, -offset);
	}

	private void drawCellContent(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		final int cellX = grid.col(cell) * getCellSize();
		final int cellY = grid.row(cell) * getCellSize();
		final int passageWidth = getPassageWidth(cell, cell);// TODO
		final int offset = (int) ceil((getCellSize() / 2 - passageWidth / 2));
		g.translate(cellX, cellY);
		g.setColor(getCellBgColor(cell));
		g.fillRect(offset, offset, passageWidth, passageWidth);
		drawCellText(g, grid, cell);
		g.translate(-cellX, -cellY);
	}

	private void drawCellText(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
		int fontSize = getTextFont().getSize();
		if (fontSize < getMinFontSize()) {
			return;
		}
		String text = getText(cell);
		text = (text == null) ? "" : text.trim();
		if (text.length() == 0) {
			return;
		}
		g.setColor(getTextColor(cell));
		g.setFont(getTextFont().deriveFont(Font.PLAIN, fontSize));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		Rectangle textBox = g.getFontMetrics().getStringBounds(text, g).getBounds();
		g.drawString(text, (getCellSize() - textBox.width) / 2, (getCellSize() + textBox.height / 2) / 2);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
}