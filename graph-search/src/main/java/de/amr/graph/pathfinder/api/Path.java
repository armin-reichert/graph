package de.amr.graph.pathfinder.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.amr.datastruct.StreamUtils;

/**
 * A path in a graph as an immutable list of its vertices.
 * 
 * @author Armin Reichert
 */
public class Path implements Iterable<Integer> {

	public static final double INFINITE_COST = Double.MAX_VALUE;

	public static final Path EMPTY_PATH = new Path(0);

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

	public static Path singletonPath(int vertex) {
		Path p = new Path(1);
		p.vertexList.add(vertex);
		return p;
	}

	public static Path copy(Path p) {
		Path copy = new Path(p.numVertices());
		copy.vertexList.addAll(p.vertexList);
		return copy;
	}

	public static Path add(Path p, Path q) {
		Path sum = copy(p);
		sum.vertexList.addAll(q.vertexList);
		return sum;
	}

	public static Path reversed(Path p) {
		Path result = copy(p);
		Collections.reverse(p.vertexList);
		return result;
	}

	public static Path findPath(int source, int target, GraphSearch search) {
		boolean found = search.exploreGraph(source, target);
		return found ? extractPath(source, target, search) : EMPTY_PATH;
	}

	public static Path extractPath(int source, int target, GraphSearch search) {
		if (source == -1) {
			throw new IllegalArgumentException("Illegal source vertex");
		}
		if (target == -1) {
			throw new IllegalArgumentException("Illegal target vertex");
		}
		if (search.getParent(target) == -1) {
			return EMPTY_PATH; // no path to target
		}
		if (source == target) {
			return singletonPath(source); // trivial path
		}
		List<Integer> vertexList = new LinkedList<>();
		for (int v = target; v != -1; v = search.getParent(v)) {
			vertexList.add(0, v);
		}
		return new Path(vertexList);
	}

	public int numVertices() {
		return vertexList.size();
	}

	public int numEdges() {
		return vertexList.size() - 1;
	}

	public Optional<Integer> source() {
		return vertexList.isEmpty() ? Optional.empty() : Optional.of(vertexList.get(0));
	}

	public Optional<Integer> target() {
		return vertexList.isEmpty() ? Optional.empty() : Optional.of(vertexList.get(vertexList.size() - 1));
	}

	public boolean is(int... vertices) {
		return vertexList.equals(IntStream.of(vertices).boxed().collect(Collectors.toList()));
	}

	@Override
	public Iterator<Integer> iterator() {
		return vertexList.iterator();
	}

	public IntStream vertexStream() {
		return StreamUtils.toIntStream(vertexList);
	}
}