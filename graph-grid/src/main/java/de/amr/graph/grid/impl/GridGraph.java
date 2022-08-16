package de.amr.graph.grid.impl;

import static java.util.stream.IntStream.range;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
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
import de.amr.graph.grid.api.GridTopology;

/**
 * An implementation of the {@link GridGraph2D} interface.
 * 
 * @author Armin Reichert
 * 
 * @param <V> vertex label type
 * @param <E> edge label type
 */
public class GridGraph<V, E> implements GridGraph2D<V, E> {

	private final int numCols;
	private final int numRows;
	private final BiFunction<Integer, Integer, Edge> fnEdgeFactory;
	private final VertexLabeling<V> vertexLabeling;
	private final EdgeLabeling<E> edgeLabeling;
	private final GridTopology top;
	private final BitSet wires;

	// helper methods

	private void checkCell(int cell) {
		if (cell < 0 || cell >= numCols * numRows) {
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
	 * @param numCols              the number of columns
	 * @param numRows              the number of rows
	 * @param top                  the topology of this grid
	 * @param fnDefaultVertexLabel default vertex label
	 * @param fnDefaultEdgeLabel   default edge label
	 * @param fnEdgeFactory        function for creating edges of the correct type
	 */
	public GridGraph(int numCols, int numRows, GridTopology top, Function<Integer, V> fnDefaultVertexLabel,
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
		this.top = top;
		this.wires = new BitSet(top.dirCount() * numCols * numRows);
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
		return range(0, numCols * numRows);
	}

	@Override
	public int numVertices() {
		return numCols * numRows;
	}

	@Override
	public boolean containsVertex(int v) {
		return v >= 0 && v < numCols * numRows;
	}

	@Override
	public Stream<Edge> edges() {
		List<Edge> edgeList = new ArrayList<>();
		if (top == Grid4Topology.get()) { // optimized code for 4-direction-topology
			for (int row = 0; row < numRows; ++row) {
				for (int col = 0; col < numCols; ++col) {
					int v = index(col, row);
					if (wires.get(bit(v, Grid4Topology.E))) {
						edgeList.add(fnEdgeFactory.apply(v, neighborCell(v, Grid4Topology.E)));
					}
					if (wires.get(bit(v, Grid4Topology.S))) {
						edgeList.add(fnEdgeFactory.apply(v, neighborCell(v, Grid4Topology.S)));
					}
				}
			}
		} else {
			/*@formatter:off*/
			vertices().forEach(cell -> {
				top.dirs()
					.filter(dir -> isConnected(cell, dir))
					.map(dir -> neighbor(cell, dir))
					.filter(Optional::isPresent)
					.map(Optional::get)
					.filter(neighbor -> cell < neighbor)
					.forEach(neighbor -> edgeList.add(fnEdgeFactory.apply(cell, neighbor)));
			});
			/*@formatter:on*/
		}
		return edgeList.stream();
	}

	@Override
	public int numEdges() {
		return wires.cardinality() / 2; // two bits are used to store one edge
	}

	@Override
	public boolean isFull() {
		int c = numCols(), r = numRows();
		if (getTopology() == Grid4Topology.get()) {
			return numEdges() == 2 * c * r - c - r;
		}
		if (getTopology() == Grid8Topology.get()) {
			return numEdges() == 4 * c * r - 3 * c - 3 * r + 2;
		}
		throw new IllegalStateException("No topology");
	}

	@Override
	public void addVertex(int v) {
		throw new UnsupportedOperationException("Cannot add vertex to grid graph");
	}

	@Override
	public void removeVertex(int v) {
		throw new UnsupportedOperationException("Cannot remove vertex from grid graph");
	}

	@Override
	public Optional<Edge> edge(int u, int v) {
		return adjacent(u, v) ? Optional.of(fnEdgeFactory.apply(u, v)) : Optional.empty();
	}

	@Override
	public void addEdge(int u, int v) {
		checkCell(u);
		checkCell(v);
		if (!areNeighbors(u, v)) {
			throw new IllegalStateException(String.format("Cannot add edge {%d, %d}, cells are no grid neighbors.", u, v));
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
			throw new IllegalStateException(String.format("Cannot remove edge {%d, %d}, edge does not exist.", u, v));
		}
		direction(u, v).ifPresent(dir -> wire(u, v, dir, false));
	}

	@Override
	public void removeEdges() {
		wires.clear();
	}

	@Override
	public Stream<Integer> adj(int v) {
		checkCell(v);
		return top.dirs().filter(dir -> isConnected(v, dir)).map(dir -> neighborCell(v, dir));
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
	public GridTopology getTopology() {
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
			return index(0, 0);
		case TOP_RIGHT:
			return index(numCols - 1, 0);
		case CENTER:
			return index(numCols / 2, numRows / 2);
		case BOTTOM_LEFT:
			return index(0, numRows - 1);
		case BOTTOM_RIGHT:
			return index(numCols - 1, numRows - 1);
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
		//@formatter:off
		vertices().forEach(cell -> top.dirs().forEach(dir -> {
			int neighbor = neighborCell(cell, dir);
			if (neighbor != NO_VERTEX) {
				wire(cell, neighbor, dir, true);
			}
		}));
		//@formatter:off
	}
	
	@Override
	public void fillOrthogonal() {
		wires.clear();
		//@formatter:off
		vertices().forEach(cell -> top.dirs().filter(top::isOrthogonal).forEach(dir -> {
			int neighbor = neighborCell(cell, dir);
			if (neighbor != NO_VERTEX) {
				wire(cell, neighbor, dir, true);
			}
		}));
		//@formatter:off
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
	public Stream<Integer> neighbors(int v) {
		return top.dirs().map(dir -> neighborCell(v, dir)).filter(cell -> cell != NO_VERTEX);
	}

	@Override
	public Optional<Integer> neighbor(int v, byte dir) {
		checkCell(v);
		checkDir(dir);
		int neighbor = neighborCell(v, dir);
		return neighbor == NO_VERTEX ? Optional.empty() : Optional.of(neighbor);
	}

	private int neighborCell(int v, int dir) {
		int col = col(v) + top.dx(dir);
		int row = row(v) + top.dy(dir);
		return isValidCol(col) && isValidRow(row) ? index(col, row) : NO_VERTEX;
	}

	@Override
	public boolean isConnected(int v, byte dir) {
		checkCell(v);
		checkDir(dir);
		return wires.get(bit(v, dir));
	}

	@Override
	public Optional<Byte> direction(int u, int v) {
		checkCell(u);
		checkCell(v);
		return top.dirs().filter(dir -> neighbor(u, dir).orElse(NO_VERTEX) == v).findFirst();
	}

	@Override
	public String toString() {
		return String.format("%s (%d cols, %d rows, %d cells, %d edges, %s)", getClass().getName(), numCols, numRows, numCols*numRows, numEdges(), top);
	}
}