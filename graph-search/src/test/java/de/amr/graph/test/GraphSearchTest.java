package de.amr.graph.test;

import static de.amr.graph.core.api.TraversalState.COMPLETED;
import static de.amr.graph.core.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.Path.edge;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.amr.graph.core.api.Graph;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.impl.Grid4Topology;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DepthFirstSearch;

public class GraphSearchTest {

	private Graph<Void, Void> graph;
	private Path solution1;
	private Path solution2;
	private ObservableGraphSearch bfs;
	private ObservableGraphSearch dfs;
	private GridGraph<Void, Void> fullGrid;
	private GridGraph<Void, Void> emptyGrid;

	@Before
	public void createFixture() {
		graph = new GridGraph<>(3, 3, Grid4Topology.get(), v -> null, (u, v) -> null, UndirectedEdge::new);
		graph.addEdge(0, 1);
		graph.addEdge(1, 2);
		graph.addEdge(0, 3);
		graph.addEdge(2, 5);
		graph.addEdge(3, 6);
		graph.addEdge(5, 8);
		graph.addEdge(6, 7);
		graph.addEdge(7, 8);

		bfs = new BreadthFirstSearch(graph);
		dfs = new DepthFirstSearch(graph);

		solution1 = edge(0, 1).concat(edge(1, 2)).concat(edge(2, 5)).concat(edge(5, 8));
		solution2 = edge(0, 3).concat(edge(3, 6)).concat(edge(6, 7)).concat(edge(7, 8));

		fullGrid = new GridGraph<>(3, 3, Grid4Topology.get(), v -> null, (u, v) -> null, UndirectedEdge::new);
		fullGrid.fill();

		emptyGrid = new GridGraph<>(3, 3, Grid4Topology.get(), v -> null, (u, v) -> null, UndirectedEdge::new);
	}

	@Test(expected = IllegalStateException.class)
	public void testSetSelfAsParent() {
		bfs.setParent(0, 0);
	}

	@Test(expected = NullPointerException.class)
	public void testNullGraphInSearch() {
		new BreadthFirstSearch(null);
	}

	@Test(expected = NullPointerException.class)
	public void testAddNullObserver() {
		bfs.addObserver(null);
	}

	@Test(expected = NullPointerException.class)
	public void testRemoveNullObserver() {
		bfs.removeObserver(null);
	}

	@Test
	public void testInitialization() {
		graph.vertices().forEach(v -> {
			assertEquals(Graph.NO_VERTEX, bfs.getParent(v));
			assertEquals(TraversalState.UNVISITED, bfs.getState(v));
			assertEquals(Path.INFINITE_COST, bfs.getCost(v), Math.ulp(0));
		});
	}

	@Test
	public void testExploreEmptyGrid() {
		BreadthFirstSearch search = new BreadthFirstSearch(emptyGrid);
		emptyGrid.vertices().forEach(v -> assertEquals(UNVISITED, search.getState(v)));
		search.exploreGraph(0);
		assertEquals(COMPLETED, search.getState(0));
		emptyGrid.vertices().filter(v -> v != 0).forEach(v -> assertEquals(UNVISITED, search.getState(v)));
	}

	@Test
	public void testExploreFullGrid() {
		BreadthFirstSearch search = new BreadthFirstSearch(fullGrid);
		fullGrid.vertices().forEach(v -> assertEquals(UNVISITED, search.getState(v)));
		search.exploreGraph(0);
		fullGrid.vertices().forEach(v -> assertEquals(COMPLETED, search.getState(v)));
	}

	@Test
	public void testExploreSampleGraph() {
		graph.vertices().forEach(v -> assertEquals(UNVISITED, bfs.getState(v)));
		bfs.exploreGraph(0);
		graph.vertices().filter(v -> v != 4).forEach(v -> assertEquals(COMPLETED, bfs.getState(v)));
	}

	@Test
	public void testSingletonPath() {
		assertEquals(Path.unit(0), bfs.findPath(0, 0));
	}

	@Test
	public void testBFS() {
		Path path = bfs.findPath(0, 8);
		assertEquals(5, path.numVertices());
		assertTrue(path.equals(solution1) || path.equals(solution2));
	}

	@Test
	public void testDFS() {
		Path path = dfs.findPath(0, 8);
		assertEquals(5, path.numVertices());
		assertTrue(path.equals(solution1) || path.equals(solution2));
	}
}