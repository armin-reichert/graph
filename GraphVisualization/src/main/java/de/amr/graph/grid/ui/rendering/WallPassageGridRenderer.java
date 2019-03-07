package de.amr.graph.grid.ui.rendering;

import static java.lang.Math.ceil;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;

/**
 * Renders a grid as "passages" or "cells with walls" depending on the selected passage thickness.
 * 
 * @author Armin Reichert
 */
public class WallPassageGridRenderer extends ConfigurableGridRenderer {

	private class WallPassageCellRenderer implements GridCellRenderer {

		private void drawCellContent(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
			final int cellX = grid.col(cell) * getCellSize();
			final int cellY = grid.row(cell) * getCellSize();
			final int passageWidth = getPassageWidth(cell, cell); // TODO
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
			g.setFont(getTextFont().deriveFont(fontSize));
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			Rectangle textBox = g.getFontMetrics().getStringBounds(text, g).getBounds();
			g.drawString(text, (getCellSize() - textBox.width) / 2, (getCellSize() + textBox.height / 2) / 2);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}

		@Override
		public void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell) {
			grid.getTopology().dirs().filter(dir -> grid.isConnected(cell, dir))
					.forEach(dir -> drawHalfPassage(g, grid, cell, dir, getPassageColor(cell, dir)));
			drawCellContent(g, grid, cell);
		}
	}

	private GridCellRenderer cellRenderer;

	public WallPassageGridRenderer() {
		cellRenderer = new WallPassageCellRenderer();
	}

	public void setCellRenderer(GridCellRenderer cellRenderer) {
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
		final int dir = grid.direction(either, other).getAsInt();
		final int inv = grid.getTopology().inv(dir);
		drawHalfPassage(g, grid, either, dir, visible ? getPassageColor(either, dir) : getGridBgColor());
		drawHalfPassage(g, grid, other, inv, visible ? getPassageColor(other, inv) : getGridBgColor());
		cellRenderer.drawCell(g, grid, either);
		cellRenderer.drawCell(g, grid, other);
	}

	private void drawHalfPassage(Graphics2D g, GridGraph2D<?, ?> grid, int cell, int dir, Color passageColor) {
		final int cellX = grid.col(cell) * getCellSize();
		final int cellY = grid.row(cell) * getCellSize();
		final int centerX = cellX + getCellSize() / 2;
		final int centerY = cellY + getCellSize() / 2;
		final int passageWidth = getPassageWidth(cell, grid.neighbor(cell, dir).getAsInt());
		final int longside = (getCellSize() + passageWidth) / 2;
		final int shortside = passageWidth;
		g.setColor(passageColor);
		if (grid.getTopology() instanceof Top4) {
			switch (dir) {
			case Top4.E:
				g.translate(centerX - shortside / 2, centerY - shortside / 2);
				g.fillRect(0, 0, longside, shortside);
				g.translate(-centerX + shortside / 2, -centerY + shortside / 2);
				break;
			case Top4.S:
				g.translate(centerX - shortside / 2, centerY - shortside / 2);
				g.fillRect(0, 0, shortside, longside);
				g.translate(-centerX + shortside / 2, -centerY + shortside / 2);
				break;
			case Top4.W:
				g.translate(centerX - getCellSize() / 2, centerY - shortside / 2);
				g.fillRect(0, 0, longside, shortside);
				g.translate(-centerX + getCellSize() / 2, -centerY + shortside / 2);
				break;
			case Top4.N:
				g.translate(centerX - shortside / 2, centerY - getCellSize() / 2);
				g.fillRect(0, 0, shortside, longside);
				g.translate(-centerX + shortside / 2, -centerY + getCellSize() / 2);
				break;
			}
		} else if (grid.getTopology() instanceof Top8) {
			// TODO
		}
	}
}