package de.amr.graph.pathfinder.impl;

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
		searchingForward = true;
		meetingPoint = -1;
	}

	@Override
	public void start(int source, int target) {
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
			if (fwd.exploreVertex()) {
				return true;
			}
			searchingForward = false;
		} else {
			if (bwd.exploreVertex()) {
				return true;
			}
			searchingForward = true;
		}
		if (checkCommonVertex()) {
			return true;
		}
		return false;
	}

	private boolean checkCommonVertex() {
		int fwdCurrent = fwd.getCurrentVertex();
		int bwdCurrent = bwd.getCurrentVertex();
		if (fwdCurrent == -1 || bwdCurrent == -1) {
			return false;
		}
		if (fwdCurrent == bwdCurrent) {
			meetingPoint = fwdCurrent;
			System.out.println("Meeting point=" + meetingPoint);
			int v = meetingPoint;
			int p = bwd.getParent(v);
			while (p != -1) {
				int pp = bwd.getParent(p);
				if (p != -1) {
					bwd.setParent(p, v);
					v = p;
					p = pp;
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