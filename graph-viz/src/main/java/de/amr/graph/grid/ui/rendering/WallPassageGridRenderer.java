package de.amr.graph.grid.ui.rendering;

import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.Grid4Topology;
import org.tinylog.Logger;

import java.awt.*;

/**
 * Renders a grid as "passages" or "cells with walls" depending on the selected passage thickness.
 * 
 * @author Armin Reichert
 */
public class WallPassageGridRenderer extends ConfigurableGridRenderer {

	private class DefaultCellRenderer implements GridCellRenderer {

		@Override
		public void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
			grid.getTopology().dirs().filter(dir -> grid.isConnected(cell, dir))
					.forEach(dir -> drawHalfPassage(g, grid, cell, dir, getPassageColor(cell, dir)));
			drawCellContent(g, grid, cell);
		}

		private void drawCellContent(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
			final int cellX = grid.col(cell) * getCellSize();
			final int cellY = grid.row(cell) * getCellSize();
			final int passageWidth = getPassageWidth(cell, cell); // TODO
			final int offset = getCellSize() / 2 - passageWidth / 2;
			g.translate(cellX, cellY);
			g.setColor(getCellBgColor(cell));
			g.fillRect(offset, offset, passageWidth, passageWidth);
			drawCellText(g, grid, cell);
			g.translate(-cellX, -cellY);
		}

		private void drawCellText(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
			Font font = getTextFont(cell);
			if (font.getSize() < getMinFontSize()) {
				return;
			}
			String text = getText(cell);
			text = (text == null) ? "" : text.trim();
			if (text.length() == 0) {
				return;
			}
			Logger.trace("Cell %d has text %s".formatted(cell, text));
			g.setColor(getTextColor(cell));
			g.setFont(font);
			Rectangle textBox = g.getFontMetrics().getStringBounds(text, g).getBounds();
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.drawString(text, (getCellSize() - textBox.width) / 2, (getCellSize() + textBox.height / 2) / 2);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
	}

	private GridCellRenderer cellRenderer;

	public WallPassageGridRenderer() {
		cellRenderer = new DefaultCellRenderer();
	}

	public WallPassageGridRenderer(GridCellRenderer cellRenderer) {
		this.cellRenderer = cellRenderer;
	}

	@Override
	public GridCellRenderer getCellRenderer(int cell) {
		return cellRenderer;
	}

	@Override
	public void drawGrid(Graphics2D g, GridGraph2D<?, ?> grid) {
		grid.edges().forEach(passage -> drawPassage(g, grid, passage.either(), passage.other(), true));
		grid.vertices().filter(cell -> grid.degree(cell) == 0)
				.forEach(cell -> getCellRenderer(cell).drawCell(g, grid, cell));
	}

	@Override
	public void drawPassage(Graphics2D g, GridGraph2D<?, ?> grid, int either, int other, boolean visible) {
		final byte dir = grid.direction(either, other).get();
		final byte inv = grid.getTopology().inv(dir);
		drawHalfPassage(g, grid, either, dir, visible ? getPassageColor(either, dir) : getGridBgColor());
		drawHalfPassage(g, grid, other, inv, visible ? getPassageColor(other, inv) : getGridBgColor());
		getCellRenderer(either).drawCell(g, grid, either);
		getCellRenderer(other).drawCell(g, grid, other);
	}

	private void drawHalfPassage(Graphics2D g, GridGraph2D<?, ?> grid, int cell, byte dir, Color passageColor) {
		final int cellX = grid.col(cell) * getCellSize();
		final int cellY = grid.row(cell) * getCellSize();
		final int centerX = cellX + getCellSize() / 2;
		final int centerY = cellY + getCellSize() / 2;
		final int passageWidth = getPassageWidth(cell, grid.neighbor(cell, dir).get());
		final int longside = (getCellSize() + passageWidth) / 2;
		final int shortside = passageWidth;
		g.setColor(passageColor);
		switch (dir) {
		case Grid4Topology.E:
			g.translate(centerX - shortside / 2, centerY - shortside / 2);
			g.fillRect(0, 0, longside, shortside);
			g.translate(-centerX + shortside / 2, -centerY + shortside / 2);
			break;
		case Grid4Topology.S:
			g.translate(centerX - shortside / 2, centerY - shortside / 2);
			g.fillRect(0, 0, shortside, longside);
			g.translate(-centerX + shortside / 2, -centerY + shortside / 2);
			break;
		case Grid4Topology.W:
			g.translate(centerX - getCellSize() / 2, centerY - shortside / 2);
			g.fillRect(0, 0, longside, shortside);
			g.translate(-centerX + getCellSize() / 2, -centerY + shortside / 2);
			break;
		case Grid4Topology.N:
			g.translate(centerX - shortside / 2, centerY - getCellSize() / 2);
			g.fillRect(0, 0, shortside, longside);
			g.translate(-centerX + shortside / 2, -centerY + getCellSize() / 2);
			break;
		default:
			break;
		}
	}
}