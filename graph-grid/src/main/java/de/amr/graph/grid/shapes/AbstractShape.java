package de.amr.graph.grid.shapes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.amr.graph.grid.api.GridGraph2D;

/**
 * Base class for shapes (square, rectangle, ...) on a grid.
 * <p>
 * Implements the {@link CellSequence} interface such that a shape can be used as an Iterator or a
 * Stream of cells.
 * 
 * @author Armin Reichert
 *
 */
public abstract class AbstractShape implements Iterable<Integer> {

	public final GridGraph2D<?, ?> grid;

	protected final List<Integer> cells = new ArrayList<>();

	protected AbstractShape(GridGraph2D<?, ?> grid) {
		this.grid = grid;
	}

	protected void addCell(int col, int row) {
		if (grid.isValidCol(col) && grid.isValidRow(row)) {
			cells.add(grid.cell(col, row));
		}
	}

	@Override
	public Iterator<Integer> iterator() {
		return cells.iterator();
	}
}