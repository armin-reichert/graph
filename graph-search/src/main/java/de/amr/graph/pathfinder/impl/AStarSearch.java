package de.amr.graph.pathfinder.impl;

import static de.amr.graph.core.api.TraversalState.COMPLETED;
import static de.amr.graph.core.api.TraversalState.UNVISITED;
import static de.amr.graph.core.api.TraversalState.VISITED;

import java.util.function.ToDoubleBiFunction;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.queue.MinPQVertexQueue;

/**
 * A* search.
 * 
 * <p>
 * Open/closed list and f, g, h are realized as follows:
 * </p>
 * 
 * <pre>
 * f(v) = g(v) + h(v) = getScore(v)
 * g(v) = getCost(v)
 * h(v) = getEstimatedCost(v) = fnEstimatedCost.apply(v, target)
 * 
 * v in open list:   getState(v) == UNVISITED
 * v in closed list: getState(v) == COMPLETED
 * </pre>
 * 
 * @author Armin Reichert
 */
public class AStarSearch extends AbstractGraphSearch {

	static class AStarSearchInfo extends BasicSearchInfo {
		public double score;

		@Override
		public String toString() {
			return String.format("[parent: %d, state:%s,cost: %.2f, score: %.2f]", parent, state, cost, score);
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
	 * @param fnEdgeCost      edge cost function e.g. street length between two cities
	 * @param fnEstimatedCost estimated path cost e.g. Euclidean distance between two cities. This must be an
	 *                        <b>underestimate</b> of the real cost.
	 */
	public AStarSearch(Graph<?, ?> graph, ToDoubleBiFunction<Integer, Integer> fnEdgeCost,
			ToDoubleBiFunction<Integer, Integer> fnEstimatedCost) {
		super(graph);
		this.frontier = new MinPQVertexQueue(this::getScore);
		this.fnEdgeCost = fnEdgeCost;
		this.fnEstimatedCost = fnEstimatedCost;
	}

	@Override
	public void start(int sourceVertex, int targetVertex) {
		super.start(sourceVertex, targetVertex);
		setScore(sourceVertex, getEstimatedCostToTarget(sourceVertex));
	}

	@Override
	protected void expand(int v) {
		graph.adj(v).filter(child -> getState(child) != COMPLETED).forEach(neighbor -> {
			double tentativeCost = getCost(v) + fnEdgeCost.applyAsDouble(v, neighbor);
			if (getState(neighbor) == UNVISITED || tentativeCost < getCost(neighbor)) {
				setParent(neighbor, v);
				setCost(neighbor, tentativeCost);
				setScore(neighbor, tentativeCost + getEstimatedCostToTarget(neighbor));
				if (getState(neighbor) == UNVISITED) {
					// found first path
					setState(neighbor, VISITED);
					frontier.add(neighbor);
					fireVertexAddedToFrontier(neighbor);
				} else {
					// found better path
					frontier.decreaseKey(neighbor);
				}
			}
		});
	}

	/**
	 * Returns the estimated ("heuristic") cost of the given vertex (distance to target).
	 * 
	 * @param v vertex
	 * @return the estimated cost ("h"-value) of the vertex
	 */
	public double getEstimatedCostToTarget(int v) {
		return fnEstimatedCost.applyAsDouble(v, target);
	}

	/**
	 * Returns the score of the given vertex which determines its priority in the frontier.
	 * 
	 * @param v vertex
	 * @return the score ("f"-value) of the vertex
	 */
	public double getScore(int v) {
		if (!vertexInfoMap.containsKey(v)) {
			return Path.INFINITE_COST;
		}
		return ((AStarSearchInfo) vertexInfoMap.get(v)).score;
	}

	/**
	 * Sets the "score" (f-value) for the given vertex.
	 * 
	 * @param v     vertex
	 * @param score score for this vertex
	 */
	public void setScore(int v, double score) {
		((AStarSearchInfo) getOrCreateVertexInfo(v)).score = score;
	}
}