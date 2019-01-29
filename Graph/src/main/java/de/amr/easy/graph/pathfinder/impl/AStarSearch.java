package de.amr.easy.graph.pathfinder.impl;

import static de.amr.easy.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.easy.graph.pathfinder.api.TraversalState.VISITED;
import static java.util.Comparator.comparingInt;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.ToIntBiFunction;

import de.amr.easy.graph.core.api.Graph;

/**
 * The A* path finder.
 * 
 * <p>
 * Functions f ("score"), g ("cost from source") and h ("heuristic cost to target vertex") are
 * realized as follows:
 * 
 * <pre>
 * f(v) = score[v]
 * g(v) = costFromSource[v]
 * h(v) = fnEstimatedCost(v, target)
 * 
 * "open list": priority queue q
 * "closed list: vertex is in closed list <=> getState(vertex) == COMPLETED
 * </pre>
 * 
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 * 
 * @author Armin Reichert
 * 
 * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">Wikipedia</a>
 * @see <a href="#">Patrick Henry Winston, Artificial Intelligence, Addison-Wesley, 1984</a>
 */
public class AStarSearch<V, E> extends BreadthFirstSearch<V, E> {

	private final Function<E, Integer> fnEdgeCost;
	private final ToIntBiFunction<Integer, Integer> fnEstimatedCost;
	private final int[] score;

	/**
	 * Creates an A* path finder instance.
	 * 
	 * @param graph
	 *                          a graph
	 * @param fnEdgeCost
	 *                          function giving the cost for each edge
	 * @param fnEstimatedDist
	 *                          heuristics estimating the distance between nodes, for example Euclidean
	 *                          Manhattan distance
	 */
	public AStarSearch(Graph<V, E> graph, Function<E, Integer> fnEdgeCost,
			ToIntBiFunction<Integer, Integer> fnEstimatedDist) {
		this.graph = graph;
		this.q = new PriorityQueue<>(comparingInt(this::getScore));
		this.fnEdgeCost = fnEdgeCost;
		this.fnEstimatedCost = fnEstimatedDist;
		this.score = new int[graph.numVertices()];
		this.distFromSource = new int[graph.numVertices()];
	}

	public int getScore(int vertex) {
		return score[vertex];
	}

	public boolean inClosedList(int vertex) {
		return getState(vertex) == COMPLETED;
	}

	private void addToClosedList(int vertex) {
		setState(vertex, COMPLETED);
	}

	public boolean inOpenList(int vertex) {
		return getState(vertex) == VISITED;
	}

	private void addToOpenList(int vertex) {
		setState(vertex, VISITED);
		q.add(vertex);
	}

	private void decreaseKey(int vertex) {
		// PriorityQueue class has no "decrease-key" operation, therefore remove and add again
		q.remove(vertex);
		q.add(vertex);
	}

	@Override
	public void traverseGraph(int source, int target) {
		init();
		Arrays.fill(score, Integer.MAX_VALUE);
		Arrays.fill(distFromSource, Integer.MAX_VALUE);

		distFromSource[source] = 0;
		score[source] = fnEstimatedCost.applyAsInt(source, target);
		addToOpenList(source);

		while (!(q.isEmpty() || q.peek() == target)) {
			int vertex = q.poll();
			addToClosedList(vertex);
			graph.adj(vertex).filter(child -> !inClosedList(child)).forEach(child -> {
				E edgeData = graph.getEdgeLabel(vertex, child);
				int dist = distFromSource[vertex] + fnEdgeCost.apply(edgeData);
				if (!inOpenList(child) || dist < distFromSource[child]) {
					distFromSource[child] = dist;
					score[child] = dist + fnEstimatedCost.applyAsInt(child, target);
					if (!inOpenList(child)) {
						addToOpenList(child);
					} else {
						decreaseKey(child);
					}
					setParent(child, vertex);
				}
			});
		}
	}
}