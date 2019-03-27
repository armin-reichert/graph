package de.amr.graph.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.amr.graph.core.api.Graph;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.AbstractGraphSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DepthFirstSearch;

public class GraphSearchTest {

	private GridGraph<Void, Void> circle3;

	@Before
	public void createFixture() {
		circle3 = new GridGraph<>(3, 3, Top4.get(), v -> null, (u, v) -> null, UndirectedEdge::new);
		circle3.addEdge(0, 1);
		circle3.addEdge(1, 2);
		circle3.addEdge(0, 3);
		circle3.addEdge(2, 5);
		circle3.addEdge(3, 6);
		circle3.addEdge(5, 8);
		circle3.addEdge(6, 7);
		circle3.addEdge(7, 8);
	}

	@Test(expected = IllegalStateException.class)
	public void testSetSelfAsParent() {
		Graph<Void, Void> graph = new GridGraph<>(2, 2, Top4.get(), v -> null, (u, v) -> null,
				UndirectedEdge::new);
		AbstractGraphSearch<?> pf = new BreadthFirstSearch(graph);
		pf.setParent(0, 0);
	}

	@Test(expected = NullPointerException.class)
	public void testNonNullGraph() {
		new BreadthFirstSearch(null);
	}

	@Test(expected = NullPointerException.class)
	public void testAddNullObserver() {
		Graph<Void, Void> g = new GridGraph<>(2, 2, Top4.get(), v -> null, (u, v) -> null, UndirectedEdge::new);
		new BreadthFirstSearch(g).addObserver(null);
	}

	@Test(expected = NullPointerException.class)
	public void testRemoveNullObserver() {
		Graph<Void, Void> g = new GridGraph<>(2, 2, Top4.get(), v -> null, (u, v) -> null, UndirectedEdge::new);
		new BreadthFirstSearch(g).addObserver(null);
	}

	@Test
	public void testInitialization() {
		Graph<Void, Void> g = new GridGraph<>(2, 2, Top4.get(), v -> null, (u, v) -> null, UndirectedEdge::new);
		BreadthFirstSearch search = new BreadthFirstSearch(g);
		g.vertices().forEach(v -> {
			assertEquals(-1, search.getParent(v));
		});
	}

	@Test
	public void testExploreIsolatedGraph() {
		Graph<Void, Void> g = new GridGraph<>(3, 3, Top4.get(), v -> null, (u, v) -> null, UndirectedEdge::new);
		BreadthFirstSearch search = new BreadthFirstSearch(g);
		g.vertices().forEach(v -> {
			assertEquals(TraversalState.UNVISITED, search.getState(v));
		});
		search.exploreGraph(0);
		assertEquals(TraversalState.COMPLETED, search.getState(0));
		g.vertices().filter(v -> v != 0).forEach(v -> {
			assertEquals(TraversalState.UNVISITED, search.getState(v));
		});
	}

	@Test
	public void testExploreFullGraph() {
		GridGraph<Void, Void> g = new GridGraph<>(3, 3, Top4.get(), v -> null, (u, v) -> null,
				UndirectedEdge::new);
		g.fill();
		BreadthFirstSearch search = new BreadthFirstSearch(g);
		g.vertices().forEach(v -> {
			assertEquals(TraversalState.UNVISITED, search.getState(v));
		});
		search.exploreGraph(0);
		g.vertices().forEach(v -> {
			assertEquals(TraversalState.COMPLETED, search.getState(v));
		});
	}

	@Test
	public void testExploreCircleGraph() {
		BreadthFirstSearch search = new BreadthFirstSearch(circle3);
		circle3.vertices().forEach(v -> {
			assertEquals(TraversalState.UNVISITED, search.getState(v));
		});
		search.exploreGraph(0);
		circle3.vertices().filter(v -> v != 4).forEach(v -> {
			assertEquals(TraversalState.COMPLETED, search.getState(v));
		});
	}

	@Test
	public void testSingletonPath() {
		Path path = new BreadthFirstSearch(circle3).findPath(0, 0);
		assertEquals(Path.unit(0), path);
	}

	@Test
	public void testBFS() {
		Path path = new BreadthFirstSearch(circle3).findPath(0, 8);
		assertEquals(5, path.numVertices());
		assertTrue(path.is(0, 1, 2, 5, 8) || path.is(0, 3, 6, 7, 8));
	}

	@Test
	public void testDFS() {
		Path path = new DepthFirstSearch(circle3).findPath(0, 8);
		assertEquals(5, path.numVertices());
		assertTrue(path.is(0, 1, 2, 5, 8) || path.is(0, 3, 6, 7, 8));
	}
}