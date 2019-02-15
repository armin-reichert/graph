package de.amr.graph.event.api;

import de.amr.graph.core.api.ObservableGraph;

/**
 * Graph event base class.
 * 
 * @author Armin Reichert
 *
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 */
public class GraphEvent<V, E> {

	protected final ObservableGraph<V, E> graph;

	public GraphEvent(ObservableGraph<V, E> graph) {
		this.graph = graph;
	}

	public ObservableGraph<V, E> getGraph() {
		return graph;
	}
}