package de.amr.graph.pathfinder.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import de.amr.graph.core.api.Graph;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;

/**
 * Bidirectional search.
 * 
 * @author Armin Reichert
 */
public class BidiGraphSearch<F extends AbstractGraphSearch, B extends AbstractGraphSearch>
		implements ObservableGraphSearch {

	private final F forwardSearch;
	private final B backwardsSearch;
	private boolean searchingForward;
	private int meetingPoint;

	public BidiGraphSearch(F forwardSearch, B backwardsSearch) {
		this.forwardSearch = forwardSearch;
		this.backwardsSearch = backwardsSearch;
		meetingPoint = Graph.NO_VERTEX;
	}

	public F getForwardSearch() {
		return forwardSearch;
	}

	public B getBackwardsSearch() {
		return backwardsSearch;
	}

	public int getMeetingPoint() {
		return meetingPoint;
	}

	@Override
	public void start(int source, int target) {
		if (source == target) {
			forwardSearch.setState(source, TraversalState.COMPLETED);
			forwardSearch.setCost(source, 0);
			backwardsSearch.setState(source, TraversalState.COMPLETED);
			backwardsSearch.setCost(source, 0);
			meetingPoint = source;
		} else {
			forwardSearch.start(source, target);
			backwardsSearch.start(target, source);
			meetingPoint = Graph.NO_VERTEX;
			searchingForward = false;
		}
	}

	@Override
	public boolean canExplore() {
		return meetingPoint == Graph.NO_VERTEX && (forwardSearch.canExplore() || backwardsSearch.canExplore());
	}

	@Override
	public boolean exploreVertex() {
		searchingForward = !searchingForward;
		if (searchingForward && forwardSearch.canExplore() && forwardSearch.exploreVertex()) {
			return true;
		} else if (!searchingForward && backwardsSearch.canExplore() && backwardsSearch.exploreVertex()) {
			return true;
		}
		return checkMeetingPoint(forwardSearch.getCurrentVertex()) || checkMeetingPoint(backwardsSearch.getCurrentVertex());
	}

	private boolean checkMeetingPoint(int candidate) {
		if (forwardSearch.getState(candidate) == TraversalState.COMPLETED
				&& backwardsSearch.getState(candidate) == TraversalState.COMPLETED) {
			meetingPoint = candidate;
			reverseParentLinks(meetingPoint);
			return true;
		}
		return false;
	}

	private void reverseParentLinks(int meetingVertex) {
		List<Integer> backPath = new ArrayList<>();
		BitSet cycleCheck = new BitSet();
		for (int v = meetingVertex; v != Graph.NO_VERTEX; v = backwardsSearch.getParent(v)) {
			backPath.add(v);
			if (cycleCheck.get(v)) {
				System.err.println("Path: " + backPath);
				System.err.println("Vertex: " + v);
				throw new IllegalStateException("Cycle detected when creating backward path");
			}
			cycleCheck.set(v);
		}
		// recompute cost for backward path
		backwardsSearch.setCost(meetingVertex, forwardSearch.getCost(meetingVertex));
		for (int i = 1; i < backPath.size(); ++i) {
			backwardsSearch.setParent(backPath.get(i), backPath.get(i - 1));
			double edgeCost = backwardsSearch.getCost(i - 1) - backwardsSearch.getCost(i);
			backwardsSearch.setCost(i - 1, backwardsSearch.getCost(i - 1) + edgeCost);
		}
	}

	@Override
	public void setParent(int child, int parent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getCost(int v) {
		return forwardSearch.getCost(v) != Path.INFINITE_COST ? forwardSearch.getCost(v) : backwardsSearch.getCost(v);
	}

	@Override
	public void setCost(int v, double value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getMaxCost() {
		return forwardSearch.getMaxCost();
	}

	@Override
	public Optional<Integer> getMaxCostVertex() {
		return forwardSearch.getMaxCostVertex();
	}

	@Override
	public int getSource() {
		return forwardSearch.getSource();
	}

	@Override
	public int getTarget() {
		return forwardSearch.getTarget();
	}

	@Override
	public int getCurrentVertex() {
		return searchingForward ? forwardSearch.getCurrentVertex() : backwardsSearch.getCurrentVertex();
	}

	@Override
	public OptionalInt getNextVertex() {
		return searchingForward ? forwardSearch.getNextVertex() : backwardsSearch.getNextVertex();
	}

	@Override
	public TraversalState getState(int v) {
		return forwardSearch.getState(v) != TraversalState.UNVISITED ? forwardSearch.getState(v)
				: backwardsSearch.getState(v);
	}

	@Override
	public int getParent(int v) {
		if (forwardSearch.getState(v) != TraversalState.UNVISITED) {
			return forwardSearch.getParent(v);
		}
		if (backwardsSearch.getState(v) != TraversalState.UNVISITED) {
			return backwardsSearch.getParent(v);
		}
		return Graph.NO_VERTEX;
	}

	@Override
	public void addObserver(GraphSearchObserver observer) {
		forwardSearch.addObserver(observer);
		backwardsSearch.addObserver(observer);
	}

	@Override
	public void removeObserver(GraphSearchObserver observer) {
		forwardSearch.removeObserver(observer);
		backwardsSearch.removeObserver(observer);
	}

	@Override
	public void removeAllObservers() {
		forwardSearch.removeAllObservers();
		backwardsSearch.removeAllObservers();
	}
}