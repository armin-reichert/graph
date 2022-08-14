package de.amr.graph.core.api;

/**
 * An edge with comparable edge label. Can be used for example in priority queues.
 * 
 * @author Armin Reichert
 *
 * @param <W> edge weight type
 */
public record WeightedEdge<W extends Comparable<W>> (int either, int other, W weight)
		implements Edge, Comparable<WeightedEdge<W>> {

	@Override
	public int compareTo(WeightedEdge<W> other) {
		if (weight == null) {
			return other.weight == null ? 0 : 1;
		}
		return weight.compareTo(other.weight);
	}
}