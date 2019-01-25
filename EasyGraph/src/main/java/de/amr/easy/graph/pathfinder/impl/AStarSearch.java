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
 * Functions f ("score"), g ("distance from source vertex") and h ("heuristic distance to target
 * vertex") are realized as follows:
 * 
 * <pre>
 * f(v) = score[v]
 * g(v) = distFromSource[v]
 * h(v) = fnEstimatedDist(v, target)
 * 
 * The "open list" is realized by the inherited priority queue q
 * 
 * vertex is in closed list <=> getState(vertex) == COMPLETED
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
	private final ToIntBiFunction<Integer, Integer> fnEstimatedDist;
	private final int[] score;

	/**
	 * Creates an A* path finder instance.
	 * 
	 * @param graph
	 *                          a graph
	 * @param fnEdgeCost
	 *                          function giving the cost for each edge
	 * @param fnEstimatedDist
	 *                          heuristics estimating the distance (as an integer) between nodes, for
	 *                          example Euclidean distance squared or the Mahattan distance for grid
	 *                          graphs
	 */
	public AStarSearch(Graph<V, E> graph, Function<E, Integer> fnEdgeCost,
			ToIntBiFunction<Integer, Integer> fnEstimatedDist) {
		this.graph = graph;
		this.fnEdgeCost = fnEdgeCost;
		this.fnEstimatedDist = fnEstimatedDist;
		score = new int[graph.numVertices()];
		distFromSource = new int[graph.numVertices()];
		q = new PriorityQueue<>(comparingInt(this::getScore));
	}

	public int getScore(int v) {
		return score[v];
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
		score[source] = fnEstimatedDist.applyAsInt(source, target);
		addToOpenList(source);

		while (!(q.isEmpty() || q.peek() == target)) {
			int vertex = q.poll();
			addToClosedList(vertex);
			graph.adj(vertex).filter(child -> !inClosedList(child)).forEach(child -> {
				E edgeData = graph.getEdgeLabel(vertex, child);
				int newDist = distFromSource[vertex] + fnEdgeCost.apply(edgeData);
				if (!inOpenList(child) || newDist < distFromSource[child]) {
					distFromSource[child] = newDist;
					score[child] = newDist + fnEstimatedDist.applyAsInt(child, target);
					setParent(child, vertex);
					if (inOpenList(child)) {
						decreaseKey(child);
					} else {
						addToOpenList(child);
					}
				}
			});
		}
	}
}