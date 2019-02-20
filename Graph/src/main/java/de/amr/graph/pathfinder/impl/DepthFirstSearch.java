package de.amr.graph.pathfinder.impl;

import java.util.ArrayDeque;
import java.util.Deque;

import de.amr.graph.core.api.Graph;

/**
 * Depth-first traversal of a graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch<V, E> extends GraphSearch<V, E> {

	private final Deque<Integer> stack;

	public DepthFirstSearch(Graph<V, E> graph) {
		super(graph);
		stack = new ArrayDeque<>();
	}

	@Override
	protected void init() {
		super.init();
		stack.clear();
	}

	@Override
	protected int removeFromFrontier() {
		return stack.pop();
	}

	@Override
	protected void addToFrontier(int v) {
		stack.push(v);
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