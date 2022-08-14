package de.amr.graph.core.api;

import java.util.Objects;

/**
 * Undirected edge implementation. Two undirected edges are equal if their vertex sets are equal.
 * 
 * @author Armin Reichert
 */
public class UndirectedEdge implements Edge {

	private final int either;
	private final int other;

	public UndirectedEdge(int either, int other) {
		this.either = either;
		this.other = other;
	}

	@Override
	public int either() {
		return either;
	}

	@Override
	public int other() {
		return other;
	}

	@Override
	public int hashCode() {
		int min = Math.min(either, other);
		int max = Math.max(either, other);
		return Objects.hash(min, max);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UndirectedEdge e2 = (UndirectedEdge) obj;
		return either == e2.either && this.other == e2.other || either == e2.other && other == e2.either;
	}
}