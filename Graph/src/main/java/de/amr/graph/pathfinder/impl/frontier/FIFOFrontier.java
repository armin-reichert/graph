package de.amr.graph.pathfinder.impl.frontier;

import java.util.ArrayDeque;
import java.util.Queue;

import de.amr.graph.pathfinder.api.Frontier;

public class FIFOFrontier implements Frontier {

	protected Queue<Integer> q = new ArrayDeque<>();

	@Override
	public void add(int v) {
		q.add(v);
	}

	@Override
	public int next() {
		return q.poll();
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