package de.amr.graph.pathfinder.impl.frontier;

import java.util.ArrayDeque;

import de.amr.graph.pathfinder.api.Frontier;

public class LIFOFrontier implements Frontier {

	private final ArrayDeque<Integer> stack = new ArrayDeque<>();

	@Override
	public void add(int v) {
		stack.push(v);
	}

	@Override
	public int next() {
		return stack.pop();
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public boolean contains(int v) {
		return stack.contains(v);
	}

	@Override
	public void clear() {
		stack.clear();
	}
}