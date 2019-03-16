package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.api.VertexQueue;

/**
 * Base class for graph search algorithms.
 * <p>
 * Stores the traversal state, parent link and cost for each vertex. Also supports registration of
 * observers for vertex and edge traversals and for changes of the search queue (frontier).
 * 
 * @author Armin Reichert
 */
public abstract class GraphSearch<V, E, Q extends VertexQueue> {

	protected final Graph<V, E> graph;
	protected final Map<Integer, Integer> parentMap;
	protected final Map<Integer, TraversalState> stateMap;
	protected final Map<Integer, Double> costMap;
	protected final Set<GraphSearchObserver> observers;
	protected final ToDoubleBiFunction<Integer, Integer> fnEdgeCost;
	protected double maxCost;
	protected Q frontier;
	protected int current, source, target;

	protected GraphSearch(Graph<V, E> graph) {
		this(graph, (u, v) -> 1);
	}

	protected GraphSearch(Graph<V, E> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost) {
		this.graph = Objects.requireNonNull(graph);
		this.parentMap = new HashMap<>();
		this.stateMap = new HashMap<>();
		this.costMap = new HashMap<>();
		this.observers = new HashSet<>(5);
		this.fnEdgeCost = fnEdgeCost;
	}

	/**
	 * Initializes the search such that {@link #exploreGraph(int, int)} always starts in a clean state.
	 */
	public void init() {
		parentMap.clear();
		stateMap.clear();
		costMap.clear();
		frontier.clear();
		maxCost = 0;
		current = source = target = -1;
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
	 * 
	 * @return {@code true} if the target has been found
	 */
	public boolean exploreGraph(int source, int target) {
		init();
		start(source, target);
		while (canExplore()) {
			if (exploreVertex()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tells if there is some vertex left to explore.
	 * 
	 * @return {@code true} if there is some vertex left to explore
	 */
	public boolean canExplore() {
		return !frontier.isEmpty();
	}

	/**
	 * Explores the next vertex.
	 * 
	 * @return {@code true} if the target has been found
	 */
	public boolean exploreVertex() {
		current = frontier.next();
		setState(current, COMPLETED);
		fireVertexRemovedFromFrontier(current);
		if (current == target) {
			return true;
		}
		expand(current);
		return false;
	}

	/**
	 * Start the search. Subclasses may modify this.
	 */
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

	/**
	 * Returns the vertex currently being processed.
	 * 
	 * @return the current vertex
	 */
	public int getCurrent() {
		return current;
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
			setCost(child, getCost(parent) + fnEdgeCost.applyAsDouble(parent, child));
			maxCost = Math.max(maxCost, getCost(child));
		} else {
			setCost(child, 0);
			maxCost = 0;
		}
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

	/**
	 * Returns the cost of the given vertex.
	 * 
	 * @param v
	 *            vertex
	 * @return vertex cost
	 */
	public double getCost(int v) {
		return costMap.getOrDefault(v, Path.INFINITE_COST);
	}

	/**
	 * Sets the cost for the given vertex.
	 * 
	 * @param v
	 *                vertex
	 * @param value
	 *                cost value
	 */
	public void setCost(int v, double value) {
		costMap.put(v, value);
	}

	/**
	 * Returns the maximum cost/distance of any vertex reachable from the source.
	 * 
	 * @return the maximum distance
	 */
	public double getMaxCost() {
		return maxCost;
	}

	/**
	 * Returns a vertex with maximum distance encountered in this traversal.
	 * 
	 * @return a vertex with maximum distance or empty
	 */
	public Optional<Integer> getMaxCostVertex() {
		return graph.vertices().boxed().max(Comparator.comparing(this::getCost));
	}

	// Observer related stuff

	public void addObserver(GraphSearchObserver observer) {
		Objects.requireNonNull(observer);
		observers.add(observer);
	}

	public void removeObserver(GraphSearchObserver observer) {
		Objects.requireNonNull(observer);
		observers.remove(observer);
	}

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
		observers.forEach(observer -> observer.vertexAddedToFrontier(vertex));
	}

	protected void fireVertexStateChanged(int vertex, TraversalState oldState, TraversalState newState) {
		if (oldState != newState) {
			observers.forEach(observer -> observer.vertexStateChanged(vertex, oldState, newState));
		}
	}
}