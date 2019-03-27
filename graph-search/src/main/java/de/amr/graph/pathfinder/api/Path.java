package de.amr.graph.pathfinder.api;

import static de.amr.graph.pathfinder.api.GraphSearch.NO_VERTEX;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.amr.datastruct.StreamUtils;

/**
 * A path in a graph as an immutable list of its vertices.
 * 
 * @author Armin Reichert
 */
public class Path implements Iterable<Integer> {

	public static final double INFINITE_COST = Double.POSITIVE_INFINITY;

	public static final Path NO_PATH = new Path(Collections.emptyList());

	public static Path unit(int v) {
		return new Path(Collections.singletonList(v));
	}

	public static Path edge(int u, int v) {
		List<Integer> vertexList = new ArrayList<>();
		vertexList.add(u);
		vertexList.add(v);
		return new Path(vertexList);
	}

	public static Path extractPath(int source, int target, GraphSearch search) {
		if (source == -1) {
			throw new IllegalArgumentException("Illegal source vertex");
		}
		if (target == -1) {
			throw new IllegalArgumentException("Illegal target vertex");
		}
		if (search.getParent(target) == NO_VERTEX) {
			return NO_PATH;
		}
		if (source == target) {
			return unit(source); // trivial path
		}
		List<Integer> vertexList = new LinkedList<>();
		for (int v = target; v != -1; v = search.getParent(v)) {
			vertexList.add(0, v);
		}
		return new Path(vertexList);
	}

	private final List<Integer> vertexList;

	private Path(int initialCapacity) {
		vertexList = new ArrayList<>(initialCapacity);
	}

	private Path(List<Integer> vertexList) {
		this.vertexList = vertexList;
	}

	@Override
	public int hashCode() {
		return Objects.hash(vertexList);
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
		return Objects.equals(vertexList, other.vertexList);
	}

	public int source() {
		if (this == NO_PATH) {
			throw new IllegalArgumentException("NO_PATH has no source");
		}
		return vertexList.get(0);
	}

	public int target() {
		if (this == NO_PATH) {
			throw new IllegalArgumentException("NO_PATH has no target");
		}
		return vertexList.get(vertexList.size() - 1);
	}

	public int numVertices() {
		return vertexList.size();
	}

	public int numEdges() {
		return vertexList.isEmpty() ? 0 : vertexList.size() - 1;
	}

	@Override
	public Iterator<Integer> iterator() {
		return vertexList.iterator();
	}

	public IntStream vertexStream() {
		return StreamUtils.toIntStream(vertexList);
	}

	/**
	 * Returns a copy of this path.
	 * 
	 * @return path copy
	 */
	public Path copy() {
		if (this == NO_PATH) {
			throw new IllegalArgumentException("NO_PATH cannot be copied");
		}
		Path copy = new Path(numVertices());
		copy.vertexList.addAll(vertexList);
		return copy;
	}

	/**
	 * Appends the given path to this path. The source of <code>p</code> must equals the target of this
	 * path.
	 * 
	 * @param p
	 *            path to append
	 * @return concatenation of this path with the given path
	 */
	public Path concat(Path p) {
		Objects.requireNonNull(p);
		if (this == NO_PATH) {
			throw new IllegalArgumentException("Cannot append to NO_PATH");
		}
		if (p == NO_PATH) {
			throw new IllegalArgumentException("Cannot append NO_PATH");
		}
		if (numVertices() == 1) {
			return p.copy();
		}
		if (p.numVertices() == 1) {
			return copy();
		}
		Path sum = copy();
		sum.vertexList.remove(sum.vertexList.size() - 1);
		sum.vertexList.addAll(p.vertexList);
		return sum;
	}

	/**
	 * Returns a reversed copy of this path.
	 * 
	 * @return reversed copy
	 */
	public Path reversed() {
		if (this == NO_PATH) {
			throw new IllegalArgumentException("Cannot reverse NO_PATH");
		}
		Path result = copy();
		Collections.reverse(result.vertexList);
		return result;
	}

	public boolean is(int... vertices) {
		return vertexList.equals(IntStream.of(vertices).boxed().collect(Collectors.toList()));
	}
}