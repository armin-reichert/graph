package de.amr.graph.pathfinder.impl;

import de.amr.graph.pathfinder.api.GraphSearchObserver;

public interface ObservableGraphSearch extends GraphSearch {

	void addObserver(GraphSearchObserver observer);

	void removeObserver(GraphSearchObserver observer);

	void removeAllObservers();

}