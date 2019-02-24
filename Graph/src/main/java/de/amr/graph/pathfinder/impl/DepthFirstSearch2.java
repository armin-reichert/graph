package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import java.util.OptionalInt;
import java.util.stream.IntStream;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.impl.frontier.LIFOFrontier;

/**
 * Alternative implementation of depth-first traversal of an undirected graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch2<V, E> extends GraphSearch<V, E> {

	public DepthFirstSearch2(Graph<V, E> graph) {
		super(graph);
		frontier = new LIFOFrontier();
	}

	@Override
	public void exploreGraph(int source, int target) {
		init();

		setState(source, VISITED);
		frontier.add(source);
		fireVertexAddedToFrontier(source);

		int current = source;
		while (!frontier.isEmpty()) {
			if (current == target) {
				break;
			}
			OptionalInt neighborIfAny = unvisitedChildren(current).findAny();
			if (neighborIfAny.isPresent()) {
				int neighbor = neighborIfAny.getAsInt();
				setState(neighbor, VISITED);
				setParent(neighbor, current);
				if (unvisitedChildren(neighbor).findAny().isPresent()) {
					frontier.add(neighbor);
					fireVertexAddedToFrontier(neighbor);
				}
				current = neighbor;
			} else {
				setState(current, COMPLETED);
				if (!frontier.isEmpty()) {
					current = frontier.next();
					fireVertexRemovedFromFrontier(current);
				}
				if (getState(current) == VISITED) {
					frontier.add(current);
					fireVertexAddedToFrontier(current);
				}
			}
		}
		while (!frontier.isEmpty()) {
			setState(frontier.next(), COMPLETED);
		}
	}

	private IntStream unvisitedChildren(int v) {
		return graph.adj(v).filter(child -> getState(child) == UNVISITED);
	}
}