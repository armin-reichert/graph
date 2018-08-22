package de.amr.easy.grid.impl.iterators.traversals;

import java.util.Iterator;

import de.amr.easy.grid.api.CellSequence;
import de.amr.easy.grid.api.GridGraph2D;
import de.amr.easy.grid.impl.iterators.shapes.Square;

/**
 * A sequence of cells filling the grid like an expanding square.
 * 
 * @author Armin Reichert
 */
public class ExpandingSquare implements CellSequence {

	private final GridGraph2D<?, ?> grid;
	private final int topLeft;
	private final int minSize;
	private final int maxSize;

	public ExpandingSquare(GridGraph2D<?, ?> grid, Integer topLeft, int minSize, int maxSize) {
		this.grid = grid;
		this.topLeft = topLeft;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	@Override
	public Iterator<Integer> iterator() {

		return new Iterator<Integer>() {

			private Square currentSquare = new Square(grid, topLeft, minSize);
			private Iterator<Integer> currentIterator = currentSquare.iterator();

			@Override
			public boolean hasNext() {
				return currentSquare.getSize() < maxSize || currentIterator.hasNext();
			}

			@Override
			public Integer next() {
				if (!currentIterator.hasNext()) {
					currentSquare = new Square(grid, topLeft, currentSquare.getSize() + 1);
					currentIterator = currentSquare.iterator();
				}
				return currentIterator.next();
			}
		};
	}
}