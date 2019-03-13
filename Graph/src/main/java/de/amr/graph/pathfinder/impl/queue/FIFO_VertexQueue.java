package de.amr.graph.pathfinder.impl.queue;

import java.util.ArrayDeque;
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
	public int next() {
		return q.remove();
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
}