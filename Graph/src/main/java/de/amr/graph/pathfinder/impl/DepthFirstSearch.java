package de.amr.graph.pathfinder.impl;

import de.amr.datastruct.Stack;
import de.amr.graph.core.api.Graph;

/**
 * Depth-first traversal of a graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch<V, E> extends AbstractSearch<V, E> {

	protected Stack<Integer> stack;

	public DepthFirstSearch(Graph<V, E> graph) {
		super(graph);
		stack = new Stack<>();
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