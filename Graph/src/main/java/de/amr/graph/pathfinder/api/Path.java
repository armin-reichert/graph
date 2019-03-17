package de.amr.graph.pathfinder.api;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.amr.datastruct.StreamUtils;
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * A path in a graph as list of vertices.
 * 
 * @author Armin Reichert
 */
public class Path implements Iterable<Integer> {

	public static final double INFINITE_COST = Double.MAX_VALUE;

	public static final Path EMPTY_PATH = new Path(Collections.emptyList());

	private List<Integer> vertexList;

	private Path(List<Integer> vertexList) {
		this.vertexList = vertexList;
	}

	public static Path computePath(int source, int target, GraphSearch<?> search) {
		search.exploreGraph(source, target);
		return constructPath(source, target, search);
	}

	public static Path constructPath(int source, int target, GraphSearch<?> search) {
		if (search.getParent(target) == -1) {
			return new Path(Collections.emptyList()); // no path to target
		}
		if (source == target) {
			return new Path(Collections.singletonList(source)); // trivial path
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
		List<Integer> vertexArrayList = IntStream.of(vertices).boxed().collect(Collectors.toList());
		return vertexList.equals(vertexArrayList);
	}

	@Override
	public Iterator<Integer> iterator() {
		return vertexList.iterator();
	}

	public IntStream vertexStream() {
		return StreamUtils.toIntStream(vertexList);
	}
}
