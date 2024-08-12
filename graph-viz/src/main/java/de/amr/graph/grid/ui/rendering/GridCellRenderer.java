package de.amr.graph.grid.ui.rendering;

import java.awt.Graphics2D;

import de.amr.graph.grid.api.GridGraph2D;

public interface GridCellRenderer {

	/**
	 * Draws a single grid "cell".
	 * 
	 * @param g
	 *               the graphics context
	 * @param grid
	 *               the grid graph
	 * @param cell
	 *               the cell
	 */
	void drawCell(Graphics2D g, GridGraph2D<?, ?> grid, int cell);

}
