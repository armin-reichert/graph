package de.amr.graph.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.amr.graph.core.api.Graph;
import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DepthFirstSearch;
import de.amr.graph.pathfinder.impl.GraphSearch;

public class GraphSearchTest {

	private GridGraph<Void, Void> circle3() {
		GridGraph<Void, Void> g = new GridGraph<>(3, 3, Top4.get(), v -> null, (u, v) -> null,
				UndirectedEdge::new);
		g.addEdge(0, 1);
		g.addEdge(1, 2);
		g.addEdge(0, 3);
		g.addEdge(2, 5);
		g.addEdge(3, 6);
		g.addEdge(5, 8);
		g.addEdge(6, 7);
		g.addEdge(7, 8);
		return g;
	}

	@Test(expected = NullPointerException.class)
	public void testNonNullGraph() {
		new BreadthFirstSearch<>(null);
	}

	@Test
	public void testInitialization() {
		Graph<Void, Void> g = new GridGraph<>(2, 2, Top4.get(), v -> null, (u, v) -> null, UndirectedEdge::new);
		GraphSearch<Void, Void> search = new BreadthFirstSearch<>(g);
		g.vertices().forEach(v -> {
			assertEquals(-1, search.getParent(v));
		});
	}

	@Test
	public void testExploreIsolatedGraph() {
		Graph<Void, Void> g = new GridGraph<>(3, 3, Top4.get(), v -> null, (u, v) -> null, UndirectedEdge::new);
		GraphSearch<Void, Void> search = new BreadthFirstSearch<>(g);
		g.vertices().forEach(v -> {
			assertEquals(TraversalState.UNVISITED, search.getState(v));
		});
		search.exploreGraph(0);
		assertEquals(TraversalState.VISITED, search.getState(0));
		g.vertices().filter(v -> v != 0).forEach(v -> {
			assertEquals(TraversalState.UNVISITED, search.getState(v));
		});
	}

	@Test
	public void testExploreFullGraph() {
		GridGraph<Void, Void> g = new GridGraph<>(3, 3, Top4.get(), v -> null, (u, v) -> null,
				UndirectedEdge::new);
		g.fill();
		GraphSearch<Void, Void> search = new BreadthFirstSearch<>(g);
		g.vertices().forEach(v -> {
			assertEquals(TraversalState.UNVISITED, search.getState(v));
		});
		search.exploreGraph(0);
		g.vertices().forEach(v -> {
			assertEquals(TraversalState.VISITED, search.getState(v));
		});
	}

	@Test
	public void testExploreCircleGraph() {
		GridGraph<Void, Void> g = circle3();
		GraphSearch<Void, Void> search = new BreadthFirstSearch<>(g);
		g.vertices().forEach(v -> {
			assertEquals(TraversalState.UNVISITED, search.getState(v));
		});
		search.exploreGraph(0);
		g.vertices().filter(v -> v != 4).forEach(v -> {
			assertEquals(TraversalState.VISITED, search.getState(v));
		});
	}

	@Test
	public void testBFS() {
		GridGraph<Void, Void> g = circle3();
		GraphSearch<Void, Void> search = new BreadthFirstSearch<>(g);
		List<Integer> path = search.findPath(0, 8);
		assertEquals(5, path.size());
		assertEquals(Arrays.asList(0, 1, 2, 5, 8), path);
		// assertEquals(Arrays.asList(0, 3, 6, 7, 8), path);
	}

	@Test
	public void testDFS() {
		GridGraph<Void, Void> g = circle3();
		GraphSearch<Void, Void> search = new DepthFirstSearch<>(g);
		List<Integer> path = search.findPath(0, 8);
		assertEquals(5, path.size());
		assertEquals(Arrays.asList(0, 1, 2, 5, 8), path);
		// assertEquals(Arrays.asList(0, 3, 6, 7, 8), path);
	}

}
