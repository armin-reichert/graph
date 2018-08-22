package de.amr.easy.grid.impl.iterators.traversals;

import java.util.Iterator;

import de.amr.easy.grid.api.CellSequence;
import de.amr.easy.grid.api.GridGraph2D;

/**
 * A sequence of cells filling the grid by sweeping a vertical line from left to right.
 * 
 * @author Armin Reichert
 */
public class LeftToRightSweep implements CellSequence {

	private final GridGraph2D<?, ?> grid;

	public LeftToRightSweep(GridGraph2D<?, ?> grid) {
		this.grid = grid;
	}

	@Override
	public Iterator<Integer> iterator() {

		return new Iterator<Integer>() {

			private int x;
			private int y;

			@Override
			public boolean hasNext() {
				return !(x == grid.numCols() - 1 && y == grid.numRows() - 1);
			}

			@Override
			public Integer next() {
				if (y < grid.numRows() - 1) {
					++y;
				} else {
					++x;
					y = 0;
				}
				return grid.cell(x, y);
			}
		};
	}
}
