package de.amr.graph.grid.impl;

import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.Topology;

/**
 * Factory for common types of grids.
 * 
 * @author Armin Reichert
 */
public interface GridFactory {

	static <V, E> GridGraph<V, E> fullGrid(int numCols, int numRows, Topology top) {
		GridGraph<V, E> g = new GridGraph<>(numCols, numRows, top, v -> null, (u, v) -> null,
				UndirectedEdge::new);
		g.fill();
		return g;
	}

	static <V, E> GridGraph<V, E> emptyGrid(int numCols, int numRows, Topology top) {
		GridGraph<V, E> g = new GridGraph<>(numCols, numRows, top, v -> null, (u, v) -> null,
				UndirectedEdge::new);
		return g;
	}
}