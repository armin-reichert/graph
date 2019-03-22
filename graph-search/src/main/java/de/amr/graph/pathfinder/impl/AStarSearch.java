package de.amr.graph.pathfinder.impl;

import static de.amr.graph.core.api.TraversalState.COMPLETED;
import static de.amr.graph.core.api.TraversalState.VISITED;

import java.util.HashMap;
import java.util.Map;
import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.pathfinder.impl.queue.MinPQ_VertexQueue;

/**
 * The A* path finder.
 * 
 * <p>
 * Open/closed list and functions f, g, h are realized as:
 * 
 * <pre>
 * g(v) = getCost(v)
 * h(v) = fnEstimatedCost.apply(v, target)
 * f(v) = g(v) + h(v) = getScore(v)
 * Vertex v in open list:   getState(v) == OPEN
 * Vertex v in closed list: getState(v) == CLOSED
 * </pre>
 * 
 * @author Armin Reichert
 * 
 * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">Wikipedia</a>
 * @see <a href="https://www.redblobgames.com/pathfinding/a-star/introduction.html">Amit Patel, Red
 *      Blob Games</a>
 * @see <a href="#">Patrick Henry Winston, Artificial Intelligence, Addison-Wesley, 1984</a>
 */
public class AStarSearch extends AbstractGraphSearch<MinPQ_VertexQueue> {

	public static final TraversalState OPEN = VISITED;
	public static final TraversalState CLOSED = COMPLETED;

	private final ToDoubleBiFunction<Integer, Integer> fnEstimatedCost;
	private final Map<Integer, Double> score;

	/**
	 * Creates an A* path finder instance.
	 * 
	 * @param graph
	 *                          a graph
	 * @param fnEdgeCost
	 *                          function defining the cost for each edge
	 * @param fnEstimatedCost
	 *                          estimated path cost, for example the Euclidean or Manhattan distance for
	 *                          a 2D grid. Must be an underestimate of the real cost.
	 */
	public AStarSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost,
			ToDoubleBiFunction<Integer, Integer> fnEstimatedCost) {
		super(graph, fnEdgeCost);
		frontier = new MinPQ_VertexQueue(this::getScore);
		score = new HashMap<>();
		this.fnEstimatedCost = fnEstimatedCost;
	}

	@Override
	public void init() {
		super.init();
		score.clear();
	}

	@Override
	public void start(int source, int target) {
		this.source = source;
		this.target = target;
		setState(source, OPEN);
		// next two lines only included for consistency:
		setCost(source, 0);
		setScore(source, getEstimatedCost(source));
		frontier.add(source);
	}

	@Override
	protected void expand(int v) {
		graph.adj(v).filter(child -> getState(child) != CLOSED).forEach(child -> {
			double newCost = getCost(v) + fnEdgeCost.applyAsDouble(v, child);
			if (getState(child) != OPEN || newCost < getCost(child)) {
				setParent(child, v);
				setCost(child, newCost);
				setScore(child, newCost + getEstimatedCost(child));
				if (getState(child) == OPEN) {
					frontier.decreaseKey(child);
				}
				else {
					setState(child, OPEN);
					frontier.add(child);
				}
			}
		});
	}

	public double getEstimatedCost(int v) {
		return fnEstimatedCost.applyAsDouble(v, target);
	}

	public double getScore(int v) {
		return score.getOrDefault(v, Double.MAX_VALUE);
	}

	private void setScore(int v, double value) {
		score.put(v, value);
	}
}