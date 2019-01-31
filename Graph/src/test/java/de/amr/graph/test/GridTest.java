package de.amr.graph.test;

import static de.amr.graph.grid.api.GridPosition.CENTER;
import static de.amr.graph.grid.impl.Top4.E;
import static de.amr.graph.grid.impl.Top4.N;
import static de.amr.graph.grid.impl.Top4.S;
import static de.amr.graph.grid.impl.Top4.W;
import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.ObservableGridGraph;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.pathfinder.api.TraversalState;
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
		grid = new ObservableGridGraph<>(WIDTH, HEIGHT, Top4.get(), v -> UNVISITED, (u, v) -> 1,
				UndirectedEdge::new);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGridInitialization() {
		assertEquals(grid.numEdges(), 0);
		assertEquals(grid.numVertices(), WIDTH * HEIGHT);
		assertEquals(grid.numCols(), WIDTH);
		assertEquals(grid.numRows(), HEIGHT);
		assertEquals(grid.numVertices(), grid.vertices().count());
		assertEquals(grid.numEdges(), grid.edges().count());
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
		int center = grid.cell(CENTER);
		assertTrue(grid.areNeighbors(center, center + 1));
		assertTrue(grid.areNeighbors(center, center - 1));
		assertTrue(grid.areNeighbors(center, center - grid.numCols()));
		assertTrue(grid.areNeighbors(center, center + grid.numCols()));
		assertTrue(!grid.areNeighbors(center, center));
		assertTrue(!grid.areNeighbors(center, center - 2));
		assertTrue(!grid.areNeighbors(center, center + 2));
	}

	@Test
	public void testFillAllEdges() {
		assertEquals(grid.numEdges(), 0);
		grid.fill();
		assertEquals(grid.numEdges(), 2 * WIDTH * HEIGHT - (WIDTH + HEIGHT));
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
		assertEquals(grid.numEdges(), 0);
		grid.fill();
		assertEquals(grid.numEdges(), 2 * WIDTH * HEIGHT - (WIDTH + HEIGHT));
		grid.removeEdges();
		assertEquals(grid.numEdges(), 0);
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
					int n = grid.neighbor(cell, N).getAsInt();
					assertEquals(n, grid.cell(x, y - 1));
				}
				if (x < grid.numCols() - 1) {
					int e = grid.neighbor(cell, E).getAsInt();
					assertEquals(e, grid.cell(x + 1, y));
				}
				if (y < grid.numRows() - 1) {
					int s = grid.neighbor(cell, S).getAsInt();
					assertEquals(s, grid.cell(x, y + 1));
				}
				if (x > 0) {
					int w = grid.neighbor(cell, W).getAsInt();
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
	public void testConnected() {
		int u = grid.cell(GridPosition.TOP_LEFT);
		int v = grid.cell(GridPosition.BOTTOM_RIGHT);
		assertFalse(GraphUtils.areConnected(grid, u, v));
		grid.fill();
		assertTrue(GraphUtils.areConnected(grid, u, v));
		grid.removeEdges();

		assertFalse(GraphUtils.areConnected(grid, 0, 1));
		grid.addEdge(0, 1);
		assertTrue(GraphUtils.areConnected(grid, 0, 1));
		grid.removeEdge(0, 1);
		assertFalse(GraphUtils.areConnected(grid, 0, 1));

		assertTrue(GraphUtils.areConnected(grid, 0, 0));
	}

	@Test
	public void testManhattanDist() {
		int u = grid.cell(GridPosition.TOP_LEFT);
		int v = grid.cell(GridPosition.BOTTOM_RIGHT);
		int dist = grid.manhattan(u, v);
		assertEquals((grid.numRows() - 1) + (grid.numCols() - 1), dist);
		assertEquals(0, grid.manhattan(u, u));
	}

	@Test
	public void testEuclidian2Dist() {
		int u = grid.cell(GridPosition.TOP_LEFT);
		int v = grid.cell(GridPosition.BOTTOM_RIGHT);
		int dist = grid.euclidean2(u, v);
		assertEquals((grid.numRows() - 1) * (grid.numRows() - 1) + (grid.numCols() - 1) * (grid.numCols() - 1),
				dist);
		assertEquals(0, grid.euclidean2(u, u));
	}

	@Test
	public void testChebyshevDist() {
		int u = grid.cell(GridPosition.TOP_LEFT);
		int v = grid.cell(GridPosition.BOTTOM_RIGHT);
		int dist = grid.chebyshev(u, v);
		assertEquals(Math.max(grid.numRows() - 1, grid.numCols() - 1), dist);
		assertEquals(0, grid.chebyshev(u, u));
	}

	@Test
	public void testEdgeLabel() {
		grid.addEdge(0, 1, 5);
		assertEquals(new Integer(5), grid.getEdgeLabel(0, 1));
		grid.setEdgeLabel(0, 1, 6);
		assertEquals(new Integer(6), grid.getEdgeLabel(0, 1));
	}
}