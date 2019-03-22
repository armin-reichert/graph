package de.amr.graph.pathfinder.impl;

import static de.amr.graph.core.api.TraversalState.COMPLETED;
import static de.amr.graph.core.api.TraversalState.UNVISITED;
import static de.amr.graph.core.api.TraversalState.VISITED;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.api.VertexQueue;

/**
 * Base class for graph search algorithms.
 * <p>
 * Stores the traversal state, parent link and cost for each vertex. Also supports registration of
 * observers for vertex and edge traversals and for changes of the search queue (frontier).
 * 
 * @author Armin Reichert
 */
public abstract class AbstractGraphSearch<Q extends VertexQueue> implements ObservableGraphSearch {

	protected final Graph<?, ?> graph;
	protected final Map<Integer, Integer> parentMap;
	protected final Map<Integer, TraversalState> stateMap;
	protected final Map<Integer, Double> costMap;
	protected final Set<GraphSearchObserver> observers;
	protected final ToDoubleBiFunction<Integer, Integer> fnEdgeCost;
	protected double maxCost;
	protected Q frontier;
	protected int current, source, target;

	protected AbstractGraphSearch(Graph<?, ?> graph) {
		this(graph, (u, v) -> 1);
	}

	protected AbstractGraphSearch(Graph<?, ?> graph, Q frontier) {
		this(graph, (u, v) -> 1, frontier);
	}

	protected AbstractGraphSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		this(graph, fnEdgeCost, null);
	}

	protected AbstractGraphSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost,
			Q frontier) {
		this.graph = Objects.requireNonNull(graph);
		this.parentMap = new HashMap<>();
		this.stateMap = new HashMap<>();
		this.costMap = new HashMap<>();
		this.observers = new HashSet<>(5);
		this.fnEdgeCost = fnEdgeCost;
		this.frontier = frontier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.amr.graph.pathfinder.impl.GraphSearch#init()
	 */
	@Override
	public void init() {
		parentMap.clear();
		stateMap.clear();
		costMap.clear();
		frontier.clear();
		maxCost = 0;
		current = source = target = -1;
	}

	@Override
	public boolean canExplore() {
		return !frontier.isEmpty();
	}

	@Override
	public boolean exploreVertex() {
		current = frontier.poll();
		setState(current, COMPLETED);
		fireVertexRemovedFromFrontier(current);
		if (current == target) {
			return true;
		}
		expand(current);
		return false;
	}

	@Override
	public void start(int source, int target) {
		this.source = source;
		this.target = target;
		setState(source, VISITED);
		setParent(source, -1);
		frontier.add(source);
		fireVertexAddedToFrontier(source);
	}

	/**
	 * Expands the frontier at the given vertex. Subclasses may modify this.
	 * 
	 * @param v
	 *            vertex to be expanded
	 */
	protected void expand(int v) {
		graph.adj(v).filter(neighbor -> getState(neighbor) == UNVISITED).forEach(neighbor -> {
			setState(neighbor, VISITED);
			setParent(neighbor, v);
			frontier.add(neighbor);
			fireVertexAddedToFrontier(neighbor);
		});
	}

	@Override
	public int getCurrentVertex() {
		return current;
	}

	@Override
	public OptionalInt getNextVertex() {
		return frontier.peek();
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

	@Override
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
	@Override
	public void setParent(int child, int parent) {
		parentMap.put(child, parent);
		if (parent != -1) {
			setCost(child, getCost(parent) + fnEdgeCost.applyAsDouble(parent, child));
			maxCost = Math.max(maxCost, getCost(child));
		}
		else {
			setCost(child, 0);
			maxCost = 0;
		}
		if (parent != -1) {
			fireEdgeTraversed(parent, child);
		}
	}

	@Override
	public int getParent(int v) {
		return parentMap.getOrDefault(v, -1);
	}

	@Override
	public double getCost(int v) {
		return costMap.getOrDefault(v, Path.INFINITE_COST);
	}

	@Override
	public void setCost(int v, double value) {
		costMap.put(v, value);
	}

	@Override
	public double getMaxCost() {
		return maxCost;
	}

	@Override
	public Optional<Integer> getMaxCostVertex() {
		return graph.vertices().boxed().max(Comparator.comparing(this::getCost));
	}

	// Observer related stuff

	@Override
	public void addObserver(GraphSearchObserver observer) {
		Objects.requireNonNull(observer);
		observers.add(observer);
	}

	@Override
	public void removeObserver(GraphSearchObserver observer) {
		Objects.requireNonNull(observer);
		observers.remove(observer);
	}

	@Override
	public void removeAllObservers() {
		observers.clear();
	}

	protected void fireEdgeTraversed(int either, int other) {
		observers.forEach(observer -> observer.edgeTraversed(either, other));
	}

	protected void fireVertexAddedToFrontier(int vertex) {
		observers.forEach(observer -> observer.vertexAddedToFrontier(vertex));
	}

	protected void fireVertexRemovedFromFrontier(int vertex) {
		observers.forEach(observer -> observer.vertexRemovedFromFrontier(vertex));
	}

	protected void fireVertexStateChanged(int vertex, TraversalState oldState, TraversalState newState) {
		if (oldState != newState) {
			observers.forEach(observer -> observer.vertexStateChanged(vertex, oldState, newState));
		}
	}
}