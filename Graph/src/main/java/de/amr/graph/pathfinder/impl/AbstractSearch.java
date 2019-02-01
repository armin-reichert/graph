package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
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
 * Abstract base class for graph search algorithms.
 * 
 * <p>
 * Stores the traversal state and parent link for each vertex and supports registration of observers
 * for vertex and edge traversals.
 * 
 * @author Armin Reichert
 */
public abstract class AbstractSearch<V, E> implements PathFinder {

	protected final Graph<V, E> graph;
	private final Map<Integer, Integer> parentMap = new HashMap<>();
	private final Map<Integer, TraversalState> stateMap = new HashMap<>();
	private final Set<GraphTraversalObserver> observers = new HashSet<>(5);

	public AbstractSearch(Graph<V, E> graph) {
		this.graph = graph;
	}

	/**
	 * Initializes the search such that {@link #traverseGraph(int, int)} starts in a clean state.
	 */
	protected void init() {
		parentMap.clear();
		stateMap.clear();
	}

	/**
	 * Runs the search algorithm starting from the given source vertex and ending when all vertices
	 * reachable from the source have been visited.
	 * 
	 * @param source
	 *                 source vertex
	 */
	public void traverseGraph(int source) {
		traverseGraph(source, -1);
	}

	/**
	 * Runs the search algorithm starting from the given source vertex and ending when the given target
	 * vertex has been found or all vertices reachable from the source have been visited.
	 * 
	 * @param source
	 *                 source vertex
	 * @param target
	 *                 target vertex
	 */
	public void traverseGraph(int source, int target) {
		init();
		enqueue(source);
		setState(source, VISITED);
		setParent(source, -1);
		while (!isQueueEmpty()) {
			int current = dequeue();
			setState(source, COMPLETED);
			if (current == target) {
				return;
			}
			expand(current);
		}
	}

	/**
	 * Expands the given vertex.
	 * 
	 * @param v
	 *            vertex
	 */
	protected void expand(int current) {
		graph.adj(current).filter(neighbor -> getState(neighbor) == UNVISITED).forEach(neighbor -> {
			enqueue(neighbor);
			setState(neighbor, VISITED);
			setParent(neighbor, current);
		});
	}

	/**
	 * Adds the given vertex to the search queue.
	 * 
	 * @param v
	 *            vertex
	 */
	protected abstract void enqueue(int v);

	/**
	 * Takes the given vertex from the search queue.
	 * 
	 * @return vertex taken from queue
	 */
	protected abstract int dequeue();

	/**
	 * Tells if the search queue is empty.
	 * 
	 * @return true if the search queue is empty
	 */
	protected abstract boolean isQueueEmpty();

	/**
	 * Tells if the given vertex is in the search queue.
	 * 
	 * @param v
	 *            vertex
	 * @return <code>true</code> if the vertex is on the queue
	 */
	public abstract boolean inQueue(int v);

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
	public List<Integer> path(int source, int target) {
		traverseGraph(source, target);
		List<Integer> path = new LinkedList<>();
		for (int v = target; v != -1; v = getParent(v)) {
			path.add(0, v);
		}
		return path.isEmpty() ? Collections.emptyList() : path;
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
	 * Sets the parent vertex for the given child vertex.
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