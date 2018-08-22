package de.amr.easy.grid.impl.iterators.traversals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.amr.easy.graph.api.UndirectedEdge;
import de.amr.easy.grid.api.CellSequence;
import de.amr.easy.grid.api.GridGraph2D;
import de.amr.easy.grid.api.GridPosition;
import de.amr.easy.grid.impl.GridGraph;
import de.amr.easy.grid.impl.Top4;
import de.amr.easy.grid.impl.iterators.shapes.Square;

/**
 * A sequence of cells starting at the center of the grid and expanding like a spiral until all grid
 * cells are traversed.
 * 
 * @author Armin Reichert
 */
public class Spiral implements CellSequence {

	private final List<Integer> cells = new ArrayList<>();

	public Spiral(GridGraph2D<?, ?> grid, Integer start) {
		int size = Math.max(grid.numCols(), grid.numRows());
		int offsetY = (size - grid.numRows()) / 2;
		GridGraph2D<?, ?> squareGrid = new GridGraph<>(size, size, new Top4(), null, (u, v) -> null, UndirectedEdge::new);
		int leftUpperCorner = squareGrid.cell(GridPosition.CENTER);
		for (int i = 0, n = size / 2 + 1; i < n; ++i) {
			Square square = new Square(squareGrid, leftUpperCorner, 2 * i + 1);
			for (int cell : square) {
				int x = squareGrid.col(cell);
				int y = squareGrid.row(cell) - offsetY;
				if (grid.isValidCol(x) && grid.isValidRow(y)) {
					cells.add(grid.cell(x, y));
				}
			}
			if (i < n - 1) {
				leftUpperCorner = squareGrid.cell(squareGrid.col(leftUpperCorner) - 1, squareGrid.row(leftUpperCorner) - 1);
			}
		}
	}

	@Override
	public Iterator<Integer> iterator() {
		return cells.iterator();
	}
}
