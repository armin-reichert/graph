package de.amr.graph.test;

import static de.amr.graph.grid.api.GridPosition.BOTTOM_LEFT;
import static de.amr.graph.grid.api.GridPosition.BOTTOM_RIGHT;
import static de.amr.graph.grid.api.GridPosition.TOP_LEFT;
import static de.amr.graph.grid.api.GridPosition.TOP_RIGHT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.GridFactory;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;

public class FullGridTest {

	private static final int COLS = 15;
	private static final int ROWS = 10;
	private static final int NUM_EDGES_FULL_4 = 2 * COLS * ROWS - COLS - ROWS;
	private static final int NUM_EDGES_FULL_8 = 4 * COLS * ROWS - 3 * COLS - 3 * ROWS + 2;

	private GridGraph2D<Void, Void> full4;
	private GridGraph2D<Void, Void> full8;

	@Before
	public void setUp() {
		full4 = GridFactory.fullGrid(COLS, ROWS, Top4.get(), null, null);
		full8 = GridFactory.fullGrid(COLS, ROWS, Top8.get(), null, null);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGridSize() {
		assertEquals(NUM_EDGES_FULL_4, full4.numEdges());
		assertEquals(COLS * ROWS, full4.numVertices());
		assertEquals(COLS, full4.numCols());
		assertEquals(ROWS, full4.numRows());

		assertEquals(NUM_EDGES_FULL_8, full8.numEdges());
		assertEquals(COLS * ROWS, full8.numVertices());
		assertEquals(COLS, full8.numCols());
		assertEquals(ROWS, full8.numRows());
	}

	@Test
	public void testIsFullOrEmpty() {
		assertFalse(full4.isEmpty());
		assertTrue(full4.isFull());

		assertFalse(full8.isEmpty());
		assertTrue(full8.isFull());
	}

	@Test
	public void testGridEdgeStreamSize() {
		assertEquals(NUM_EDGES_FULL_4, full4.edges().count());
		assertEquals(NUM_EDGES_FULL_8, full8.edges().count());
	}

	@Test
	public void testAdjVerticesInside() {

		for (int row = 1; row < ROWS - 1; ++row) {
			for (int col = 1; col < COLS - 1; ++col) {
				int cell = full4.cell(col, row);
				assertFalse(full4.adjacent(cell, cell - COLS - 1));
				assertTrue(full4.adjacent(cell, cell - COLS));
				assertFalse(full4.adjacent(cell, cell - COLS + 1));
				assertTrue(full4.adjacent(cell, cell - 1));
				assertTrue(full4.adjacent(cell, cell + 1));
				assertFalse(full4.adjacent(cell, cell + COLS - 1));
				assertTrue(full4.adjacent(cell, cell + COLS));
				assertFalse(full4.adjacent(cell, cell + COLS + 1));
			}
		}

		for (int row = 1; row < ROWS - 1; ++row) {
			for (int col = 1; col < COLS - 1; ++col) {
				int cell = full8.cell(col, row);
				assertTrue(full8.adjacent(cell, cell - COLS - 1));
				assertTrue(full8.adjacent(cell, cell - COLS));
				assertTrue(full8.adjacent(cell, cell - COLS + 1));
				assertTrue(full8.adjacent(cell, cell - 1));
				assertTrue(full8.adjacent(cell, cell + 1));
				assertTrue(full8.adjacent(cell, cell + COLS - 1));
				assertTrue(full8.adjacent(cell, cell + COLS));
				assertTrue(full8.adjacent(cell, cell + COLS + 1));
			}
		}
	}

	@Test
	public void testAdjVerticesAtTop() {
		for (int col = 1; col < COLS - 1; ++col) {
			int cell = full4.cell(col, 0);
			assertTrue(full4.adjacent(cell, cell - 1));
			assertTrue(full4.adjacent(cell, cell + 1));
			assertFalse(full4.adjacent(cell, cell + COLS - 1));
			assertTrue(full4.adjacent(cell, cell + COLS));
			assertFalse(full4.adjacent(cell, cell + COLS + 1));
		}
		for (int col = 1; col < COLS - 1; ++col) {
			int cell = full8.cell(col, 0);
			assertTrue(full8.adjacent(cell, cell - 1));
			assertTrue(full8.adjacent(cell, cell + 1));
			assertTrue(full8.adjacent(cell, cell + COLS - 1));
			assertTrue(full8.adjacent(cell, cell + COLS));
			assertTrue(full8.adjacent(cell, cell + COLS + 1));
		}
	}

	@Test
	public void testAdjVerticesAtRight() {
		for (int row = 1; row < ROWS - 1; ++row) {
			int cell = full4.cell(COLS - 1, row);
			assertFalse(full4.adjacent(cell, cell - COLS - 1));
			assertTrue(full4.adjacent(cell, cell - COLS));
			assertTrue(full4.adjacent(cell, cell - 1));
			assertFalse(full4.adjacent(cell, cell + COLS - 1));
			assertTrue(full4.adjacent(cell, cell + COLS));
		}

		for (int row = 1; row < ROWS - 1; ++row) {
			int cell = full8.cell(COLS - 1, row);
			assertTrue(full8.adjacent(cell, cell - COLS - 1));
			assertTrue(full8.adjacent(cell, cell - COLS));
			assertTrue(full8.adjacent(cell, cell - 1));
			assertTrue(full8.adjacent(cell, cell + COLS - 1));
			assertTrue(full8.adjacent(cell, cell + COLS));
		}
	}

	@Test
	public void testAdjVerticesAtBottom() {
		for (int col = 1; col < COLS - 1; ++col) {
			int cell = full4.cell(col, ROWS - 1);
			assertFalse(full4.adjacent(cell, cell - COLS - 1));
			assertTrue(full4.adjacent(cell, cell - COLS));
			assertFalse(full4.adjacent(cell, cell - COLS + 1));
			assertTrue(full4.adjacent(cell, cell - 1));
			assertTrue(full4.adjacent(cell, cell + 1));
		}

		for (int col = 1; col < COLS - 1; ++col) {
			int cell = full8.cell(col, ROWS - 1);
			assertTrue(full8.adjacent(cell, cell - COLS - 1));
			assertTrue(full8.adjacent(cell, cell - COLS));
			assertTrue(full8.adjacent(cell, cell - COLS + 1));
			assertTrue(full8.adjacent(cell, cell - 1));
			assertTrue(full8.adjacent(cell, cell + 1));
		}
	}

	@Test
	public void testAdjVerticesAtLeft() {
		for (int row = 1; row < ROWS - 1; ++row) {
			int cell = full4.cell(0, row);
			assertTrue(full4.adjacent(cell, cell - COLS));
			assertFalse(full4.adjacent(cell, cell - COLS + 1));
			assertTrue(full4.adjacent(cell, cell + 1));
			assertTrue(full4.adjacent(cell, cell + COLS));
			assertFalse(full4.adjacent(cell, cell + COLS + 1));
		}

		for (int row = 1; row < ROWS - 1; ++row) {
			int cell = full8.cell(0, row);
			assertTrue(full8.adjacent(cell, cell - COLS));
			assertTrue(full8.adjacent(cell, cell - COLS + 1));
			assertTrue(full8.adjacent(cell, cell + 1));
			assertTrue(full8.adjacent(cell, cell + COLS));
			assertTrue(full8.adjacent(cell, cell + COLS + 1));
		}
	}

	@Test
	public void testDegree() {
		assertEquals(2, full4.degree(full4.cell(TOP_LEFT)));
		assertEquals(2, full4.degree(full4.cell(TOP_RIGHT)));
		assertEquals(2, full4.degree(full4.cell(BOTTOM_LEFT)));
		assertEquals(2, full4.degree(full4.cell(BOTTOM_RIGHT)));
		for (int x = 0; x < full4.numCols(); ++x) {
			for (int y = 0; y < full4.numRows(); ++y) {
				Integer cell = full4.cell(x, y);
				assertTrue(full4.degree(cell) >= 2 && full4.degree(cell) <= 4);
				if (x == 0 || x == COLS - 1 || y == 0 || y == ROWS - 1) {
					assertTrue(full4.degree(cell) <= 3);
				}
			}
		}
	}

	@Test
	public void testConnectedTowards() {
		for (int x = 0; x < full4.numCols(); ++x) {
			for (int y = 0; y < full4.numRows(); ++y) {
				Integer cell = full4.cell(x, y);
				if (full4.numCols() > 1) {
					if (x == 0) {
						assertTrue(full4.isConnected(cell, Top4.E));
					}
					if (x == full4.numCols() - 1) {
						assertTrue(full4.isConnected(cell, Top4.W));
					}
				}
				if (full4.numRows() > 1) {
					if (y == 0) {
						assertTrue(full4.isConnected(cell, Top4.S));
					}
					if (y == full4.numRows() - 1) {
						assertTrue(full4.isConnected(cell, Top4.N));
					}
				}
			}
		}
	}
}