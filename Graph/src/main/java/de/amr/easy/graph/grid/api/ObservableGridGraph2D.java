package de.amr.easy.graph.grid.api;

import de.amr.easy.graph.event.api.ObservableGraph;

/**
 * A 2D grid graph that fires events to its observers.
 * 
 * @author Armin Reichert
 * 
 *         * @param <V> vertex label type
 * @param <E>
 *          edge label type
 */
public interface ObservableGridGraph2D<V, E> extends GridGraph2D<V, E>, ObservableGraph<V, E> {
}