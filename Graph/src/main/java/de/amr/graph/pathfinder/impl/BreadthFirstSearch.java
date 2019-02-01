package de.amr.graph.pathfinder.impl;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
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
public class BreadthFirstSearch<V, E> extends AbstractSearch<V, E> {

	protected Queue<Integer> q;
	protected int[] cost;
	protected int maxCost;

	public BreadthFirstSearch(Graph<V, E> graph) {
		this(graph, new ArrayDeque<>());
	}

	protected BreadthFirstSearch(Graph<V, E> graph, Queue<Integer> q) {
		super(graph);
		this.q = q;
		this.cost = new int[graph.numVertices()];
	}

	@Override
	protected void init() {
		super.init();
		q.clear();
		Arrays.fill(cost, -1);
		maxCost = -1;
	}

	@Override
	protected void setParent(int child, int parent) {
		super.setParent(child, parent);
		if (parent != -1) {
			cost[child] = cost[parent] + 1;
			maxCost = Math.max(maxCost, cost[child]);
		} else {
			cost[child] = maxCost = 0;
		}
	}

	@Override
	protected int dequeue() {
		return q.poll();
	}

	@Override
	protected void enqueue(int v) {
		q.add(v);
	}

	@Override
	protected boolean isQueueEmpty() {
		return q.isEmpty();
	}

	@Override
	public boolean inQueue(int v) {
		return q.contains(v);
	}

	/**
	 * The cost/distance of the given vertex from the source.
	 * 
	 * @param v
	 *            some vertex
	 * @return the distance from the source or {@code -1} if the vertex is not reachable
	 */
	public int getCost(int v) {
		return cost[v];
	}

	/**
	 * Returns the maximum cost/distance of any vertex reachable from the source.
	 * 
	 * @return the maximum distance
	 */
	public int getMaxCost() {
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