package de.amr.easy.graph.event.api;

import de.amr.easy.graph.core.api.Graph;

/**
 * A graph whose operations can be observed.
 * 
 * @param <V>
 *          vertex label type
 * @param <E>
 *          edge label type
 */
public interface ObservableGraph<V, E> extends Graph<V, E> {

	/**
	 * Adds the given observer to this graph.
	 * 
	 * @param observer
	 *                   graph observer
	 */
	void addGraphObserver(GraphObserver<V, E> observer);

	/**
	 * Removes the given observer to this graph.
	 * 
	 * @param observer
	 *                   graph observer
	 */
	void removeGraphObserver(GraphObserver<V, E> observer);

	/**
	 * Enables/disables event firing.
	 * 
	 * @param enabled
	 *                  tells if events are raised
	 */
	void setEventsEnabled(boolean enabled);
}
