package de.amr.graph.pathfinder.impl;

import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import de.amr.datastruct.Stack;
import de.amr.graph.core.api.Graph;

/**
 * Depth-first traversal of a graph.
 * 
 * @author Armin Reichert
 */
public class DepthFirstSearch<V, E> extends AbstractSearch {

	protected Graph<V, E> graph;
	protected Stack<Integer> stack;

	public DepthFirstSearch(Graph<V, E> graph) {
		this.graph = graph;
		stack = new Stack<>();
	}

	@Override
	protected void init() {
		super.init();
		stack.clear();
	}

	@Override
	public void traverseGraph(int source, int target) {
		init();
		stack.push(source);
		setState(source, VISITED);
		while (!stack.isEmpty()) {
			int current = stack.pop();
			if (current == target) {
				return;
			}
			expand(current);
		}
	}

	protected void expand(int current) {
		graph.adj(current).filter(neighbor -> getState(neighbor) == UNVISITED).forEach(neighbor -> {
			stack.push(neighbor);
			setState(neighbor, VISITED);
			setParent(neighbor, current);
		});
	}

	public boolean isStacked(int v) {
		return stack.contains(v);
	}
}