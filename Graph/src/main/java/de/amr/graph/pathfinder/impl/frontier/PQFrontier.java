package de.amr.graph.pathfinder.impl.frontier;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.ToDoubleFunction;

import de.amr.graph.pathfinder.api.Frontier;

public class PQFrontier implements Frontier {

	private final PriorityQueue<Integer> pq;

	public PQFrontier(ToDoubleFunction<Integer> fnVertexPriority) {
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
}