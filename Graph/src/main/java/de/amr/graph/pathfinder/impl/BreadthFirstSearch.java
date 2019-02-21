package de.amr.graph.pathfinder.impl;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import de.amr.graph.core.api.Graph;

/**
 * Breadth-first search in undirected graph, starting from a given source vertex. After being
 * executed, the cost/distance of each vertex from the source can be queried, as well as the maximal
 * cost/distance of any vertex reachable from the source.
 * 
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 * 
 * @author Armin Reichert
 */
public class BreadthFirstSearch<V, E> extends GraphSearch<V, E> {

	protected Queue<Integer> frontier;
	protected final Map<Integer, Double> cost;
	protected double maxCost;

	public BreadthFirstSearch(Graph<V, E> graph) {
		super(graph);
		frontier = new ArrayDeque<Integer>();
		cost = new HashMap<>();
	}

	@Override
	protected void init() {
		super.init();
		frontier.clear();
		cost.clear();
		maxCost = -1;
	}

	@Override
	protected void setParent(int child, int parent) {
		super.setParent(child, parent);
		if (parent != -1) {
			setCost(child, getCost(parent) + 1);
			maxCost = Math.max(maxCost, getCost(child));
		} else {
			setCost(child, 0);
			maxCost = 0;
		}
	}

	@Override
	protected int removeFromFrontier() {
		return frontier.poll();
	}

	@Override
	protected void addToFrontier(int v) {
		frontier.add(v);
	}

	@Override
	protected boolean isFrontierEmpty() {
		return frontier.isEmpty();
	}

	@Override
	public boolean partOfFrontier(int v) {
		return frontier.contains(v);
	}

	/**
	 * Returns the cost of the given vertex.
	 * 
	 * @param v
	 *            vertex
	 * @return vertex cost
	 */
	public double getCost(int v) {
		return cost.getOrDefault(v, -1d);
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
		cost.put(v, value);
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
}