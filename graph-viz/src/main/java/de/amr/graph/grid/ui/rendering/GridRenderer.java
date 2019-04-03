package de.amr.graph.grid.ui.rendering;

import java.awt.Graphics2D;

import de.amr.graph.grid.api.GridGraph2D;

/**
 * Common interface for grid renderer implementations.
 * 
 * @author Armin Reichert
 */
public interface GridRenderer {

	/**
	 * @return the rendering model providing the rendering data
	 */
	GridRenderingModel getModel();

	/**
	 * @return the used cell renderer
	 */
	GridCellRenderer getCellRenderer(int cell);

	/**
	 * Draws the complete grid.
	 * 
	 * @param g
	 *               the graphics context
	 * @param grid
	 *               the grid graph
	 */
	void drawGrid(Graphics2D g, GridGraph2D<?, ?> grid);

	/**
	 * Draws the "passage" between the given cells.
	 * 
	 * @param g
	 *                  the graphics context
	 * @param grid
	 *                  the grid graph
	 * @param either
	 *                  either edge vertex
	 * @param other
	 *                  other edge vertex
	 * @param visible
	 *                  if {@code true} the edge is drawn, otherwise is is hidden
	 */
	void drawPassage(Graphics2D g, GridGraph2D<?, ?> grid, int either, int other, boolean visible);

}