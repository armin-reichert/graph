package de.amr.graph.grid.api;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;
import static java.lang.Math.max;

import de.amr.graph.grid.impl.GridGraph;

/**
 * Mixin with common metric functions.
 * 
 * @author Armin Reichert
 */
public interface GridMetrics {

	private static int dx(GridGraph<?, ?> grid, int u, int v) {
		return abs(grid.col(u) - grid.col(v));
	}

	private static int dy(GridGraph<?, ?> grid, int u, int v) {
		return abs(grid.row(u) - grid.row(v));
	}

	/**
	 * Returns the Chebyshev distance (maximum metric) between the given grid cells.
	 * 
	 * @param u grid cell
	 * @param v grid cell
	 * 
	 * @return Chebyshev distance between cells
	 */
	static int chebyshev(GridGraph<?, ?> grid, int u, int v) {
		return max(dx(grid, u, v), dy(grid, u, v));
	}

	/**
	 * Returns the Manhattan distance (L1 norm) between the given grid cells.
	 * 
	 * @param u grid cell
	 * @param v grid cell
	 * 
	 * @return Manhattan distance between cells
	 */
	static int manhattan(GridGraph<?, ?> grid, int u, int v) {
		return dx(grid, u, v) + dy(grid, u, v);
	}

	/**
	 * Returns the Euclidean distance between the given grid cells.
	 * 
	 * @param u grid cell
	 * @param v grid cell
	 * 
	 * @return Euclidean distance between cells
	 */
	static double euclidean(GridGraph<?, ?> grid, int u, int v) {
		return hypot(dx(grid, u, v), dy(grid, u, v));
	}
}