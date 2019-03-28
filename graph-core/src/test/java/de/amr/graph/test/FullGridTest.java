package de.amr.graph.test;

import static de.amr.graph.grid.api.GridPosition.BOTTOM_LEFT;
import static de.amr.graph.grid.api.GridPosition.BOTTOM_RIGHT;
import static de.amr.graph.grid.api.GridPosition.TOP_LEFT;
import static de.amr.graph.grid.api.GridPosition.TOP_RIGHT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.util.GraphUtils;

public class FullGridTest {

	private static final int WIDTH = 15;
	private static final int HEIGHT = 10;

	private GridGraph2D<Void, Void> full4;
	private GridGraph2D<Void, Void> full8;

	@Before
	public void setUp() {
		full4 = GraphUtils.<Void, Void> fullGrid(WIDTH, HEIGHT, Top4.get());
		full8 = GraphUtils.<Void, Void> fullGrid(3, 3, Top8.get());
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGridSize() {
		assertEquals(2 * WIDTH * HEIGHT - (WIDTH + HEIGHT), full4.numEdges());
		assertEquals(WIDTH * HEIGHT, full4.numVertices());
		assertEquals(WIDTH, full4.numCols());
		assertEquals(HEIGHT, full4.numRows());
	}

	@Test
	public void testGridEdgeStream() {
		assertEquals(2 * WIDTH * HEIGHT - (WIDTH + HEIGHT), full4.edges().count());
	}

	private void assertEqualElements(IntStream numbers, Integer... cells) {
		assertEquals(numbers.boxed().collect(Collectors.toSet()), new HashSet<>(Arrays.asList(cells)));
	}

	@Test
	public void testAdjVertices() {
		int cell = full4.cell(1, 1);
		assertEqualElements(full4.adj(cell), full4.cell(1, 0), full4.cell(1, 2), full4.cell(2, 1),
				full4.cell(0, 1));
	}

	@Test
	public void testAdjVerticesAtCorners() {
		int cell;
		cell = full4.cell(TOP_LEFT);
		assertEqualElements(full4.adj(cell), full4.cell(1, 0), full4.cell(0, 1));
		cell = full4.cell(TOP_RIGHT);
		assertEqualElements(full4.adj(cell), full4.cell(WIDTH - 2, 0), full4.cell(WIDTH - 1, 1));
		cell = full4.cell(BOTTOM_LEFT);
		assertEqualElements(full4.adj(cell), full4.cell(0, HEIGHT - 2), full4.cell(1, HEIGHT - 1));
		cell = full4.cell(BOTTOM_RIGHT);
		assertEqualElements(full4.adj(cell), full4.cell(WIDTH - 1, HEIGHT - 2),
				full4.cell(WIDTH - 2, HEIGHT - 1));
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
				if (x == 0 || x == WIDTH - 1 || y == 0 || y == HEIGHT - 1) {
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

	@Test
	public void testFullGrid4() {
		int c = full4.numCols(), r = full4.numRows();
		assertEquals(r * (c - 1) + c * (r - 1), full4.numEdges());
	}

	@Test
	public void testFullGrid8() {
		int c = full8.numCols(), r = full8.numRows();
		assertEquals(4 * c * r - 3 * c - 3 * r + 2, full8.numEdges());

		assertTrue(full8.adjacent(4, 0));
		assertTrue(full8.adjacent(4, 1));
		assertTrue(full8.adjacent(4, 2));
		assertTrue(full8.adjacent(4, 3));
		assertFalse(full8.adjacent(4, 4));
		assertTrue(full8.adjacent(4, 5));
		assertTrue(full8.adjacent(4, 6));
		assertTrue(full8.adjacent(4, 7));
		assertTrue(full8.adjacent(4, 8));

		assertTrue(full8.adjacent(0, 4));
		assertTrue(full8.adjacent(1, 4));
		assertTrue(full8.adjacent(2, 4));
		assertTrue(full8.adjacent(3, 4));
		assertTrue(full8.adjacent(5, 4));
		assertTrue(full8.adjacent(6, 4));
		assertTrue(full8.adjacent(7, 4));
		assertTrue(full8.adjacent(8, 4));
	}
}