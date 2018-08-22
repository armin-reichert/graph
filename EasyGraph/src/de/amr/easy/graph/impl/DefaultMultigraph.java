package de.amr.easy.graph.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.amr.easy.graph.api.Edge;
import de.amr.easy.graph.api.Multigraph;

/**
 * Edge list based implementation of an undirected multigraph.
 * 
 * @author Armin Reichert
 */
public class DefaultMultigraph implements Multigraph {

	private final Set<Integer> vertexSet = new HashSet<>();
	private final List<Edge> edgeList = new ArrayList<>();

	public DefaultMultigraph() {
	}

	@Override
	public void addVertex(int vertex) {
		vertexSet.add(vertex);
	}

	@Override
	public void addEdge(Edge edge) {
		int u = edge.either(), v = edge.other();
		assertVertexExists(u);
		assertVertexExists(v);
		edgeList.add(edge);
	}

	@Override
	public Stream<Edge> edges(int v, int w) {
		assertVertexExists(v);
		assertVertexExists(w);
		return edgeList.stream().filter(edge -> {
			int either = edge.either(), other = edge.other();
			return v == either && w == other || v == other && w == either;
		});
	}

	@Override
	public void removeEdge(int u, int v) {
		edges(u, v).findFirst().ifPresent(e -> edgeList.remove(e));
	}

	@Override
	public void removeEdges() {
		edgeList.clear();
	}

	@Override
	public boolean adjacent(int u, int v) {
		assertVertexExists(u);
		assertVertexExists(v);
		return edges(u, v).findAny().isPresent();
	}

	@Override
	public IntStream vertexStream() {
		return vertexSet.stream().mapToInt(Integer::intValue);
	}

	@Override
	public int vertexCount() {
		return vertexSet.size();
	}

	@Override
	public Stream<Edge> edgeStream() {
		return edgeList.stream();
	}

	@Override
	public int edgeCount() {
		return edgeList.size();
	}

	@Override
	public int degree(int w) {
		assertVertexExists(w);
		return (int) edgeList.stream().filter(edge -> {
			int u = edge.either(), v = edge.other();
			return u == w || v == w;
		}).count();
	}

	@Override
	public IntStream adjVertices(int w) {
		assertVertexExists(w);
		return edgeList.stream().filter(edge -> {
			int u = edge.either(), v = edge.other();
			return u == w || v == w;
		}).map(edge -> {
			int u = edge.either(), v = edge.other();
			return u == w ? v : u;
		}).mapToInt(Integer::intValue);
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(vertexCount()).append("\n");
		s.append(edgeCount()).append("\n");
		for (int v : vertexSet) {
			s.append(v).append("\n");
		}
		for (Edge e : edgeList) {
			s.append(e.either()).append(" ").append(e.other()).append("\n");
		}
		return s.toString();
	}

	protected void assertVertexExists(int v) {
		if (!vertexSet.contains(v)) {
			throw new IllegalStateException("Vertex not in graph: " + v);
		}
	}
}