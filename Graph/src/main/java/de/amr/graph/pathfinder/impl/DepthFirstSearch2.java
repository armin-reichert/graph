package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import java.util.OptionalInt;
import java.util.stream.IntStream;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.api.Frontier;

/**
 * Alternative implementation of depth-first traversal of an undirected graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch2<V, E> extends GraphSearch<V, E> {

	private final LIFOFrontier frontier = new LIFOFrontier();

	public DepthFirstSearch2(Graph<V, E> graph) {
		super(graph);
	}

	@Override
	public Frontier frontier() {
		return frontier;
	}

	@Override
	public void exploreGraph(int source, int target) {
		init();

		int current = source;
		frontier.add(current);
		setState(current, VISITED);

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
				}
				current = neighbor;
			} else {
				setState(current, COMPLETED);
				if (!frontier.isEmpty()) {
					current = frontier.next();
				}
				if (getState(current) == VISITED) {
					frontier.add(current);
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