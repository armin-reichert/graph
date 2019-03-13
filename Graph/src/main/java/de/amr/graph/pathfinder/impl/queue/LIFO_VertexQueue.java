package de.amr.graph.pathfinder.impl.queue;

import java.util.ArrayDeque;

import de.amr.graph.pathfinder.api.VertexQueue;

/**
 * Stack of vertices.
 * 
 * @author Armin Reichert
 */
public class LIFO_VertexQueue implements VertexQueue {

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