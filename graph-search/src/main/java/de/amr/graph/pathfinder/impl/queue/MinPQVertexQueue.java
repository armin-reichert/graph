package de.amr.graph.pathfinder.impl.queue;

import java.util.Comparator;
import java.util.OptionalInt;
import java.util.PriorityQueue;
import java.util.function.ToDoubleFunction;

import de.amr.graph.pathfinder.api.VertexQueue;

/**
 * (Min-)Priority queue of vertices.
 * 
 * @author Armin Reichert
 */
public class MinPQVertexQueue implements VertexQueue {

	private final PriorityQueue<Integer> vertexQ;

	public MinPQVertexQueue(ToDoubleFunction<Integer> fnVertexPriority) {
		vertexQ = new PriorityQueue<>(Comparator.comparingDouble(fnVertexPriority));
	}

	public void decreaseKey(int v) {
		vertexQ.remove(v);
		vertexQ.add(v);
	}

	@Override
	public void add(int v) {
		vertexQ.offer(v);
	}

	@Override
	public int poll() {
		return vertexQ.remove();
	}

	@Override
	public OptionalInt peek() {
		return vertexQ.isEmpty() ? OptionalInt.empty() : OptionalInt.of(vertexQ.peek());
	}

	@Override
	public boolean isEmpty() {
		return vertexQ.isEmpty();
	}

	@Override
	public boolean contains(int v) {
		return vertexQ.contains(v);
	}

	@Override
	public void clear() {
		vertexQ.clear();
	}

	@Override
	public String toString() {
		return vertexQ.toString();
	}
}