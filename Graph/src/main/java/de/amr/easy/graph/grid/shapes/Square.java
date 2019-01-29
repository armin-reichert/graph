package de.amr.easy.graph.grid.shapes;

import static de.amr.easy.graph.grid.impl.Top4.E;
import static de.amr.easy.graph.grid.impl.Top4.N;
import static de.amr.easy.graph.grid.impl.Top4.S;
import static de.amr.easy.graph.grid.impl.Top4.W;

import java.util.Arrays;

import de.amr.easy.graph.grid.api.GridGraph2D;
import de.amr.easy.graph.grid.impl.Top4;

/**
 * Iterates grid cells clockwise as a square with given top left corner and size.
 * 
 * @author Armin Reichert
 */
public class Square extends AbstractShape {

	private final int topLeft;
	private final int size;

	public Square(GridGraph2D<?, ?> grid, int topLeft, int size) {
		super(grid);
		this.topLeft = topLeft;
		this.size = size;
		if (size == 0) {
			return;
		}
		int x = grid.col(topLeft), y = grid.row(topLeft);
		if (size == 1) {
			addCell(x, y);
			return;
		}
		for (int dir : Arrays.asList(E, S, W, N)) {
			for (int i = 0; i < size - 1; ++i) {
				addCell(x, y);
				x += Top4.get().dx(dir);
				y += Top4.get().dy(dir);
			}
		}
	}

	public int getTopLeft() {
		return topLeft;
	}

	public int getSize() {
		return size;
	}
}