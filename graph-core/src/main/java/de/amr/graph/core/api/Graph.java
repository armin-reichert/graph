package de.amr.graph.core.api;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Interface for a graph with vertex and edge labels.
 * 
 * <p>
 * Vertices are represented by integers and can be labeled by arbitrary objects of the given vertex label type. Edges
 * can be labeled by objects of the specified edge label type.
 *
 * @param <V> vertex label type
 * @param <E> edge label type
 * 
 * @author Armin Reichert
 */
public interface Graph<V, E> extends VertexLabeling<V>, EdgeLabeling<E> {

	/** Constant representing no vertex. */
	int NO_VERTEX = -1;

	/**
	 * @return stream of the vertices of this graph
	 */
	IntStream vertices();

	/**
	 * @return the number of vertices of this graph
	 */
	default int numVertices() {
		return (int) vertices().count();
	}

	/**
	 * Tells if this graph contains the given vertex.
	 * 
	 * @param v vertex
	 * @return {@code true} if the given vertex is contained in this graph
	 */
	boolean containsVertex(int v);

	/**
	 * @return stream of the edges of this graph
	 */
	Stream<Edge> edges();

	/**
	 * @return the number of edges of this graph
	 */
	default int numEdges() {
		return (int) edges().count();
	}

	/**
	 * Adds the given vertex to this graph.
	 * 
	 * @param v a vertex
	 */
	void addVertex(int v);

	/**
	 * Adds the given vertex to this graph and assigns the given label.
	 * 
	 * @param v     a vertex
	 * @param label vertex label
	 */
	default void addVertex(int v, V label) {
		addVertex(v);
		set(v, label);
	}

	/**
	 * Removes the given vertex from this graph.
	 * 
	 * @param v a vertex
	 */
	void removeVertex(int v);

	/**
	 * @param v a vertex
	 * @return all "adjacent" vertices (connected by some edge) to the given vertex
	 */
	Stream<Integer> adj(int v);

	/**
	 * Tells if the given vertices are connected by some edge.
	 * 
	 * @param v a vertex
	 * @param w a vertex
	 * @return {@code true} if there exists an edge between the vertices
	 */
	boolean adjacent(int v, int w);

	/**
	 * @param v a vertex
	 * @return the number of vertices adjacent to <code>v</code>
	 */
	default int degree(int v) {
		return (int) adj(v).count();
	}

	/**
	 * Adds an edge between the given vertices.
	 * 
	 * @param v a vertex
	 * @param w a vertex
	 */
	void addEdge(int v, int w);

	/**
	 * Adds an edge with a label between the given vertices.
	 * 
	 * @param v         a vertex
	 * @param w         a vertex
	 * @param edgeLabel edge label
	 */
	void addEdge(int v, int w, E edgeLabel);

	/**
	 * @param v a vertex
	 * @param w a vertex
	 * @return the edge between the vertices if it exists
	 */
	Optional<Edge> edge(int v, int w);

	/**
	 * Removes the edge between the given vertices.
	 * 
	 * @param edge an edge
	 */
	void removeEdge(int v, int w);

	/**
	 * Removes the given edge.
	 * 
	 * @param edge an edge
	 */
	default void removeEdge(Edge edge) {
		removeEdge(edge.either(), edge.other());
	}

	/**
	 * Removes all edges from this graph.
	 */
	void removeEdges();

	/* Default implementations for vertex labeling. */

	VertexLabeling<V> getVertexLabeling();

	@Override
	default void clearVertexLabels() {
		getVertexLabeling().clearVertexLabels();
	}

	@Override
	default V getDefaultVertexLabel(int v) {
		return getVertexLabeling().getDefaultVertexLabel(v);
	}

	@Override
	default void setDefaultVertexLabel(IntFunction<V> fnDefaultLabel) {
		getVertexLabeling().setDefaultVertexLabel(fnDefaultLabel);
	}

	@Override
	default V get(int v) {
		return getVertexLabeling().get(v);
	}

	@Override
	default void set(int v, V vertex) {
		if (!containsVertex(v)) {
			throw new IllegalArgumentException("Illegal vertex: " + v);
		}
		getVertexLabeling().set(v, vertex);
	}

	/* Default implementations for edge labeling. */

	EdgeLabeling<E> getEdgeLabeling();

	@Override
	default void clearEdgeLabels() {
		getEdgeLabeling().clearEdgeLabels();
	}

	@Override
	default E getDefaultEdgeLabel(int u, int v) {
		return getEdgeLabeling().getDefaultEdgeLabel(u, v);
	}

	@Override
	default void setDefaultEdgeLabel(BiFunction<Integer, Integer, E> fnDefaultLabel) {
		getEdgeLabeling().setDefaultEdgeLabel(fnDefaultLabel);
	}

	@Override
	default E getEdgeLabel(int u, int v) {
		return getEdgeLabeling().getEdgeLabel(u, v);
	}

	@Override
	default void setEdgeLabel(int u, int v, E e) {
		getEdgeLabeling().setEdgeLabel(u, v, e);
	}
}