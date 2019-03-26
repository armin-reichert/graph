package de.amr.graph.test;

import static de.amr.graph.pathfinder.api.Path.NO_PATH;
import static de.amr.graph.pathfinder.api.Path.edge;
import static de.amr.graph.pathfinder.api.Path.unit;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.amr.graph.pathfinder.api.Path;

public class PathTest {

	@Test
	public void testNoPath() {
		assertEquals(0, NO_PATH.numEdges());
		assertEquals(0, NO_PATH.numVertices());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoPathSource() {
		NO_PATH.source();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoPathTarget() {
		NO_PATH.target();
	}

	@Test
	public void testUnit() {
		Path p = unit(0);
		assertEquals(0, p.numEdges());
		assertEquals(1, p.numVertices());
		assertEquals(0, p.source());
		assertEquals(0, p.target());
	}

	@Test
	public void testEdge() {
		Path p = edge(0, 1);
		assertEquals(1, p.numEdges());
		assertEquals(2, p.numVertices());
		assertEquals(0, p.source());
		assertEquals(1, p.target());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConcatNoPath() {
		NO_PATH.concat(NO_PATH);
	}

	@Test(expected = NullPointerException.class)
	public void testConcatNull() {
		unit(0).concat(null);
	}

	@Test
	public void testConcatUnit() {
		assertEquals(unit(0), unit(0).concat(unit(0)));
		assertEquals(edge(0, 1), unit(0).concat(edge(0, 1)));
		assertEquals(edge(0, 1), edge(0, 1).concat(unit(1)));
	}

	@Test
	public void testConcat() {
		Path p = edge(0, 1).concat(edge(1, 2)).concat(edge(2, 3));
		assertEquals(3, p.numEdges());
		assertEquals(4, p.numVertices());
		assertEquals(0, p.source());
		assertEquals(3, p.target());
	}
}