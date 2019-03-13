package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ToDoubleBiFunction;
import java.util.function.ToDoubleFunction;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.queue.MinPQ_VertexQueue;

/**
 * The A* path finder.
 * 
 * <pre>
 * f(v):             getScore(v)
 * g(v):             getCost(v)
 * h(v):             fnEstimatedCost.apply(v, target)
 * v in open list:   getState(v) == OPEN
 * v in closed list: getState(v) == CLOSED
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
public class AStarSearch<V, E> extends GraphSearch<V, E, MinPQ_VertexQueue> {

	public static final TraversalState OPEN = VISITED;
	public static final TraversalState CLOSED = COMPLETED;

	private final ToDoubleFunction<E> fnEdgeCost;
	private final ToDoubleBiFunction<Integer, Integer> fnEstimatedPathCost;
	private final Map<Integer, Double> score;

	/**
	 * Creates an A* path finder instance.
	 * 
	 * @param graph
	 *                              a graph
	 * @param fnEdgeCost
	 *                              function defining the cost for each edge
	 * @param fnEstimatedPathCost
	 *                              estimated path cost, for example the Euclidean or Manhattan distance
	 *                              for a 2D grid. Must be an underestimate of the real cost.
	 */
	public AStarSearch(Graph<V, E> graph, ToDoubleFunction<E> fnEdgeCost,
			ToDoubleBiFunction<Integer, Integer> fnEstimatedPathCost) {
		super(graph);
		frontier = new MinPQ_VertexQueue(this::getScore);
		score = new HashMap<>();
		this.fnEdgeCost = fnEdgeCost;
		this.fnEstimatedPathCost = fnEstimatedPathCost;
	}

	@Override
	public void init() {
		super.init();
		score.clear();
	}

	@Override
	public void exploreGraph(int source, int target) {
		init();
		setState(source, OPEN);
		// next two lines only included for consistency:
		setCost(source, 0);
		setScore(source, fnEstimatedPathCost.applyAsDouble(source, target));
		frontier.add(source);
		while (!frontier.isEmpty()) {
			int current = frontier.next();
			setState(current, CLOSED);
			if (current == target) {
				break;
			}
			graph.adj(current).filter(child -> getState(child) != CLOSED).forEach(child -> {
				double newCost = getCost(current) + edgeCost(current, child);
				if (getState(child) != OPEN || newCost < getCost(child)) {
					setParent(child, current);
					setCost(child, newCost);
					setScore(child, newCost + fnEstimatedPathCost.applyAsDouble(child, target));
					if (getState(child) == OPEN) {
						((MinPQ_VertexQueue) frontier).decreaseKey(child);
					} else {
						setState(child, OPEN);
						frontier.add(child);
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

	private double edgeCost(int u, int v) {
		return fnEdgeCost.applyAsDouble(graph.getEdgeLabel(u, v));
	}
}