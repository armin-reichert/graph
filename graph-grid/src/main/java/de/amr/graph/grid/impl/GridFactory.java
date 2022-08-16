package de.amr.graph.grid.impl;

import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.GridTopology;

/**
 * Factory for common types of grids.
 * 
 * @author Armin Reichert
 */
public interface GridFactory {

	static <V, E> GridGraph<V, E> fullGrid(int numCols, int numRows, GridTopology top, V defaultVertexLabel,
			E defaultEdgeLabel) {
		GridGraph<V, E> g = new GridGraph<>(numCols, numRows, top, v -> defaultVertexLabel, (u, v) -> defaultEdgeLabel,
				UndirectedEdge::new);
		g.fill();
		return g;
	}

	static <V, E> GridGraph<V, E> emptyGrid(int numCols, int numRows, GridTopology top, V defaultVertexLabel,
			E defaultEdgeLabel) {
		return new GridGraph<>(numCols, numRows, top, v -> defaultVertexLabel, (u, v) -> defaultEdgeLabel,
				UndirectedEdge::new);
	}

	static <V, E> ObservableGridGraph<V, E> fullObservableGrid(int numCols, int numRows, GridTopology top,
			V defaultVertexLabel, E defaultEdgeLabel) {
		ObservableGridGraph<V, E> g = new ObservableGridGraph<>(numCols, numRows, top, v -> defaultVertexLabel,
				(u, v) -> defaultEdgeLabel, UndirectedEdge::new);
		g.fill();
		return g;
	}

	static <V, E> ObservableGridGraph<V, E> emptyObservableGrid(int numCols, int numRows, GridTopology top,
			V defaultVertexLabel, E defaultEdgeLabel) {
		return new ObservableGridGraph<>(numCols, numRows, top, v -> defaultVertexLabel, (u, v) -> defaultEdgeLabel,
				UndirectedEdge::new);
	}
}