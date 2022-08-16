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

	private final GridGraph2D<?, ?> grid;

	public CollapsingWalls(GridGraph2D<?, ?> grid) {
		this.grid = grid;
	}

	@Override
	public Iterator<Integer> iterator() {

		return new Iterator<Integer>() {

			private int nextLeft;
			private int nextRight;
			private boolean left;
			private int visited;

			{
				left = true;
				nextLeft = grid.cell(0, 0);
				nextRight = grid.cell(grid.numCols() - 1, grid.numRows() - 1);
				visited = 0;
			}

			@Override
			public boolean hasNext() {
				return visited < grid.numVertices();
			}

			@Override
			public Integer next() {
				if (left) {
					int cell = nextLeft;
					int x = grid.col(nextLeft);
					int y = grid.row(nextLeft);
					if (y < grid.numRows() - 1) {
						nextLeft = grid.cell(x, y + 1);
					} else {
						nextLeft = grid.cell(x + 1, 0);
					}
					left = false;
					++visited;
					return cell;
				} else {
					int cell = nextRight;
					int x = grid.col(nextRight);
					int y = grid.row(nextRight);
					if (y > 0) {
						nextRight = grid.cell(x, y - 1);
					} else {
						nextRight = grid.cell(x - 1, grid.numRows() - 1);
					}
					left = true;
					++visited;
					return cell;
				}
			}
		};
	}
}