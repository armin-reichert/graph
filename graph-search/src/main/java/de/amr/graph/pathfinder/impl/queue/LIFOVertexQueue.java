package de.amr.graph.pathfinder.impl.queue;

import java.util.ArrayDeque;
import java.util.OptionalInt;

import de.amr.graph.pathfinder.api.VertexQueue;

/**
 * Stack of vertices.
 * 
 * @author Armin Reichert
 */
public class LIFOVertexQueue implements VertexQueue {

	private final ArrayDeque<Integer> stack = new ArrayDeque<>();

	@Override
	public void add(int v) {
		stack.push(v);
	}

	@Override
	public int poll() {
		return stack.pop();
	}

	@Override
	public OptionalInt peek() {
		return stack.isEmpty() ? OptionalInt.empty() : OptionalInt.of(stack.peek());
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

	@Override
	public String toString() {
		return stack.toString();
	}
}