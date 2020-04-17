package de.amr.graph.pathfinder.impl;

import static de.amr.graph.core.api.TraversalState.COMPLETED;
import static de.amr.graph.core.api.TraversalState.UNVISITED;
import static de.amr.graph.core.api.TraversalState.VISITED;

import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.AStarSearch.AStarSearchInfo;
import de.amr.graph.pathfinder.impl.queue.MinPQ_VertexQueue;

/**
 * The <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">A*</a>
 * pathfinder.
 * 
 * <p>
 * Open/closed list and functions f, g, h are realized as follows:
 * </p>
 * 
 * <pre>
 * g(v) = getCost(v)
 * h(v) = fnEstimatedCost.apply(v, target)
 * f(v) = g(v) + h(v) = getScore(v)
 * v in open list:   getState(v) == UNVISITED
 * v in closed list: getState(v) == COMPLETED
 * </pre>
 * 
 * @author Armin Reichert
 * 
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/A*_search_algorithm">Wikipedia</a>
 * @see <a href=
 *      "https://www.redblobgames.com/pathfinding/a-star/introduction.html">Amit
 *      Patel, Red Blob Games</a>
 */
public class AStarSearch extends AbstractGraphSearch<MinPQ_VertexQueue, AStarSearchInfo> {

	static class AStarSearchInfo extends BasicSearchInfo {
		public double score;

		@Override
		public String toString() {
			return String.format("(parent:%d,state:%s,cost:%.2g,score:%.2g)", parent, state, cost, score);
		}
	}

	@Override
	protected AStarSearchInfo createVertexInfo(int v) {
		return new AStarSearchInfo();
	}

	private final ToDoubleBiFunction<Integer, Integer> fnEstimatedCost;

	/**
	 * Creates an A* path finder instance.
	 * 
	 * @param graph           the graph to be searched
	 * @param fnEdgeCost      edge cost function e.g. street length between two
	 *                        cities
	 * @param fnEstimatedCost estimated path cost e.g. Euclidean distance between
	 *                        two cities. This must be an <b>underestimate</b> of
	 *                        the real cost.
	 */
	public AStarSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost,
			ToDoubleBiFunction<Integer, Integer> fnEstimatedCost) {
		super(graph, fnEdgeCost);
		this.fnEstimatedCost = fnEstimatedCost;
		this.frontier = new MinPQ_VertexQueue(this::getScore);
	}

	@Override
	public void start(int source, int target) {
		super.start(source, target);
		setScore(source, getEstimatedCost(source));
	}

	@Override
	protected void expand(int v) {
		graph.adj(v).filter(child -> getState(child) != COMPLETED).forEach(child -> {
			double tentativeCost = getCost(v) + fnEdgeCost.applyAsDouble(v, child);
			if (getState(child) == UNVISITED || tentativeCost < getCost(child)) {
				setParent(child, v);
				setCost(child, tentativeCost);
				setScore(child, tentativeCost + getEstimatedCost(child));
				if (getState(child) == UNVISITED) {
					// found first path to the child
					setState(child, VISITED);
					frontier.add(child);
					fireVertexAddedToFrontier(child);
				} else {
					// found better path to the child
					frontier.decreaseKey(child);
				}
			}
		});
	}

	/**
	 * Returns the estimated ("heuristic") cost of the given vertex.
	 * 
	 * @param v vertex
	 * @return the estimated cost ("h"-value) of the vertex
	 */
	public double getEstimatedCost(int v) {
		return fnEstimatedCost.applyAsDouble(v, target);
	}

	/**
	 * Returns the score of the given vertex which determines its priority in the
	 * frontier.
	 * 
	 * @param v vertex
	 * @return the score ("f"-value) of the vertex
	 */
	public double getScore(int v) {
		return vertexInfo.containsKey(v) ? vertexInfo.get(v).score : Path.INFINITE_COST;
	}

	/**
	 * Sets the "score" (f-value) for the given vertex.
	 * 
	 * @param v     vertex
	 * @param score score for this vertex
	 */
	public void setScore(int v, double score) {
		getOrCreateVertexInfo(v).score = score;
	}
}