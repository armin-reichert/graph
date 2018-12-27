package de.amr.easy.grid.impl;

import de.amr.easy.graph.api.UndirectedEdge;
import de.amr.easy.graph.api.traversal.TraversalState;

/**
 * Type of 2D grid used by maze generator implementations.
 * 
 * @author Armin Reichert
 */
public class OrthogonalGrid extends ObservableGridGraph<TraversalState, Integer> {

	public static OrthogonalGrid emptyGrid(int numCols, int numRows, TraversalState defaultState) {
		OrthogonalGrid grid = new OrthogonalGrid(numCols, numRows, defaultState);
		return grid;
	}

	public static OrthogonalGrid fullGrid(int numCols, int numRows, TraversalState defaultState) {
		OrthogonalGrid grid = new OrthogonalGrid(numCols, numRows, defaultState);
		grid.fill();
		return grid;
	}

	private OrthogonalGrid(int numCols, int numRows, TraversalState defaultState) {
		super(numCols, numRows, new Top4(), v -> defaultState, (u, v) -> 1, UndirectedEdge::new);
	}

	public boolean isUnvisited(int v) {
		return get(v) == TraversalState.UNVISITED;
	}

	public boolean isVisited(int v) {
		return get(v) == TraversalState.VISITED;
	}

	public boolean isCompleted(int v) {
		return get(v) == TraversalState.COMPLETED;
	}
}