package de.amr.graph.pathfinder.impl.queue;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.ToDoubleFunction;

import de.amr.graph.pathfinder.api.VertexQueue;

/**
 * (Min-)Priority queue of vertices.
 * 
 * @author Armin Reichert
 */
public class MinPQ_VertexQueue implements VertexQueue {

	private final PriorityQueue<Integer> pq;

	public MinPQ_VertexQueue(ToDoubleFunction<Integer> fnVertexPriority) {
		pq = new PriorityQueue<>(Comparator.comparingDouble(fnVertexPriority));
	}

	public void decreaseKey(int v) {
		pq.remove(v);
		pq.add(v);
	}

	@Override
	public void add(int v) {
		pq.offer(v);
	}

	@Override
	public int next() {
		return pq.remove();
	}

	@Override
	public boolean isEmpty() {
		return pq.isEmpty();
	}

	@Override
	public boolean contains(int v) {
		return pq.contains(v);
	}

	@Override
	public void clear() {
		pq.clear();
	}

	@Override
	public String toString() {
		return pq.toString();
	}
}