package de.amr.graph.pathfinder.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p>
 * A path in a graph. This is an immutable object. Paths are constructed from unit paths and edges by concatenation.
 * </p>
 * 
 * @author Armin Reichert
 */
public class Path implements Iterable<Integer> {

	private static class Null extends Path {

		public Null() {
			super(Collections.emptyList());
		}

		@Override
		public int source() {
			throw new PathException("Source of NULL path is undefined");
		}

		@Override
		public int target() {
			throw new PathException("Target of NULL path is undefined");
		}

		@Override
		public Path concat(Path p) {
			return this; // 0 * p = 0
		}

		@Override
		public Path reversed() {
			return this;
		}
	}

	/** Constant representing an infinite path cost. */
	public static final double INFINITE_COST = Double.POSITIVE_INFINITY;

	/** The single NULL path instance. */
	public static final Path NULL = new Null();

	/**
	 * Creates the unit path for the given vertex.
	 * 
	 * @param v a vertex
	 * @return the path consisting of the vertex alone without edges. This is a unit wrt to the path concatenation.
	 */
	public static Path unit(int v) {
		return new Path(Collections.singletonList(v));
	}

	/**
	 * Creates the elementary path consisting of the single edge from u to v. If such an edge exists in the underlying
	 * graph is the responsibility of the caller.
	 * 
	 * @param u either vertex of the edge
	 * @param v other vertex of the edge
	 * @return path consisting of thus edge
	 */
	public static Path edge(int u, int v) {
		List<Integer> edge = new ArrayList<>(2);
		edge.add(u);
		edge.add(v);
		return new Path(edge);
	}

	private final List<Integer> vertices;

	private Path(List<Integer> vertices) {
		this.vertices = vertices;
	}

	@Override
	public int hashCode() {
		return Objects.hash(vertices);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Path other = (Path) obj;
		return Objects.equals(vertices, other.vertices);
	}

	public int source() {
		return vertices.get(0);
	}

	public int target() {
		return vertices.get(vertices.size() - 1);
	}

	public int numVertices() {
		return vertices.size();
	}

	public int numEdges() {
		return vertices.isEmpty() ? 0 : vertices.size() - 1;
	}

	public boolean isUnit() {
		return numVertices() == 1;
	}

	@Override
	public Iterator<Integer> iterator() {
		return vertices.iterator();
	}

	public Stream<Integer> vertexStream() {
		return vertices.stream();
	}

	/**
	 * Appends the given path to this path. The source of <code>p</code> must equals the target of this path.
	 * 
	 * @param p path to append
	 * @return concatenation of this path with the given path
	 */
	public Path concat(Path p) {
		Objects.requireNonNull(p);
		if (p == NULL) {
			return NULL; // p * 0 = 0
		}
		if (p.source() != target()) {
			throw new PathException("Cannot concat path, source does not match this path's target");
		}
		if (isUnit()) {
			return p; // 1 * p = p
		}
		if (p.isUnit()) {
			return this; // p * 1 = p
		}
		List<Integer> concat = new ArrayList<>(vertices);
		concat.remove(concat.size() - 1);
		concat.addAll(p.vertices);
		return new Path(concat);
	}

	/**
	 * Returns the reversed path.
	 * 
	 * @return reversed path
	 */
	public Path reversed() {
		List<Integer> reversed = new ArrayList<>(vertices);
		Collections.reverse(reversed);
		return new Path(reversed);
	}

	/**
	 * Checks if this path contains exactly the given vertices.
	 * 
	 * @param vertexSeq sequence of vertices
	 * @return if this path contains exactly the given vertices
	 */
	public boolean is(int... vertexSeq) {
		return vertices.equals(IntStream.of(vertexSeq).boxed().toList());
	}
}