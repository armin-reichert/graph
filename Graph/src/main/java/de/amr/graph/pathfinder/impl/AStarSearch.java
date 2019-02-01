package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;
import static java.util.Comparator.comparingDouble;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;

import de.amr.graph.core.api.Graph;

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
 * h(v) = fnEstimatedCost.apply(v, target)
 * 
 * "v in open list": getState(v) == VISITED && priority queue q contains v
 * "v in closed list: getState(v) == COMPLETED
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

	private final ToDoubleFunction<E> fnEdgeCost;
	private final ToDoubleBiFunction<Integer, Integer> fnEstimatedCost;
	private final double[] score;

	/**
	 * Creates an A* path finder instance.
	 * 
	 * @param graph
	 *                          a graph
	 * @param fnEdgeCost
	 *                          function returning the cost for each edge
	 * @param fnEstimatedCost
	 *                          cost estimate between two vertices, for example the Euclidean or
	 *                          Manhattan distance for a 2D grid
	 */
	public AStarSearch(Graph<V, E> graph, ToDoubleFunction<E> fnEdgeCost,
			ToDoubleBiFunction<Integer, Integer> fnEstimatedCost) {
		super(graph);
		this.q = new PriorityQueue<>(comparingDouble(this::getScore));
		this.fnEdgeCost = fnEdgeCost;
		this.fnEstimatedCost = fnEstimatedCost;
		this.score = new double[graph.numVertices()];
		this.cost = new double[graph.numVertices()];
	}

	@Override
	public void traverseGraph(int source, int target) {
		init();
		Arrays.fill(score, Integer.MAX_VALUE);
		Arrays.fill(cost, Integer.MAX_VALUE);
		cost[source] = 0;
		score[source] = cost[source] + fnEstimatedCost.applyAsDouble(source, target);
		setOpen(source);
		while (!q.isEmpty()) {
			int current = q.poll();
			setClosed(current);
			if (current == target) {
				break;
			}
			graph.adj(current).filter(child -> !isClosed(child)).forEach(child -> {
				E edge = graph.getEdgeLabel(current, child);
				double newCostToChild = cost[current] + fnEdgeCost.applyAsDouble(edge);
				if (!isOpen(child) || newCostToChild < cost[child]) {
					cost[child] = newCostToChild;
					score[child] = cost[child] + fnEstimatedCost.applyAsDouble(child, target);
					if (!isOpen(child)) {
						setOpen(child);
					} else {
						decreaseKey(child);
					}
					setParent(child, current);
				}
			});
		}
	}

	public double getScore(int v) {
		return score[v];
	}

	public boolean isOpen(int v) {
		return getState(v) == VISITED;
	}

	public boolean isClosed(int v) {
		return getState(v) == COMPLETED;
	}

	private void setOpen(int v) {
		setState(v, VISITED);
		q.add(v);
	}

	private void setClosed(int v) {
		setState(v, COMPLETED);
	}

	private void decreaseKey(int v) {
		// Used PriorityQueue has no "decrease-key" operation
		q.remove(v);
		q.add(v);
	}
}