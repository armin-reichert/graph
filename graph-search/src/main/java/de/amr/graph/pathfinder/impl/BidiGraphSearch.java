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
public class BidiGraphSearch<F extends AbstractGraphSearch<?>, B extends AbstractGraphSearch<?>>
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

	protected void clear() {
		fwd.clear();
		bwd.clear();
	}

	@Override
	public void start(int source, int target) {
		clear();
		fwd.start(source, target);
		bwd.start(target, source);
		forward = false;
		meetingPoint = -1;
	}

	@Override
	public boolean exploreGraph(int source, int target) {
		start(source, target);
		if (source == target) {
			fwd.setState(source, TraversalState.COMPLETED);
			bwd.setState(source, TraversalState.COMPLETED);
			meetingPoint = fwd.getSource();
			return true;
		}
		while (canExplore()) {
			if (exploreVertex()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canExplore() {
		return meetingPoint == -1 && (fwd.canExplore() || bwd.canExplore());
	}

	@Override
	public boolean exploreVertex() {
		forward = !forward;
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
		// recompute cost for backward path
		bwd.setCost(meetingPoint, fwd.getCost(meetingPoint));
		for (int i = 1; i < backPath.size(); ++i) {
			bwd.setParent(backPath.get(i), backPath.get(i - 1));
			double edgeCost = bwd.getCost(i-1) - bwd.getCost(i);
			bwd.setCost(i-1, bwd.getCost(i-1) + edgeCost);
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
		if (fwd.getState(v) != TraversalState.UNVISITED) {
			return fwd.getParent(v);
		}
		if (bwd.getState(v) != TraversalState.UNVISITED) {
			return bwd.getParent(v);
		}
		return -1;
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