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
 * Stores the traversal state, parent link and cost for each vertex. Also
 * supports registration of observers for vertex and edge traversals and for
 * changes of the search queue (frontier).
 * 
 * @author Armin Reichert
 */
public abstract class AbstractGraphSearch<Q extends VertexQueue> implements ObservableGraphSearch {

	protected final Graph<?, ?> graph;
	protected final Map<Integer, SearchInfo> nodeInfo;
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

	protected AbstractGraphSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost, Q frontier) {
		this.graph = Objects.requireNonNull(graph);
		this.nodeInfo = new HashMap<>();
		this.observers = new HashSet<>(5);
		this.fnEdgeCost = fnEdgeCost;
		this.frontier = frontier;
	}

	protected void clear() {
		nodeInfo.clear();
		frontier.clear();
		maxCost = 0;
		current = source = target = Graph.NO_VERTEX;
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
		clear();
		this.source = source;
		this.target = target;
		this.current = source;
		frontier.add(source);
		setState(source, VISITED);
		setParent(source, Graph.NO_VERTEX);
		setCost(source, 0);
		fireVertexAddedToFrontier(source);
	}

	/**
	 * Expands the frontier at the given vertex. Subclasses may modify this.
	 * 
	 * @param v vertex to be expanded
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
	public int getSource() {
		return source;
	}

	@Override
	public int getTarget() {
		return target;
	}

	@Override
	public int getCurrentVertex() {
		return current;
	}

	@Override
	public OptionalInt getNextVertex() {
		return frontier.peek();
	}

	private SearchInfo getOrCreateNodeInfo(int node) {
		SearchInfo info = nodeInfo.get(node);
		if (info == null) {
			info = new SearchInfo();
			info.parent = Graph.NO_VERTEX;
			info.traversalState = TraversalState.UNVISITED;
			info.cost = Path.INFINITE_COST;
			nodeInfo.put(node, info);
		}
		return info;
	}

	@Override
	public TraversalState getState(int v) {
		return nodeInfo.containsKey(v) ? nodeInfo.get(v).traversalState : UNVISITED;
	}

	/**
	 * Sets the traversal state for the given vertex.
	 * 
	 * @param v        vertex
	 * @param newState new vertex state
	 */
	protected void setState(int v, TraversalState newState) {
		SearchInfo info = getOrCreateNodeInfo(v);
		TraversalState oldState = info.traversalState;
		info.traversalState = newState;
		fireVertexStateChanged(v, oldState, newState);
	}

	@Override
	public int getParent(int v) {
		return nodeInfo.containsKey(v) ? nodeInfo.get(v).parent : Graph.NO_VERTEX;
	}

	/**
	 * Sets the parent vertex for the given vertex.
	 * 
	 * @param child  vertex
	 * @param parent parent vertex
	 */
	@Override
	public void setParent(int child, int parent) {
		if (child == parent) {
			throw new IllegalStateException("Cannot set parent to itself");
		}
		SearchInfo childInfo = getOrCreateNodeInfo(child);
		childInfo.parent = parent;
		if (parent != Graph.NO_VERTEX) {
			childInfo.cost = getCost(parent) + fnEdgeCost.applyAsDouble(parent, child);
			maxCost = Math.max(maxCost, getCost(child));
		} else {
			childInfo.cost = 0;
			maxCost = 0;
		}
		if (parent != Graph.NO_VERTEX) {
			fireEdgeTraversed(parent, child);
		}
	}

	@Override
	public double getCost(int v) {
		return nodeInfo.containsKey(v) ? nodeInfo.get(v).cost : Path.INFINITE_COST;
	}

	@Override
	public void setCost(int v, double value) {
		getOrCreateNodeInfo(v).cost = value;
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