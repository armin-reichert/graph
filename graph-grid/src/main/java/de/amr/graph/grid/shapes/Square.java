package de.amr.graph.grid.shapes;

import static de.amr.graph.grid.impl.Grid4Topology.E;
import static de.amr.graph.grid.impl.Grid4Topology.N;
import static de.amr.graph.grid.impl.Grid4Topology.S;
import static de.amr.graph.grid.impl.Grid4Topology.W;

import java.util.Arrays;

import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.Grid4Topology;

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
				x += Grid4Topology.get().dx(dir);
				y += Grid4Topology.get().dy(dir);
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