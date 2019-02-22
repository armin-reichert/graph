package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import de.amr.graph.core.api.Graph;

/**
 * Alternative implementation of depth-first traversal of an undirected graph.
 * <p>
 * This implementation has a nicer visualization.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch2<V, E> extends GraphSearch<V, E> {

	private final Deque<Integer> stack;

	public DepthFirstSearch2(Graph<V, E> graph) {
		super(graph);
		stack = new ArrayDeque<>();
	}

	@Override
	public void exploreGraph(int source, int target) {
		init();

		int current = source;
		stack.push(current);
		setState(current, VISITED);

		while (!stack.isEmpty()) {
			if (current == target) {
				break;
			}
			OptionalInt neighbor = unvisitedChildren(current).findAny();
			if (neighbor.isPresent()) {
				setState(neighbor.getAsInt(), VISITED);
				setParent(neighbor.getAsInt(), current);
				if (unvisitedChildren(neighbor.getAsInt()).findAny().isPresent()) {
					stack.push(neighbor.getAsInt());
				}
				current = neighbor.getAsInt();
			} else {
				setState(current, COMPLETED);
				if (!stack.isEmpty()) {
					current = stack.pop();
				}
				if (getState(current) == VISITED) {
					stack.push(current);
				}
			}
		}
		while (!stack.isEmpty()) {
			setState(stack.pop(), COMPLETED);
		}
	}

	private IntStream unvisitedChildren(int v) {
		return graph.adj(v).filter(child -> getState(child) == UNVISITED);
	}

	@Override
	protected int removeFromFrontier() {
		int v = stack.pop();
		fireVertexRemovedFromFrontier(v);
		return v;
	}

	@Override
	protected void addToFrontier(int v) {
		stack.push(v);
		fireVertexAddedToFrontier(v);
	}

	@Override
	protected boolean isFrontierEmpty() {
		return stack.isEmpty();
	}

	@Override
	public boolean partOfFrontier(int v) {
		return stack.contains(v);
	}
}