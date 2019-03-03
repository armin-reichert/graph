package de.amr.graph.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.amr.graph.core.api.UndirectedEdge;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top4;

public class EmptyGridTest {

	private GridGraph<Void, Void> grid;

	@Before
	public void setUp() {
		grid = new GridGraph<>(0, 0, Top4.get(), null, (u, v) -> null, UndirectedEdge::new);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testGridSize() {
		assertEquals(grid.numEdges(), 0);
		assertEquals(grid.numVertices(), 0);
		assertEquals(grid.numCols(), 0);
		assertEquals(grid.numRows(), 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGridAccessException() {
		grid.cell(0, 0);
	}

	@Test
	public void testGridEdgeStream() {
		assertTrue(grid.edges().count() == 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGridEdgeAccess() {
		grid.edge(0, 1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGridEdgeAdd() {
		grid.addEdge(0, 1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGridVertexDegree() {
		grid.degree(0);
	}
}