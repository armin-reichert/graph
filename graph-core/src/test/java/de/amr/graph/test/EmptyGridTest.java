package de.amr.graph.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.util.GraphUtils;

public class EmptyGridTest {

	private GridGraph<Void, Void> nullGrid;
	private GridGraph<Void, Void> emptyGrid3x3;

	@Before
	public void setUp() {
		nullGrid = GraphUtils.emptyGrid(0, 0, Top4.get());
		emptyGrid3x3 = GraphUtils.emptyGrid(3, 3, Top4.get());
	}

	@Test
	public void testGridSize() {
		assertEquals(0, nullGrid.numEdges());
		assertEquals(0, nullGrid.numVertices());
		assertEquals(0, nullGrid.numCols());
		assertEquals(0, nullGrid.numRows());

		assertEquals(0, emptyGrid3x3.numEdges());
		assertEquals(9, emptyGrid3x3.numVertices());
		assertEquals(3, emptyGrid3x3.numCols());
		assertEquals(3, emptyGrid3x3.numRows());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGridAccessException() {
		nullGrid.cell(0, 0);
	}

	@Test
	public void testGridEdgeStream() {
		assertTrue(nullGrid.edges().count() == 0);
		assertTrue(emptyGrid3x3.edges().count() == 0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGridEdgeAccess() {
		nullGrid.edge(0, 1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGridEdgeAdd() {
		nullGrid.addEdge(0, 1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGridVertexDegree() {
		nullGrid.degree(0);
	}
}