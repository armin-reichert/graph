package de.amr.graph.pathfinder.api;

public interface ObservableGraphSearch extends GraphSearch {

	void addObserver(GraphSearchObserver observer);

	void removeObserver(GraphSearchObserver observer);

	void removeAllObservers();

}