package de.amr.graph.grid.test;

import static de.amr.graph.core.api.TraversalState.UNVISITED;
import static de.amr.graph.grid.api.GridMetrics.chebyshev;
import static de.amr.graph.grid.api.GridMetrics.euclidean;
import static de.amr.graph.grid.api.GridMetrics.manhattan;
import static de.amr.graph.grid.api.GridPosition.BOTTOM_RIGHT;
import static de.amr.graph.grid.api.GridPosition.CENTER;
import static de.amr.graph.grid.api.GridPosition.TOP_LEFT;
import static de.amr.graph.grid.impl.Grid4Topology.E;
import static de.amr.graph.grid.impl.Grid4Topology.N;
import static de.amr.graph.grid.impl.Grid4Topology.S;
import static de.amr.graph.grid.impl.Grid4Topology.W;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.amr.graph.core.api.TraversalState;
import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.impl.Grid4Topology;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.ObservableGridGraph;
import de.amr.graph.util.GraphUtils;

/**
 * Test case for {@link GridGraph}
 * 
 * @author Armin Reichert
 */
public class GridTest {

	private static final int WIDTH = 100;
	private static final int HEIGHT = 100;

	private ObservableGridGraph<TraversalState, Integer> grid;

	@Before
	public void setUp() {
		grid = new ObservableGridGraph<>(WIDTH, HEIGHT, Grid4Topology.get(), v -> UNVISITED, (u, v) -> 1,
				UndirectedEdge::new);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGridInitialization() {
		assertEquals(0, grid.numEdges());
		assertEquals(WIDTH * HEIGHT, grid.numVertices());
		assertEquals(WIDTH, grid.numCols());
		assertEquals(HEIGHT, grid.numRows());
		assertEquals(grid.vertices().count(), grid.numVertices());
		assertEquals(grid.edges().count(), grid.numEdges());
	}

	@Test
	public void testInitialContent() {
		assertEquals(grid.vertices().filter(cell -> grid.get(cell) == UNVISITED).count(), grid.numVertices());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testAddVertexThrowsException() {
		grid.addVertex(0);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testRemoveVertexThrowsException() {
		grid.removeVertex(0);
	}

	@Test
	public void testGetNonexistingEdge() {
		assertFalse(grid.edge(0, 1).isPresent());
	}

	@Test
	public void testAddEdge() {
		int numEdges = grid.numEdges();
		assert (!grid.edge(0, 1).isPresent());
		grid.addEdge(0, 1);
		assertEquals(numEdges + 1, grid.numEdges());
	}

	@Test(expected = IllegalStateException.class)
	public void addEdgeTwiceThrowsException() {
		grid.addEdge(0, 1);
		grid.addEdge(0, 1);
	}

	@Test(expected = IllegalStateException.class)
	public void addEdgeToNonNeighborThrowsException() {
		grid.addEdge(0, 2);
	}

	@Test
	public void testAreNeighbors() {
		int v = grid.cell(CENTER);
		assertFalse(grid.areNeighbors(v, v));
		assertTrue(grid.areNeighbors(v, v + 1));
		assertTrue(grid.areNeighbors(v, v - 1));
		assertTrue(grid.areNeighbors(v, v - grid.numCols()));
		assertTrue(grid.areNeighbors(v, v + grid.numCols()));
		assertFalse(grid.areNeighbors(v, v - 2));
		assertFalse(grid.areNeighbors(v, v + 2));
	}

	@Test
	public void testFillAllEdges() {
		assertEquals(0, grid.numEdges());
		grid.fill();
		assertEquals(2 * WIDTH * HEIGHT - (WIDTH + HEIGHT), grid.numEdges());
	}

	@Test
	public void testRemoveEdge() {
		int numEdges = grid.numEdges();
		grid.addEdge(0, 1);
		assertEquals(grid.numEdges(), numEdges + 1);
		grid.removeEdge(0, 1);
		assertEquals(grid.numEdges(), numEdges);
		assertFalse(grid.edge(0, 1).isPresent());
	}

	@Test
	public void testRemoveAllEdges() {
		assertEquals(0, grid.numEdges());
		grid.fill();
		assertEquals(2 * WIDTH * HEIGHT - (WIDTH + HEIGHT), grid.numEdges());
		grid.removeEdges();
		assertEquals(0, grid.numEdges());
	}

	@Test
	public void testAdjVertices() {
		assertFalse(grid.adjacent(0, 1));
		assertFalse(grid.adjacent(1, 0));
		grid.addEdge(0, 1);
		assertTrue(grid.adjacent(0, 1));
		assertTrue(grid.adjacent(1, 0));
		grid.removeEdge(0, 1);
		assertFalse(grid.adjacent(0, 1));
		assertFalse(grid.adjacent(1, 0));
		grid.addEdge(1, 0);
		assertTrue(grid.adjacent(1, 0));
		assertTrue(grid.adjacent(0, 1));
	}

	@Test
	public void testCellCoordinates() {
		for (int x = 0; x < grid.numCols(); ++x) {
			for (int y = 0; y < grid.numRows(); ++y) {
				Integer cell = grid.cell(x, y);
				assertEquals(grid.col(cell), x);
				assertEquals(grid.row(cell), y);
			}
		}
	}

	@Test
	public void testGetNeighbor() {
		for (int x = 0; x < grid.numCols(); ++x) {
			for (int y = 0; y < grid.numRows(); ++y) {
				Integer cell = grid.cell(x, y);
				if (y > 0) {
					int n = grid.neighbor(cell, N).get();
					assertEquals(n, grid.cell(x, y - 1));
				}
				if (x < grid.numCols() - 1) {
					int e = grid.neighbor(cell, E).get();
					assertEquals(e, grid.cell(x + 1, y));
				}
				if (y < grid.numRows() - 1) {
					int s = grid.neighbor(cell, S).get();
					assertEquals(s, grid.cell(x, y + 1));
				}
				if (x > 0) {
					int w = grid.neighbor(cell, W).get();
					assertEquals(w, grid.cell(x - 1, y));
				}
			}
		}
	}

	@Test
	public void testCycleCheckerSquare() {
		// create graph without cycle:
		Integer a = grid.cell(0, 0);
		Integer b = grid.cell(1, 0);
		Integer c = grid.cell(1, 1);
		Integer d = grid.cell(0, 1);
		grid.addEdge(a, b);
		grid.addEdge(b, c);
		grid.addEdge(c, d);
		assertFalse(GraphUtils.containsCycle(grid));
		// add edge to create cycle:
		grid.addEdge(d, a);
		assertTrue(GraphUtils.containsCycle(grid));
	}

	@Test
	public void testManhattanDist() {
		int r = grid.numRows(), c = grid.numCols();
		int u = grid.cell(TOP_LEFT);
		int v = grid.cell(BOTTOM_RIGHT);
		assertEquals((r - 1) + (c - 1), manhattan(grid, u, v));
		assertEquals(0, manhattan(grid, u, u));
	}

	@Test
	public void testEuclideanDist() {
		int r = grid.numRows(), c = grid.numCols();
		int u = grid.cell(TOP_LEFT);
		int v = grid.cell(BOTTOM_RIGHT);
		double expected = Math.sqrt((r - 1) * (r - 1) + (c - 1) * (c - 1));
		assertEquals(expected, euclidean(grid, u, v), Math.ulp(expected));
		assertEquals(0, euclidean(grid, u, u), 0);
	}

	@Test
	public void testChebyshevDist() {
		int r = grid.numRows(), c = grid.numCols();
		int u = grid.cell(TOP_LEFT);
		int v = grid.cell(BOTTOM_RIGHT);
		assertEquals(Math.max(r - 1, c - 1), chebyshev(grid, u, v));
		assertEquals(0, chebyshev(grid, u, u));
	}

	@Test
	public void testEdgeLabel() {
		grid.addEdge(0, 1, 5);
		assertEquals(5, (int) grid.getEdgeLabel(0, 1));
		grid.setEdgeLabel(0, 1, 6);
		assertEquals(6, (int) grid.getEdgeLabel(0, 1));
	}
}