package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

import de.amr.graph.core.api.Graph;
import de.amr.graph.pathfinder.api.PathFinder;

public class IterativeDeepeningSearch<V, E> implements PathFinder {

	private static class BoundedDFS<V, E> extends GraphSearch<V, E> implements PathFinder {

		private int depth;
		private int maxDepth;
		private Deque<Integer> frontier;

		public BoundedDFS(Graph<V, E> graph, int maxDepth) {
			super(graph);
			this.maxDepth = maxDepth;
		}

		@Override
		protected void init() {
			super.init();
			frontier = (Deque<Integer>) createFrontier();
			depth = 0;
		}

		@Override
		public void exploreGraph(int source, int target) {
			init();
			addToFrontier(source);
			setState(source, VISITED);
			setParent(source, -1);
			while (!isFrontierEmpty()) {
				int current = removeFromFrontier();
				if (current == target || depth == maxDepth) {
					return;
				}
				expandFrontier(current);
				++depth;
			}
		}

		@Override
		protected Queue<Integer> createFrontier() {
			return new ArrayDeque<>();
		}

		@Override
		protected void addToFrontier(int v) {
			frontier.push(v);
		}

		@Override
		protected int removeFromFrontier() {
			return frontier.pop();
		}

		@Override
		protected boolean isFrontierEmpty() {
			return frontier.isEmpty();
		}

		@Override
		public boolean partOfFrontier(int v) {
			return frontier.contains(v);
		}

		@Override
		public double getCost(int v) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setCost(int v, double value) {
			throw new UnsupportedOperationException();
		}
	}

	private Graph<V, E> graph;

	public IterativeDeepeningSearch(Graph<V, E> graph) {
		this.graph = graph;
	}

	@Override
	public List<Integer> findPath(int source, int target) {
		int depth = 0;
		while (true) {
			System.out.println("Depth=" + depth);
			BoundedDFS<V, E> dfs = new BoundedDFS<>(graph, depth);
			List<Integer> path = dfs.findPath(source, target);
			if (!path.isEmpty()) {
				return path;
			}
			++depth;
		}
	}
}
