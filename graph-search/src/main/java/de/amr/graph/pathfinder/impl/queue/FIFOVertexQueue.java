package de.amr.graph.pathfinder.impl.queue;

import java.util.ArrayDeque;
import java.util.OptionalInt;

import de.amr.graph.pathfinder.api.VertexQueue;

/**
 * FIFO vertex queue.
 * 
 * @author Armin Reichert
 */
public class FIFOVertexQueue implements VertexQueue {

	private final ArrayDeque<Integer> q = new ArrayDeque<>();

	@Override
	public void add(int v) {
		q.add(v);
	}

	@Override
	public int poll() {
		return q.remove();
	}

	@Override
	public OptionalInt peek() {
		if (q.isEmpty()) {
			return OptionalInt.empty();
		}
		return q.peek() != null ? OptionalInt.of(q.peek()) : OptionalInt.empty();
	}

	@Override
	public boolean isEmpty() {
		return q.isEmpty();
	}

	@Override
	public boolean contains(int v) {
		return q.contains(v);
	}

	@Override
	public void clear() {
		q.clear();
	}

	@Override
	public String toString() {
		return q.toString();
	}
}