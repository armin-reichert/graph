package de.amr.graph.pathfinder.impl;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.ToDoubleFunction;

class PQFrontier extends FIFOFrontier {

	public PQFrontier(ToDoubleFunction<Integer> fnVertexPriority) {
		q = new PriorityQueue<>(Comparator.comparingDouble(fnVertexPriority));
	}

	public void decreaseKey(int v) {
		q.remove(v);
		q.add(v);
	}
}
