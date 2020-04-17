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
 * @param <Q>           type of search queue (FIFO, LIFO, priority queue)
 * @param <VERTEX_INFO> type of search info stored for each vertex
 * 
 * @author Armin Reichert
 */
public abstract class AbstractGraphSearch<Q extends VertexQueue, VERTEX_INFO extends BasicSearchInfo>
		implements ObservableGraphSearch {

	protected final Graph<?, ?> graph;
	protected final Map<Integer, VERTEX_INFO> vertexInfo;
	protected final Set<GraphSearchObserver> observers;
	protected final ToDoubleBiFunction<Integer, Integer> fnEdgeCost;
	protected double maxCost;
	protected Q frontier;
	protected int current, source, target;

	@SuppressWarnings("unchecked")
	protected VERTEX_INFO createVertexInfo(int v) {
		return (VERTEX_INFO) new BasicSearchInfo();
	}

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
		this.vertexInfo = new HashMap<>();
		this.observers = new HashSet<>(5);
		this.fnEdgeCost = fnEdgeCost;
		this.frontier = frontier;
	}

	protected void clear() {
		vertexInfo.clear();
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

	protected VERTEX_INFO getOrCreateVertexInfo(int v) {
		VERTEX_INFO info = vertexInfo.get(v);
		if (info == null) {
			info = createVertexInfo(v);
			info.parent = Graph.NO_VERTEX;
			info.state = TraversalState.UNVISITED;
			info.cost = Path.INFINITE_COST;
			vertexInfo.put(v, info);
		}
		return info;
	}

	@Override
	public TraversalState getState(int v) {
		return vertexInfo.containsKey(v) ? vertexInfo.get(v).state : UNVISITED;
	}

	/**
	 * Sets the traversal state for the given vertex.
	 * 
	 * @param v        vertex
	 * @param newState new vertex state
	 */
	protected void setState(int v, TraversalState newState) {
		BasicSearchInfo info = getOrCreateVertexInfo(v);
		TraversalState oldState = info.state;
		info.state = newState;
		fireVertexStateChanged(v, oldState, newState);
	}

	@Override
	public int getParent(int v) {
		return vertexInfo.containsKey(v) ? vertexInfo.get(v).parent : Graph.NO_VERTEX;
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
		BasicSearchInfo childInfo = getOrCreateVertexInfo(child);
		childInfo.parent = parent;
		if (parent != Graph.NO_VERTEX) {
			childInfo.cost = getCost(parent) + fnEdgeCost.applyAsDouble(parent, child);
			maxCost = Math.max(maxCost, childInfo.cost);
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
		return vertexInfo.containsKey(v) ? vertexInfo.get(v).cost : Path.INFINITE_COST;
	}

	@Override
	public void setCost(int v, double value) {
		getOrCreateVertexInfo(v).cost = value;
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