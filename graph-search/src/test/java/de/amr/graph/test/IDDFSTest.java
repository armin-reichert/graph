package de.amr.graph.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.amr.graph.core.api.ObservableGraph;
import de.amr.graph.core.impl.ObservableUGraph;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.IDDFS;

public class IDDFSTest {

	ObservableGraph<String, Integer> g;

	@Before
	public void createTestData() {
		g = new ObservableUGraph<>();
		g.addVertex(0, "S");
		g.addVertex(1, "A");
		g.addVertex(2, "B");
		g.addVertex(3, "C");
		g.addVertex(4, "D");
		g.addVertex(5, "E");
		g.addVertex(6, "F");
		g.addVertex(7, "G");
		g.addEdge(0, 1, 3);
		g.addEdge(0, 4, 4);
		g.addEdge(1, 2, 4);
		g.addEdge(1, 4, 5);
		g.addEdge(2, 3, 4);
		g.addEdge(2, 5, 5);
		g.addEdge(4, 5, 2);
		g.addEdge(5, 6, 4);
		g.addEdge(6, 7, 3);
	}

	@Test
	public void testIterativeDeepening() {
		IDDFS search = new IDDFS(g);
		Path path = Path.findPath(0, 7, search);
		System.out.println("Path=" + path);
		Assert.assertEquals(5, path.numVertices());
	}
}