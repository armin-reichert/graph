package de.amr.graph.grid.ui.rendering;

import java.awt.Color;
import java.awt.Font;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Grid renderer that can be configured via function properties.
 * 
 * @author Armin Reichert
 */
public abstract class ConfigurableGridRenderer implements GridRenderer, GridRenderingModel {

	/** Function providing the grid cell size. */
	public IntSupplier fnCellSize;

	/** Function providing the passage width. */
	public BiFunction<Integer, Integer, Integer> fnPassageWidth;

	/** Function providing the grid background color. */
	public Supplier<Color> fnGridBgColor;

	/**
	 * Function {@code (cell, direction) -> color} providing the passage color toward a given direction.
	 */
	public BiFunction<Integer, Byte, Color> fnPassageColor;

	/** Function providing the background color for a cell. */
	public Function<Integer, Color> fnCellBgColor;

	/** Function providing the text for a cell. */
	public Function<Integer, String> fnText;

	/** Function providing the minimum font size still displayed. */
	public IntSupplier fnMinFontSize;

	/** Function supplying the text font. */
	public Function<Integer, Font> fnTextFont;

	/** Function supplying the text color for a cell. */
	public Function<Integer, Color> fnTextColor;

	/**
	 * Creates a renderer with default settings.
	 */
	public ConfigurableGridRenderer() {
		fnCellSize = () -> 8;
		fnPassageWidth = (u, v) -> getCellSize() / 2;
		fnGridBgColor = () -> Color.BLACK;
		fnPassageColor = (cell, dir) -> getCellBgColor(cell);
		fnCellBgColor = cell -> Color.WHITE;
		fnMinFontSize = () -> 5;
		fnText = cell -> "";
		fnTextFont = cell -> new Font("Sans", Font.PLAIN, getCellSize() / 2);
		fnTextColor = cell -> Color.BLUE;
	}

	@Override
	public GridRenderingModel getModel() {
		return this;
	}

	@Override
	public final int getCellSize() {
		return fnCellSize.getAsInt();
	}

	@Override
	public final int getPassageWidth(int u, int v) {
		return fnPassageWidth.apply(u, v);
	}

	@Override
	public final Color getGridBgColor() {
		return fnGridBgColor.get();
	}

	@Override
	public final Color getPassageColor(int cell, byte dir) {
		return fnPassageColor.apply(cell, dir);
	}

	@Override
	public final Color getCellBgColor(int cell) {
		return fnCellBgColor.apply(cell);
	}

	@Override
	public final String getText(int cell) {
		return fnText.apply(cell);
	}

	@Override
	public final int getMinFontSize() {
		return fnMinFontSize.getAsInt();
	}

	@Override
	public final Font getTextFont(int cell) {
		return fnTextFont.apply(cell);
	}

	@Override
	public final Color getTextColor(int cell) {
		return fnTextColor.apply(cell);
	}
}