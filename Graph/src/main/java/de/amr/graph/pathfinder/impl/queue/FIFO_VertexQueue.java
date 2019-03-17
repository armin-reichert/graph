package de.amr.graph.pathfinder.impl.queue;

import java.util.ArrayDeque;
import java.util.OptionalInt;
import java.util.Queue;

import de.amr.graph.pathfinder.api.VertexQueue;

/**
 * FIFO vertex queue.
 * 
 * @author Armin Reichert
 */
public class FIFO_VertexQueue implements VertexQueue {

	private final Queue<Integer> q = new ArrayDeque<>();

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
		return q.isEmpty() ? OptionalInt.empty() : OptionalInt.of(q.peek());
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