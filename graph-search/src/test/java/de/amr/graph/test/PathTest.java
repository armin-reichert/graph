package de.amr.graph.test;

import static de.amr.graph.pathfinder.api.Path.NULL;
import static de.amr.graph.pathfinder.api.Path.edge;
import static de.amr.graph.pathfinder.api.Path.unit;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.api.PathException;

public class PathTest {

	private Path p;

	@Before
	public void createFixture() {
		p = edge(0, 1).concat(edge(1, 2)).concat(edge(2, 3));
	}

	@Test
	public void testNullPath() {
		assertEquals(0, NULL.numEdges());
		assertEquals(0, NULL.numVertices());
	}

	@Test(expected = PathException.class)
	public void testNullPathSourceUndefined() {
		NULL.source();
	}

	@Test(expected = PathException.class)
	public void testNullPathTargetUndefined() {
		NULL.target();
	}

	@Test
	public void testUnit() {
		Path unit_0 = unit(0);
		assertEquals(0, unit_0.numEdges());
		assertEquals(1, unit_0.numVertices());
		assertEquals(0, unit_0.source());
		assertEquals(0, unit_0.target());
	}

	@Test
	public void testEdge() {
		Path edge = edge(0, 1);
		assertEquals(1, edge.numEdges());
		assertEquals(2, edge.numVertices());
		assertEquals(0, edge.source());
		assertEquals(1, edge.target());
	}

	public void testConcatNullPathWithItself() {
		assertEquals(NULL, NULL.concat(NULL));
	}

	public void testConcatNullPathWithNonNullPath() {
		assertEquals(NULL, p.concat(NULL));
		assertEquals(NULL, NULL.concat(p));
	}

	@Test(expected = NullPointerException.class)
	public void testConcatNullPointer() {
		unit(0).concat(null);
	}

	@Test
	public void testConcatUnit() {
		assertEquals(unit(0), unit(0).concat(unit(0)));
		assertEquals(p, unit(p.source()).concat(p));
		assertEquals(p, p.concat(unit(p.target())));
	}

	@Test
	public void testConcat() {
		assertEquals(3, p.numEdges());
		assertEquals(4, p.numVertices());
		assertEquals(0, p.source());
		assertEquals(3, p.target());
	}
}