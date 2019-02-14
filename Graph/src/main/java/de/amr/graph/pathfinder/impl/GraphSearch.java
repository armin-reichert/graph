package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.amr.graph.core.api.Graph;
import de.amr.graph.event.api.GraphTraversalObserver;
import de.amr.graph.pathfinder.api.PathFinder;
import de.amr.graph.pathfinder.api.TraversalState;

/**
 * Base class for graph search algorithms.
 * 
 * <p>
 * Stores the traversal state and parent link for each vertex and supports registration of observers
 * for vertex and edge traversals.
 * 
 * @author Armin Reichert
 */
public abstract class GraphSearch<V, E> implements PathFinder {

	protected final Graph<V, E> graph;
	private final Map<Integer, Integer> parentMap = new HashMap<>();
	private final Map<Integer, TraversalState> stateMap = new HashMap<>();
	private final Set<GraphTraversalObserver> observers = new HashSet<>(5);

	public GraphSearch(Graph<V, E> graph) {
		this.graph = graph;
	}

	/**
	 * Initializes the search such that {@link #exploreGraph(int, int)} starts in a clean state.
	 */
	protected void init() {
		parentMap.clear();
		stateMap.clear();
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
		addToFrontier(source);
		setState(source, VISITED);
		setParent(source, -1);
		while (!isFrontierEmpty()) {
			int current = removeFromFrontier();
			if (current == target) {
				return;
			}
			expandFrontier(current);
		}
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
		return buildPath(target);
	}

	private List<Integer> buildPath(int target) {
		List<Integer> path = new LinkedList<>();
		for (int v = target; v != -1; v = getParent(v)) {
			path.add(0, v);
		}
		return path.isEmpty() ? Collections.emptyList() : path;
	}

	/**
	 * Expands the frontier at the given vertex.
	 * 
	 * @param v
	 *            vertex
	 */
	protected void expandFrontier(int v) {
		graph.adj(v).filter(neighbor -> getState(neighbor) == UNVISITED).forEach(neighbor -> {
			addToFrontier(neighbor);
			setState(neighbor, VISITED);
			setParent(neighbor, v);
		});
	}

	/**
	 * Adds the given vertex to the frontier.
	 * 
	 * @param v
	 *            vertex
	 */
	protected abstract void addToFrontier(int v);

	/**
	 * Removes the next vertex from the frontier.
	 * 
	 * @return the vertex removed from the frontier
	 */
	protected abstract int removeFromFrontier();

	/**
	 * Tells if the frontier is empty.
	 * 
	 * @return {@code true} if the frontier is empty
	 */
	protected abstract boolean isFrontierEmpty();

	/**
	 * Tells if the given vertex is part of the frontier.
	 * 
	 * @param v
	 *            vertex
	 * @return <code>true</code> if the vertex is is part of the frontier
	 */
	public abstract boolean partOfFrontier(int v);

	/**
	 * Gets the current cost of the given vertex.
	 * 
	 * @param v
	 *            some vertex
	 * @return the cost of the vertex if already computed or {@code -1}
	 */
	public abstract double getCost(int v);

	/**
	 * Sets the cost of the given vertex.
	 * 
	 * @param v
	 *                some vertex
	 * @param value
	 *                the cost value
	 */
	public abstract void setCost(int v, double value);

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
		vertexTraversed(v, oldState, newState);
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
			edgeTraversed(parent, child);
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

	public void addObserver(GraphTraversalObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(GraphTraversalObserver observer) {
		observers.remove(observer);
	}

	public void removeAllObservers() {
		observers.clear();
	}

	public void edgeTraversed(int either, int other) {
		observers.forEach(observer -> observer.edgeTraversed(either, other));
	}

	public void vertexTraversed(int v, TraversalState oldState, TraversalState newState) {
		observers.forEach(observer -> observer.vertexTraversed(v, oldState, newState));
	}
}