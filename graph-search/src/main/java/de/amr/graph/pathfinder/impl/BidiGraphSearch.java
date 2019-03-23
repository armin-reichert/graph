package de.amr.graph.pathfinder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import de.amr.graph.core.api.TraversalState;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;

/**
 * Bidirectional search.
 * 
 * @author Armin Reichert
 */
public class BidiGraphSearch<F extends ObservableGraphSearch, B extends ObservableGraphSearch>
		implements ObservableGraphSearch {

	private final F fwd;
	private final B bwd;
	private boolean forward;
	private int meetingPoint = -1;

	public BidiGraphSearch(F fwd, B bwd) {
		this.fwd = fwd;
		this.bwd = bwd;
	}

	public F getForwardSearch() {
		return fwd;
	}

	public B getBackwardsSearch() {
		return bwd;
	}

	public int getMeetingPoint() {
		return meetingPoint;
	}

	@Override
	public void init() {
		fwd.init();
		bwd.init();
	}

	@Override
	public void start(int source, int target) {
		fwd.start(source, target);
		bwd.start(target, source);
		forward = false;
		meetingPoint = -1;
	}

	@Override
	public boolean exploreGraph(int source, int target) {
		init();
		start(source, target);
		while (meetingPoint == -1 && canExplore()) {
			if (exploreVertex()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExplore() {
		return fwd.canExplore() || bwd.canExplore();
	}

	@Override
	public boolean exploreVertex() {
		forward = !forward;
		if (getSource() == getTarget()) {
			meetingPoint = fwd.getSource();
			return true;
		}
		if (forward) {
			if (fwd.canExplore()) {
				boolean targetReached = fwd.exploreVertex();
				return targetReached || checkMeetingPoint(fwd.getCurrentVertex());
			}
		}
		else { // explore backwards
			if (bwd.canExplore()) {
				boolean sourceReached = bwd.exploreVertex();
				return sourceReached || checkMeetingPoint(bwd.getCurrentVertex());
			}
		}
		if (checkMeetingPoint(fwd.getCurrentVertex()) || checkMeetingPoint(bwd.getCurrentVertex())) {
			return true;
		}
		return false;
	}

	private boolean checkMeetingPoint(int candidate) {
		if (fwd.getState(candidate) == TraversalState.COMPLETED
				&& bwd.getState(candidate) == TraversalState.COMPLETED) {
			meetingPoint = candidate;
			System.out.println("Meeting point: " + meetingPoint);
			reverseParentLinks(meetingPoint);
			return true;
		}
		return false;
	}

	private void reverseParentLinks(int meetingPoint) {
		List<Integer> backPath = new ArrayList<>();
		for (int v = meetingPoint; v != -1; v = bwd.getParent(v)) {
			backPath.add(v);
		}
		for (int i = 1; i < backPath.size(); ++i) {
			bwd.setParent(backPath.get(i), backPath.get(i - 1));
		}
	}

	@Override
	public void setParent(int child, int parent) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getCost(int v) {
		return fwd.getCost(v) != Path.INFINITE_COST ? fwd.getCost(v) : bwd.getCost(v);
	}

	@Override
	public void setCost(int v, double value) {
		throw new UnsupportedOperationException();
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
		return forward ? fwd.getCurrentVertex() : bwd.getCurrentVertex();
	}

	@Override
	public OptionalInt getNextVertex() {
		return forward ? fwd.getNextVertex() : bwd.getNextVertex();
	}

	@Override
	public TraversalState getState(int v) {
		return fwd.getState(v) != TraversalState.UNVISITED ? fwd.getState(v) : bwd.getState(v);
	}

	@Override
	public int getParent(int v) {
		return fwd.getParent(v) != -1 ? fwd.getParent(v) : bwd.getParent(v);
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