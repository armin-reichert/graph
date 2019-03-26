package de.amr.graph.grid.api;

import de.amr.graph.core.api.ObservableGraph;

/**
 * A 2D grid graph that can fire events to its observers.
 * 
 * @author Armin Reichert
 * 
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 */
public interface ObservableGridGraph2D<V, E> extends GridGraph2D<V, E>, ObservableGraph<V, E> {
}