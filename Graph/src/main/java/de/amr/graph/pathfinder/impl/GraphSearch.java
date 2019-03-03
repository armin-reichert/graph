package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.api.Frontier;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.PathFinder;
import de.amr.graph.pathfinder.api.TraversalState;

/**
 * Base class for graph search algorithms.
 * <p>
 * Stores the traversal state and parent link for each vertex and supports registration of observers
 * for vertex and edge traversals.
 * 
 * @author Armin Reichert
 */
public abstract class GraphSearch<V, E> implements PathFinder {

	protected final Graph<V, E> graph;
	protected final Map<Integer, Integer> parentMap;
	protected final Map<Integer, TraversalState> stateMap;
	protected final Set<GraphSearchObserver> observers;
	protected Frontier frontier;

	protected GraphSearch(Graph<V, E> graph) {
		Objects.requireNonNull(graph);
		this.graph = graph;
		parentMap = new HashMap<>();
		stateMap = new HashMap<>();
		observers = new HashSet<>(5);
	}

	/**
	 * Initializes the search such that {@link #exploreGraph(int, int)} starts in a clean state.
	 */
	public void init() {
		parentMap.clear();
		stateMap.clear();
		frontier.clear();
	}

	/**
	 * Explores the graph starting from the given source vertex until all reachable vertices have been
	 * visited.
	 * 
	 * @param source
	 *                 source vertex
	 */
	public void exploreGraph(int source) {
		exploreGraph(source, -1);
	}

	/**
	 * Explores the graph starting from the given source vertex until the given target vertex has been
	 * found or all reachable vertices have been visited.
	 * 
	 * @param source
	 *                 source vertex
	 * @param target
	 *                 target vertex
	 */
	public void exploreGraph(int source, int target) {
		init();
		setState(source, VISITED);
		setParent(source, -1);
		frontier.add(source);
		fireVertexAddedToFrontier(source);
		while (!frontier.isEmpty()) {
			int current = frontier.next();
			fireVertexRemovedFromFrontier(current);
			if (current == target) {
				return;
			}
			expandFrontier(current);
		}
	}

	/**
	 * Expands the frontier at the given vertex.
	 * 
	 * @param v
	 *            vertex
	 */
	protected void expandFrontier(int v) {
		graph.adj(v).filter(neighbor -> getState(neighbor) == UNVISITED).forEach(neighbor -> {
			setState(neighbor, VISITED);
			setParent(neighbor, v);
			frontier.add(neighbor);
			fireVertexAddedToFrontier(neighbor);
		});
	}

	/**
	 * Returns the path (list of vertices) between the given source and the given target vertex. If no
	 * such path exists, an empty list is returned.
	 * 
	 * @param source
	 *                 source vertex
	 * @param target
	 *                 target vertex
	 * @return path from source to target vertex or empty list
	 */
	@Override
	public List<Integer> findPath(int source, int target) {
		exploreGraph(source, target);
		return buildPath(source, target);
	}

	/**
	 * Creates the path from the source vertex to the target vertex as a list of vertices. Before
	 * calling this method, the graph search must have been executed.
	 * 
	 * @param source
	 *                 source vertex
	 * @param target
	 *                 target vertex
	 * @return path as list of vertices
	 */
	public List<Integer> buildPath(int source, int target) {
		List<Integer> path = new LinkedList<>();
		if (source == target) {
			path.add(source);
			return path;
		}
		if (getParent(target) == -1) {
			return Collections.emptyList();
		}
		for (int v = target; v != -1; v = getParent(v)) {
			path.add(0, v);
		}
		return path;
	}

	public Frontier getFrontier() {
		return frontier;
	}

	/**
	 * Sets the traversal state for the given vertex.
	 * 
	 * @param v
	 *                   vertex
	 * @param newState
	 *                   new vertex state
	 */
	protected void setState(int v, TraversalState newState) {
		TraversalState oldState = getState(v);
		stateMap.put(v, newState);
		fireVertexStateChanged(v, oldState, newState);
	}

	/**
	 * Returns the traversal state of the given vertex. The default state is
	 * {@link TraversalState#UNVISITED}.
	 * 
	 * @param v
	 *            vertex
	 * @return vertex state
	 */
	public TraversalState getState(int v) {
		return stateMap.getOrDefault(v, UNVISITED);
	}

	/**
	 * Sets the parent vertex for the given vertex.
	 * 
	 * @param child
	 *                 vertex
	 * @param parent
	 *                 parent vertex
	 */
	protected void setParent(int child, int parent) {
		parentMap.put(child, parent);
		if (parent != -1) {
			fireEdgeTraversed(parent, child);
		}
	}

	/**
	 * Returns the parent vertex for the given vertex. Default is <code>-1</code>.
	 * 
	 * @param v
	 *            vertex
	 * @return parent vertex or <code>-1</code>
	 */
	public int getParent(int v) {
		return parentMap.getOrDefault(v, -1);
	}

	// Observer related stuff

	public void addObserver(GraphSearchObserver observer) {
		Objects.nonNull(observer);
		observers.add(observer);
	}

	public void removeObserver(GraphSearchObserver observer) {
		Objects.nonNull(observer);
		observers.remove(observer);
	}

	public void removeAllObservers() {
		observers.clear();
	}

	protected void fireEdgeTraversed(int either, int other) {
		observers.forEach(observer -> observer.edgeTraversed(either, other));
	}

	protected void fireVertexStateChanged(int v, TraversalState oldState, TraversalState newState) {
		observers.forEach(observer -> observer.vertexStateChanged(v, oldState, newState));
	}

	protected void fireVertexAddedToFrontier(int vertex) {
		observers.forEach(observer -> observer.vertexAddedToFrontier(vertex));
	}

	protected void fireVertexRemovedFromFrontier(int vertex) {
		observers.forEach(observer -> observer.vertexAddedToFrontier(vertex));
	}
}