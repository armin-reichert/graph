package de.amr.graph.pathfinder.impl;

import static de.amr.graph.core.api.TraversalState.COMPLETED;
import static de.amr.graph.core.api.TraversalState.UNVISITED;
import static de.amr.graph.core.api.TraversalState.VISITED;

import java.util.Optional;
import java.util.stream.Stream;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.queue.LIFOVertexQueue;

/**
 * Alternative implementation of depth-first traversal of an undirected graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch2 extends AbstractGraphSearch {

	public DepthFirstSearch2(Graph<?, ?> graph) {
		super(graph);
		frontier = new LIFOVertexQueue();
	}

	@Override
	public Path findPath(int sourceVertex, int targetVertex) {
		start(sourceVertex, targetVertex);
		current = sourceVertex;
		boolean found = false;
		while (!frontier.isEmpty()) {
			if (current == targetVertex) {
				found = true;
				break;
			}
			Optional<Integer> neighborIfAny = unvisitedChildren(current).findAny();
			if (neighborIfAny.isPresent()) {
				int neighbor = neighborIfAny.get();
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
					current = frontier.poll();
					fireVertexRemovedFromFrontier(current);
				}
				if (getState(current) == VISITED) {
					frontier.add(current);
					fireVertexAddedToFrontier(current);
				}
			}
		}
		while (!frontier.isEmpty()) {
			setState(frontier.poll(), COMPLETED);
		}
		return found ? buildPath(targetVertex) : Path.NULL;
	}

	private Stream<Integer> unvisitedChildren(int v) {
		return graph.adj(v).filter(child -> getState(child) == UNVISITED);
	}
}