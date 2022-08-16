package de.amr.graph.grid.traversals;

import java.util.Iterator;

import de.amr.graph.grid.api.GridGraph2D;

/**
 * A sequence of cells traversing the grid like "walls" which are growing horizontally from the sides towards the
 * center.
 * 
 * @author Armin Reichert
 */
public class CollapsingWalls implements Iterable<Integer> {

	private class CellIterator implements Iterator<Integer> {
		private int nextCellLeft;
		private int nextCellRight;
		private boolean leftsTurn;
		private int visitedCellCount;

		public CellIterator() {
			leftsTurn = true;
			nextCellLeft = grid.cell(0, 0);
			nextCellRight = grid.cell(grid.numCols() - 1, grid.numRows() - 1);
			visitedCellCount = 0;
		}

		@Override
		public boolean hasNext() {
			return visitedCellCount < grid.numVertices();
		}

		private int getNextCellLeft() {
			int cell = nextCellLeft;
			int x = grid.col(nextCellLeft);
			int y = grid.row(nextCellLeft);
			if (y < grid.numRows() - 1) {
				nextCellLeft = grid.cell(x, y + 1);
			} else {
				nextCellLeft = grid.cell(x + 1, 0);
			}
			leftsTurn = false;
			++visitedCellCount;
			return cell;
		}

		private int getNextCellRight() {
			int cell = nextCellRight;
			int x = grid.col(nextCellRight);
			int y = grid.row(nextCellRight);
			if (y > 0) {
				nextCellRight = grid.cell(x, y - 1);
			} else {
				nextCellRight = grid.cell(x - 1, grid.numRows() - 1);
			}
			leftsTurn = true;
			++visitedCellCount;
			return cell;
		}

		@Override
		public Integer next() {
			return leftsTurn ? getNextCellLeft() : getNextCellRight();
		}
	}

	@SuppressWarnings("rawtypes")
	private final GridGraph2D grid;

	public CollapsingWalls(GridGraph2D<?, ?> grid) {
		this.grid = grid;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new CellIterator();
	}
}