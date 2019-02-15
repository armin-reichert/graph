package de.amr.graph.pathfinder.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

import de.amr.graph.core.api.Graph;

/**
 * Depth-first traversal of a graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch<V, E> extends GraphSearch<V, E> {

	protected Deque<Integer> stack;

	public DepthFirstSearch(Graph<V, E> graph) {
		super(graph);
	}

	@Override
	protected void init() {
		super.init();
		stack = (Deque<Integer>) createFrontier();
	}

	@Override
	protected Queue<Integer> createFrontier() {
		return new ArrayDeque<>();
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
		return stack != null && stack.contains(v);
	}

	@Override
	public double getCost(int v) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCost(int v, double value) {
		throw new UnsupportedOperationException();
	}
}