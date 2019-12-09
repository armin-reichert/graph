package de.amr.graph.grid.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.amr.graph.grid.impl.GridFactory;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Grid4Topology;

public class EmptyGridTest {

	private GridGraph<Void, Void> nullGrid;
	private GridGraph<Void, Void> emptyGrid3x3;

	@Before
	public void setUp() {
		nullGrid = GridFactory.emptyGrid(0, 0, Grid4Topology.get(), null, null);
		emptyGrid3x3 = GridFactory.emptyGrid(3, 3, Grid4Topology.get(), null, null);
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
	
	@Test
	public void testIsFullOrEmpty() {
		assertTrue(emptyGrid3x3.isEmpty());
		assertFalse(emptyGrid3x3.isFull());
	}
}