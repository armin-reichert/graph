package de.amr.graph.test;

import org.junit.Test;

import de.amr.graph.core.api.Graph;
import de.amr.graph.core.impl.UGraph;
import de.amr.graph.pathfinder.impl.IterativeDeepeningSearch;

public class IterativeDeepeningTest {

	@Test
	public void testIterativeDeepening() {
		Graph<String, Integer> g = new UGraph<>();

		g.addVertex(0);
		g.set(0, "S");

		g.addVertex(1);
		g.set(1, "A");

		g.addVertex(2);
		g.set(2, "B");

		g.addVertex(3);
		g.set(3, "C");

		g.addVertex(4);
		g.set(4, "D");

		g.addVertex(5);
		g.set(5, "E");

		g.addVertex(6);
		g.set(6, "F");

		g.addVertex(7);
		g.set(7, "G");

		g.addEdge(0, 1, 3);
		g.addEdge(0, 4, 4);
		g.addEdge(1, 2, 4);
		g.addEdge(1, 4, 5);
		g.addEdge(2, 3, 4);
		g.addEdge(2, 5, 5);
		g.addEdge(4, 5, 2);
		g.addEdge(5, 6, 4);
		g.addEdge(6, 7, 3);

		IterativeDeepeningSearch<String, Integer> search = new IterativeDeepeningSearch<>(g);
		search.findPath(0, 7);
	}

}
