package de.amr.graph.pathfinder.api;

/**
 * Graph search that may be observed by listeners.
 * 
 * @author Armin Reichert
 */
public interface ObservableGraphSearch extends GraphSearch {

	void addObserver(GraphSearchObserver observer);

	void removeObserver(GraphSearchObserver observer);

	void removeAllObservers();

}