package de.amr.easy.graph.pathfinder.impl;

import static de.amr.easy.graph.pathfinder.api.TraversalState.UNVISITED;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.amr.easy.graph.event.api.GraphTraversalObserver;
import de.amr.easy.graph.pathfinder.api.PathFinder;
import de.amr.easy.graph.pathfinder.api.TraversalState;

/**
 * Abstract base class for graph traversals. Stores traversal state and parent link for each vertex
 * and allows to register observers for vertex and edge traversals.
 * 
 * @author Armin Reichert
 */
public abstract class AbstractSearch implements PathFinder {

	private final Map<Integer, Integer> parentMap = new HashMap<>();

	private final Map<Integer, TraversalState> stateMap = new HashMap<>();

	private final Set<GraphTraversalObserver> observers = new HashSet<>(5);

	/**
	 * Initializes the traversal such that {@link #traverseGraph(int, int)} starts in a clean state.
	 */
	protected void init() {
		parentMap.clear();
		stateMap.clear();
	}

	/**
	 * Traverses the graph starting from the given source until all reachable vertices are visited.
	 * 
	 * @param source
	 *                 source vertex
	 */
	public void traverseGraph(int source) {
		traverseGraph(source, -1);
	}

	/**
	 * @param target
	 *                 target vertex
	 * @return path from source to target vertex
	 */
	@Override
	public List<Integer> path(int target) {
		List<Integer> path = new LinkedList<>();
		for (int v = target; v != -1; v = getParent(v)) {
			path.add(0, v);
		}
		return path;
	}

	protected void setState(int v, TraversalState newState) {
		TraversalState oldState = getState(v);
		stateMap.put(v, newState);
		vertexTraversed(v, oldState, newState);
	}

	public TraversalState getState(int v) {
		return stateMap.getOrDefault(v, UNVISITED);
	}

	protected void setParent(int child, int parent) {
		parentMap.put(child, parent);
		if (parent != -1) {
			edgeTraversed(parent, child);
		}
	}

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