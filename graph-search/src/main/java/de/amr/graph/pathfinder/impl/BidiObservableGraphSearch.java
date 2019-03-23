package de.amr.graph.pathfinder.impl;

import java.util.LinkedList;
import java.util.Optional;
import java.util.OptionalInt;

import de.amr.graph.core.api.TraversalState;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;

public class BidiObservableGraphSearch<F extends ObservableGraphSearch, B extends ObservableGraphSearch>
		implements ObservableGraphSearch {

	private boolean searchingForward;
	private int meetingPoint;
	private final F fwd;
	private final B bwd;

	public BidiObservableGraphSearch(F fwd, B bwd) {
		this.fwd = fwd;
		this.bwd = bwd;
	}

	@Override
	public void init() {
		fwd.init();
		bwd.init();
	}

	@Override
	public void start(int source, int target) {
		searchingForward = true;
		meetingPoint = -1;
		fwd.start(source, target);
		bwd.start(target, source);
	}

	@Override
	public boolean canExplore() {
		return meetingPoint == -1 && (fwd.canExplore() || bwd.canExplore());
	}

	@Override
	public boolean exploreVertex() {
		if (searchingForward) {
			if (fwd.canExplore() && fwd.exploreVertex()) {
				meetingPoint = fwd.getTarget();
				return true;
			}
		}
		else {
			if (bwd.canExplore() && bwd.exploreVertex()) {
				meetingPoint = bwd.getTarget();
				return true;
			}
		}
		searchingForward = !searchingForward;
		return checkMeetingPoint();
	}

	private boolean checkMeetingPoint() {
		int candidate = fwd.getCurrentVertex();
		if (fwd.getState(candidate) != TraversalState.UNVISITED
				&& bwd.getState(candidate) != TraversalState.UNVISITED) {
			meetingPoint = candidate;
			System.out.println("Meeting point: " + meetingPoint);
			// reverse parent links for path from meeting point back to target
			LinkedList<Integer> backPath = new LinkedList<>();
			for (int v = meetingPoint; v != -1; v = bwd.getParent(v)) {
				backPath.add(v);
			}
			for (int i = 0; i < backPath.size(); ++i) {
				if (i + 1 < backPath.size()) {
					bwd.setParent(backPath.get(i + 1), backPath.get(i));
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void setParent(int child, int parent) {
		if (searchingForward) {
			fwd.setParent(child, parent);
		}
		else {
			bwd.setParent(child, parent);
		}
	}

	@Override
	public double getCost(int v) {
		return fwd.getCost(v) != Path.INFINITE_COST ? fwd.getCost(v) : bwd.getCost(v);
	}

	@Override
	public void setCost(int v, double value) {
		if (searchingForward) {
			fwd.setCost(v, value);
		}
		else {
			bwd.setCost(v, value);
		}
	}

	@Override
	public double getMaxCost() {
		return fwd.getMaxCost();
	}

	@Override
	public Optional<Integer> getMaxCostVertex() {
		return fwd.getMaxCostVertex();
	}

	@Override
	public int getSource() {
		return fwd.getSource();
	}

	@Override
	public int getTarget() {
		return fwd.getTarget();
	}

	@Override
	public int getCurrentVertex() {
		return searchingForward ? fwd.getCurrentVertex() : bwd.getCurrentVertex();
	}

	@Override
	public OptionalInt getNextVertex() {
		return searchingForward ? fwd.getNextVertex() : bwd.getNextVertex();
	}

	@Override
	public TraversalState getState(int v) {
		return fwd.getState(v) != TraversalState.UNVISITED ? fwd.getState(v) : bwd.getState(v);
	}

	@Override
	public int getParent(int v) {
		if (fwd.getParent(v) != -1) {
			return fwd.getParent(v);
		}
		return bwd.getParent(v);
	}

	@Override
	public void addObserver(GraphSearchObserver observer) {
		fwd.addObserver(observer);
		bwd.addObserver(observer);
	}

	@Override
	public void removeObserver(GraphSearchObserver observer) {
		fwd.removeObserver(observer);
		bwd.removeObserver(observer);
	}

	@Override
	public void removeAllObservers() {
		fwd.removeAllObservers();
		bwd.removeAllObservers();
	}
}