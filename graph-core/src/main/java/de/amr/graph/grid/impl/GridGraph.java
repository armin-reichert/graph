package de.amr.graph.grid.impl;

import static java.util.stream.IntStream.range;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.amr.graph.core.api.Edge;
import de.amr.graph.core.api.EdgeLabeling;
import de.amr.graph.core.api.VertexLabeling;
import de.amr.graph.core.impl.EdgeLabelsMap;
import de.amr.graph.core.impl.VertexLabelsMap;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.api.Topology;

/**
 * An implementation of the {@link GridGraph2D} interface.
 * 
 * @author Armin Reichert
 * 
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 */
public class GridGraph<V, E> implements GridGraph2D<V, E> {

	private final int numCols;
	private final int numRows;
	private final int numCells;
	private final BiFunction<Integer, Integer, Edge> fnEdgeFactory;
	private final VertexLabeling<V> vertexLabeling;
	private final EdgeLabeling<E> edgeLabeling;
	private Topology top;
	private BitSet wires;

	// helper methods

	private void checkCell(int cell) {
		if (!containsVertex(cell)) {
			throw new IndexOutOfBoundsException("Invalid cell: " + cell);
		}
	}

	private void checkDir(int dir) {
		if (dir < 0 || dir >= top.dirCount()) {
			throw new IndexOutOfBoundsException("Invalid direction: " + dir);
		}
	}

	private int index(int col, int row) {
		return row * numCols + col;
	}

	private int bit(int cell, int dir) {
		return cell * top.dirCount() + dir;
	}

	private void wire(int u, int v, int dir, boolean connected) {
		wires.set(bit(u, dir), connected);
		wires.set(bit(v, top.inv(dir)), connected);
	}

	/**
	 * Creates a grid with the given properties.
	 * 
	 * @param numCols
	 *                               the number of columns
	 * @param numRows
	 *                               the number of rows
	 * @param top
	 *                               the topology of this grid
	 * @param fnDefaultVertexLabel
	 *                               default vertex label
	 * @param defaultEdgeLabel
	 *                               default edge label
	 * @param fnEdgeFactory
	 *                               function for creating edges of the correct type
	 */
	public GridGraph(int numCols, int numRows, Topology top, Function<Integer, V> fnDefaultVertexLabel,
			BiFunction<Integer, Integer, E> fnDefaultEdgeLabel, BiFunction<Integer, Integer, Edge> fnEdgeFactory) {
		if (numCols < 0) {
			throw new IllegalArgumentException("Illegal number of columns: " + numCols);
		}
		if (numRows < 0) {
			throw new IllegalArgumentException("Illegal number of rows: " + numRows);
		}
		if (top == null) {
			throw new IllegalArgumentException("Grid topology must be specified");
		}
		if (fnEdgeFactory == null) {
			throw new IllegalArgumentException("Edge factory must be specified");
		}
		this.numCols = numCols;
		this.numRows = numRows;
		this.numCells = numCols * numRows;
		this.top = top;
		this.wires = new BitSet(top.dirCount() * numCells);
		this.fnEdgeFactory = fnEdgeFactory;
		this.vertexLabeling = new VertexLabelsMap<>(fnDefaultVertexLabel);
		this.edgeLabeling = new EdgeLabelsMap<>(fnDefaultEdgeLabel);
	}

	// Implement {@link Graph} interface

	@Override
	public VertexLabeling<V> getVertexLabeling() {
		return vertexLabeling;
	}

	@Override
	public EdgeLabeling<E> getEdgeLabeling() {
		return edgeLabeling;
	}

	@Override
	public IntStream vertices() {
		return range(0, numCells);
	}

	@Override
	public int numVertices() {
		return numCells;
	}

	@Override
	public boolean containsVertex(int v) {
		return v >= 0 && v < numCells;
	}

	@Override
	public Stream<Edge> edges() {
		List<Edge> edgeList = new ArrayList<>();
		/*@formatter:off*/
		vertices().forEach(cell -> {
			top.dirs()
				.filter(dir -> isConnected(cell, dir))
				.mapToObj(dir -> neighbor(cell, dir))
				.filter(OptionalInt::isPresent)
				.map(OptionalInt::getAsInt)
				.filter(neighbor -> cell < neighbor)
				.forEach(neighbor -> edgeList.add(fnEdgeFactory.apply(cell, neighbor)));
		});
		/*@formatter:on*/
		return edgeList.stream();
	}

	@Override
	public int numEdges() {
		return wires.cardinality() / 2; // two bits are used to store one edge
	}

	@Override
	public void addVertex(int v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeVertex(int v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Optional<Edge> edge(int u, int v) {
		checkCell(u);
		checkCell(v);
		return adjacent(u, v) ? Optional.of(fnEdgeFactory.apply(u, v)) : Optional.empty();
	}

	@Override
	public void addEdge(int u, int v) {
		if (!areNeighbors(u, v)) {
			throw new IllegalStateException(
					String.format("Cannot add edge {%d, %d}, cells are no grid neighbors.", u, v));
		}
		if (adjacent(u, v)) {
			throw new IllegalStateException(String.format("Cannot add edge {%d, %d}, edge already exists.", u, v));
		}
		direction(u, v).ifPresent(dir -> wire(u, v, dir, true));
	}

	@Override
	public void addEdge(int u, int v, E e) {
		addEdge(u, v);
		setEdgeLabel(u, v, e);
	}

	@Override
	public void removeEdge(int u, int v) {
		if (!adjacent(u, v)) {
			throw new IllegalStateException(
					String.format("Cannot remove edge {%d, %d}, edge does not exist.", u, v));
		}
		direction(u, v).ifPresent(dir -> wire(u, v, dir, false));
	}

	@Override
	public void removeEdges() {
		wires.clear();
	}

	@Override
	public IntStream adj(int v) {
		checkCell(v);
		return top.dirs().filter(dir -> isConnected(v, dir)).map(dir -> neighbor(v, dir).getAsInt());
	}

	@Override
	public boolean adjacent(int u, int v) {
		checkCell(u);
		checkCell(v);
		return adj(u).anyMatch(x -> x == v);
	}

	@Override
	public int degree(int v) {
		checkCell(v);
		int degree = 0;
		for (int dir = 0; dir < top.dirCount(); ++dir) {
			if (wires.get(bit(v, dir))) {
				++degree;
			}
		}
		return degree;
	}

	// Implement {@link BareGridGraph2D} interface

	@Override
	public Topology getTopology() {
		return top;
	}

	@Override
	public int numCols() {
		return numCols;
	}

	@Override
	public int numRows() {
		return numRows;
	}

	@Override
	public int cell(int col, int row) {
		if (!isValidCol(col)) {
			throw new IndexOutOfBoundsException(String.format("Invalid col: %d", col));
		}
		if (!isValidRow(row)) {
			throw new IndexOutOfBoundsException(String.format("Invalid row: %d", row));
		}
		return index(col, row);
	}

	@Override
	public int cell(GridPosition position) {
		switch (position) {
		case TOP_LEFT:
			return cell(0, 0);
		case TOP_RIGHT:
			return cell(numCols - 1, 0);
		case CENTER:
			return cell(numCols / 2, numRows / 2);
		case BOTTOM_LEFT:
			return cell(0, numRows - 1);
		case BOTTOM_RIGHT:
			return cell(numCols - 1, numRows - 1);
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public int col(int cell) {
		checkCell(cell);
		return cell % numCols;
	}

	@Override
	public int row(int cell) {
		checkCell(cell);
		return cell / numCols;
	}

	@Override
	public void fill() {
		wires.clear();
		vertices()
				.forEach(v -> top.dirs().forEach(dir -> neighbor(v, dir).ifPresent(w -> wire(v, w, dir, true))));
	}

	@Override
	public boolean isValidCol(int col) {
		return 0 <= col && col < numCols;
	}

	@Override
	public boolean isValidRow(int row) {
		return 0 <= row && row < numRows;
	}

	@Override
	public boolean areNeighbors(int u, int v) {
		return neighbors(u).anyMatch(x -> x == v);
	}

	@Override
	public IntStream neighbors(int v, IntStream dirs) {
		return dirs.mapToObj(dir -> neighbor(v, dir)).filter(OptionalInt::isPresent)
				.mapToInt(OptionalInt::getAsInt);
	}

	@Override
	public IntStream neighbors(int v) {
		return neighbors(v, top.dirs());
	}

	@Override
	public OptionalInt neighbor(int v, int dir) {
		checkCell(v);
		checkDir(dir);
		int col = col(v) + top.dx(dir);
		int row = row(v) + top.dy(dir);
		return isValidCol(col) && isValidRow(row) ? OptionalInt.of(index(col, row)) : OptionalInt.empty();
	}

	@Override
	public boolean isConnected(int v, int dir) {
		checkCell(v);
		checkDir(dir);
		return wires.get(bit(v, dir));
	}

	@Override
	public OptionalInt direction(int u, int v) {
		checkCell(u);
		checkCell(v);
		return top.dirs().filter(dir -> {
			OptionalInt neighbor = neighbor(u, dir);
			return neighbor.isPresent() && neighbor.getAsInt() == v;
		}).findFirst();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("(").append(numCols).append(" cols, ").append(numRows).append(" rows, ").append(numCells)
				.append(" cells, ").append(numEdges()).append(" edges").append(")");
		return sb.toString();
	}
}