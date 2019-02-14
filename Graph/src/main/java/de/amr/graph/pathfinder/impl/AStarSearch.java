package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;
import static java.util.Comparator.comparingDouble;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;

import de.amr.graph.core.api.Graph;

/**
 * The A* path finder.
 * 
 * <pre>
 * f(v) = map "score"
 * g(v) = map "cost"
 * h(v) = fnEstimatedCost.apply(v, target)
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
	private final ToDoubleBiFunction<Integer, Integer> fnEstimatedPathCost;
	private final Map<Integer, Double> score;

	/**
	 * Creates an A* path finder instance.
	 * 
	 * @param graph
	 *                              a graph
	 * @param fnEdgeCost
	 *                              function returning the cost for each edge
	 * @param fnEstimatedPathCost
	 *                              estimated path cost, for example the Euclidean or Manhattan distance
	 *                              for a 2D grid. Must be an underestimate of the real cost.
	 */
	public AStarSearch(Graph<V, E> graph, ToDoubleFunction<E> fnEdgeCost,
			ToDoubleBiFunction<Integer, Integer> fnEstimatedPathCost) {
		super(graph);
		this.fnEdgeCost = fnEdgeCost;
		this.fnEstimatedPathCost = fnEstimatedPathCost;
		this.score = new HashMap<>();
	}

	@Override
	protected void init() {
		q = new PriorityQueue<>(comparingDouble(this::getScore));
		score.clear();
		super.init();
	}

	@Override
	public void exploreGraph(int source, int target) {
		init();

		// next two lines only included for consistency
		setCost(source, 0);
		setScore(source, fnEstimatedPathCost.applyAsDouble(source, target));
		open(source);

		while (!q.isEmpty()) {
			int current = q.poll();
			close(current);
			if (current == target) {
				break;
			}
			graph.adj(current).filter(child -> !isClosed(child)).forEach(child -> {
				E edge = graph.getEdgeLabel(current, child);
				double tentativeCost = getCost(current) + fnEdgeCost.applyAsDouble(edge);
				if (!isOpen(child) || tentativeCost < getCost(child)) {
					setParent(child, current);
					setCost(child, tentativeCost);
					setScore(child, tentativeCost + fnEstimatedPathCost.applyAsDouble(child, target));
					if (isOpen(child)) {
						decreaseKey(child);
					} else {
						open(child);
					}
				}
			});
		}
	}

	public double getScore(int v) {
		return score.getOrDefault(v, Double.MAX_VALUE);
	}

	private void setScore(int v, double value) {
		score.put(v, value);
	}

	public boolean isOpen(int v) {
		return getState(v) == VISITED;
	}

	public boolean isClosed(int v) {
		return getState(v) == COMPLETED;
	}

	private void open(int v) {
		setState(v, VISITED);
		q.add(v);
	}

	private void close(int v) {
		setState(v, COMPLETED);
	}

	private void decreaseKey(int v) {
		// PriorityQueue has no "decrease-key" operation
		q.remove(v);
		q.add(v);
	}
}