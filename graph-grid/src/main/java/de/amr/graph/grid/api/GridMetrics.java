package de.amr.graph.grid.api;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;
import static java.lang.Math.max;

/**
 * Mixin with common metric functions.
 * 
 * @author Armin Reichert
 */
public interface GridMetrics {

	/**
	 * The grid graph on which the metric functions operate.
	 * 
	 * @return grid graph
	 */
	GridGraph2D<?, ?> grid();

	/* private */
	default int dx(int u, int v) {
		return abs(grid().col(u) - grid().col(v));
	}

	/* private */
	default int dy(int u, int v) {
		return abs(grid().row(u) - grid().row(v));
	}

	/**
	 * Returns the Chebyshev distance (maximum metric) between the given grid cells.
	 * 
	 * @param u
	 *            grid cell
	 * @param v
	 *            grid cell
	 * 
	 * @return Chebyshev distance between cells
	 */
	default int chebyshev(int u, int v) {
		return max(dx(u, v), dy(u, v));
	}

	/**
	 * Returns the Manhattan distance (L1 norm) between the given grid cells.
	 * 
	 * @param u
	 *            grid cell
	 * @param v
	 *            grid cell
	 * 
	 * @return Manhattan distance between cells
	 */
	default int manhattan(int u, int v) {
		return dx(u, v) + dy(u, v);
	}

	/**
	 * Returns the Euclidean distance between the given grid cells.
	 * 
	 * @param u
	 *            grid cell
	 * @param v
	 *            grid cell
	 * 
	 * @return Euclidean distance between cells
	 */
	default double euclidean(int u, int v) {
		return hypot(dx(u, v), dy(u, v));
	}
}
