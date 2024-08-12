package de.amr.graph.grid.ui.rendering;

import java.awt.Color;
import java.awt.Font;

/**
 * Interface for classes providing grid rendering data.
 * 
 * @author Armin Reichert
 */
public interface GridRenderingModel {

	/**
	 * @return the width/height taken by a single cell
	 */
	int getCellSize();

	/**
	 * @return thickness of a passage / edge
	 */
	int getPassageWidth(int u, int v);

	/**
	 * @return the background color of a cell
	 */
	Color getGridBgColor();

	/**
	 * @param cell
	 *               a cell
	 * @param dir
	 *               a direction
	 * @return the color of the passage / edge towards the given direction
	 */
	Color getPassageColor(int cell, byte dir);

	/**
	 * @param cell
	 *               a cell
	 * @return the background color of the given cell
	 */
	Color getCellBgColor(int cell);

	/**
	 * @param cell
	 *               a cell
	 * @return the text for the given cell
	 */
	String getText(int cell);

	/**
	 * @return the minimum font size which should be displayed
	 */
	int getMinFontSize();

	/**
	 * @return the font for cell texts
	 */
	Font getTextFont(int cell);

	/**
	 * @param cell
	 *               a cell
	 * @return the color for cell texts
	 */
	Color getTextColor(int cell);
}