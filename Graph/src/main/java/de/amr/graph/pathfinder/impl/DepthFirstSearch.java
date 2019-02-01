package de.amr.graph.pathfinder.impl;

import java.util.ArrayDeque;
import java.util.Deque;

import de.amr.graph.core.api.Graph;

/**
 * Depth-first traversal of a graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch<V, E> extends AbstractSearch<V, E> {

	protected final Deque<Integer> stack = new ArrayDeque<>();

	public DepthFirstSearch(Graph<V, E> graph) {
		super(graph);
	}

	@Override
	protected void init() {
		super.init();
		stack.clear();
	}

	@Override
	protected int dequeue() {
		return stack.pop();
	}

	@Override
	protected void enqueue(int v) {
		stack.push(v);
	}

	@Override
	protected boolean isQueueEmpty() {
		return stack.isEmpty();
	}

	@Override
	public boolean inQueue(int v) {
		return stack.contains(v);
	}
}