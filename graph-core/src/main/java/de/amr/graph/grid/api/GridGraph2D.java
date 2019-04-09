package de.amr.graph.grid.api;

import static java.lang.Math.abs;
import static java.lang.Math.hypot;
import static java.lang.Math.max;

import java.util.OptionalInt;
import java.util.stream.IntStream;

import de.amr.graph.core.api.Graph;

/**
 * Interface for a two-dimensional grid graph.
 * 
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 * 
 * @author Armin Reichert
 */
public interface GridGraph2D<V, E> extends Graph<V, E> {

	/**
	 * @return the number of columns (width) of the grid
	 */
	int numCols();

	/**
	 * @return the number of rows (height) of the grid
	 */
	int numRows();

	/**
	 * @return the topology of this grid
	 */
	Topology getTopology();

	/**
	 * @param col
	 *              a column index
	 * @param row
	 *              a row index
	 * 
	 * @return the cell index ("cell") for coordinate (col, row)
	 */
	int cell(int col, int row);

	/**
	 * @param position
	 *                   a symbolic grid position like TOP_LEFT
	 * 
	 * @return the cell index at the given position
	 */
	int cell(GridPosition position);

	/**
	 * @param cell
	 *               a cell index
	 * 
	 * @return the column index of the given cell
	 */
	int col(int cell);

	/**
	 * @param cell
	 *               a cell index
	 * 
	 * @return the row index of the given cell
	 */
	int row(int cell);

	/**
	 * @param col
	 *              the column index
	 * 
	 * @return {@code true} if the given column index is valid
	 */
	boolean isValidCol(int col);

	/**
	 * @param row
	 *              the row index
	 * 
	 * @return if given row index is valid
	 */
	boolean isValidRow(int row);

	/**
	 * Returns all neighbors of a cell in the given directions.
	 * 
	 * @param cell
	 *               a grid cell
	 * @param dirs
	 *               a stream of directions
	 * 
	 * @return stream of the neighbor cells in the given directions
	 */
	IntStream neighbors(int cell, IntStream dirs);

	/**
	 * Returns all neighbors of a cell. A neighbor is a cell that possibly can be connected to the cell
	 * by an edge. The neighbors are defined by the grid's topology (4 neighbors, 8 neighbors etc.).
	 * 
	 * @param cell
	 *               a grid cell
	 * 
	 * @return stream of all neighbor cells
	 */
	IntStream neighbors(int cell);

	/**
	 * @param cell
	 *               a grid position
	 * @param dir
	 *               a direction
	 * @return the (optional) neighbor in the given direction
	 */
	OptionalInt neighbor(int cell, int dir);

	/**
	 * Tells if the given cells are "neighbors".
	 * 
	 * @param either
	 *                 either cell
	 * @param other
	 *                 another cell
	 * 
	 * @return {@code true} if the cells are neighbors wrt. to the grid's topology
	 */
	boolean areNeighbors(int either, int other);

	/**
	 * @param cell
	 *               a grid cell
	 * @param dir
	 *               a direction
	 * 
	 * @return {@code true} if the cell is connected to the neighbor in the given direction ("passage",
	 *         no "wall")
	 */
	boolean isConnected(int cell, int dir);

	/**
	 * @param either
	 *                 either cell
	 * @param other
	 *                 other cell
	 * 
	 * @return (optional) direction from either to other (if those cells are neighbors)
	 */
	OptionalInt direction(int either, int other);

	/**
	 * Makes this grid a full grid by adding all possible edges.
	 */
	void fill();

	/**
	 * Tells if this grid is empty.
	 * 
	 * @return {@code true} if this grid is empty i.e. has no edges
	 */
	default boolean isEmpty() {
		return numEdges() == 0;
	}

	/**
	 * Tells if this grid is full.
	 * 
	 * @return {@code true} if this grid is full i.e. has all edges
	 */
	boolean isFull();

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
		int dx = abs(col(u) - col(v)), dy = abs(row(u) - row(v));
		return max(dx, dy);
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
		int dx = abs(col(u) - col(v)), dy = abs(row(u) - row(v));
		return dx + dy;
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
		int dx = abs(col(u) - col(v)), dy = abs(row(u) - row(v));
		return hypot(dx, dy);
	}
}