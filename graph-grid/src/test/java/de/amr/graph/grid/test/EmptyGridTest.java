package de.amr.graph.grid.test;

import de.amr.graph.grid.impl.Grid4Topology;
import de.amr.graph.grid.impl.GridFactory;
import de.amr.graph.grid.impl.GridGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmptyGridTest {

	private GridGraph<Void, Void> nullGrid;
	private GridGraph<Void, Void> emptyGrid3x3;

	@BeforeEach
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

	@Test
	public void testGridAccessException() {
        assertThrows(IndexOutOfBoundsException.class, () -> nullGrid.cell(0, 0));
	}

	@Test
	public void testGridEdgeStream() {
		assertEquals(0, nullGrid.edges().count());
		assertEquals(0, emptyGrid3x3.edges().count());
	}

	@Test
	public void testGridEdgeAccess() {
		assertThrows(IndexOutOfBoundsException.class, () -> nullGrid.edge(0, 1));
	}

	@Test
	public void testGridEdgeAdd() {
        assertThrows(IndexOutOfBoundsException.class, () -> nullGrid.addEdge(0, 1));
	}

	@Test
	public void testGridVertexDegree() {
        assertThrows(IndexOutOfBoundsException.class, () -> nullGrid.degree(0));
	}

	@Test
	public void testIsFullOrEmpty() {
		assertTrue(emptyGrid3x3.isEmpty());
		assertFalse(emptyGrid3x3.isFull());
	}
}