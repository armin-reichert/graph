package de.amr.graph.event;

import de.amr.graph.core.api.ObservableGraph;

/**
 * Event for edge related changes of a graph.
 * 
 * @author Armin Reichert
 * 
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 */
public class EdgeEvent<V, E> extends GraphEvent<V, E> {

	private final int either;
	private final int other;

	public EdgeEvent(ObservableGraph<V, E> graph, int either, int other) {
		super(graph);
		this.either = either;
		this.other = other;
	}

	public int getEither() {
		return either;
	}

	public int getOther() {
		return other;
	}
}