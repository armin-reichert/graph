package de.amr.graph.test;

import static de.amr.graph.core.api.TraversalState.COMPLETED;
import static de.amr.graph.core.api.TraversalState.UNVISITED;
import static de.amr.graph.core.api.TraversalState.VISITED;
import static de.amr.graph.grid.api.GridPosition.BOTTOM_LEFT;
import static de.amr.graph.grid.api.GridPosition.BOTTOM_RIGHT;
import static de.amr.graph.grid.api.GridPosition.CENTER;
import static de.amr.graph.grid.api.GridPosition.TOP_LEFT;
import static de.amr.graph.grid.api.GridPosition.TOP_RIGHT;
import static de.amr.graph.grid.curves.CurveUtils.cells;
import static de.amr.graph.grid.curves.CurveUtils.traverse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.amr.datastruct.StreamUtils;
import de.amr.graph.core.api.TraversalState;
import de.amr.graph.grid.curves.HilbertCurve;
import de.amr.graph.grid.curves.HilbertLCurve;
import de.amr.graph.grid.curves.HilbertLCurveWirth;
import de.amr.graph.grid.curves.MooreLCurve;
import de.amr.graph.grid.curves.PeanoCurve;
import de.amr.graph.grid.impl.GridFactory;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.DepthFirstSearch;
import de.amr.graph.pathfinder.impl.DepthFirstSearch2;
import de.amr.graph.pathfinder.impl.HillClimbingSearch;

public class GridCurveTest {

	private static final int K = 8;
	private static final int N = 1 << K; // N = 2^K

	private static void assertState(IntStream cells, Function<Integer, TraversalState> fnSupplyState,
			TraversalState... expected) {
		cells.forEach(cell -> assertTrue(Arrays.stream(expected).anyMatch(s -> s == fnSupplyState.apply(cell))));
	}

	private GridGraph<Boolean, Void> grid;

	@Before
	public void setUp() {
		grid = GridFactory.<Boolean, Void> fullGrid(N, N, Top4.get());
		grid.setDefaultVertexLabel(v -> false);
	}

	@After
	public void tearDown() {
	}

	private void assertAllCells(boolean state) {
		grid.vertices().forEach(cell -> assertTrue(grid.get(cell) == state));
	}

	private void markEdge(int u, int v) {
		grid.set(u, true);
		grid.set(v, true);
	}

	@Test
	public void testBFS() {
		BreadthFirstSearch bfs = new BreadthFirstSearch(grid);
		assertState(grid.vertices(), bfs::getState, UNVISITED);
		bfs.exploreGraph(grid.cell(CENTER));
		assertState(grid.vertices(), bfs::getState, VISITED, COMPLETED);
	}

	@Test
	public void testDFS() {
		int source = grid.cell(TOP_LEFT), target = grid.cell(BOTTOM_RIGHT);
		DepthFirstSearch dfs = new DepthFirstSearch(grid);
		assertState(grid.vertices(), dfs::getState, UNVISITED);
		Path path = dfs.findPath(source, target);
		assertState(StreamUtils.toIntStream(path), dfs::getState, VISITED, COMPLETED);
	}

	@Test
	public void testDFS2() {
		int source = grid.cell(TOP_LEFT), target = grid.cell(BOTTOM_RIGHT);
		DepthFirstSearch2 dfs = new DepthFirstSearch2(grid);
		assertState(grid.vertices(), dfs::getState, UNVISITED);
		Path path = dfs.findPath(source, target);
		assertState(StreamUtils.toIntStream(path), dfs::getState, COMPLETED);
	}

	@Test
	public void testHillClimbing() {
		int source = grid.cell(TOP_LEFT), target = grid.cell(BOTTOM_RIGHT);
		ToDoubleFunction<Integer> cost = u -> grid.manhattan(u, target);
		HillClimbingSearch hillClimbing = new HillClimbingSearch(grid, cost);
		assertState(grid.vertices(), hillClimbing::getState, UNVISITED);
		Path path = hillClimbing.findPath(source, target);
		path.forEach(cell -> assertTrue(hillClimbing.getState(cell) != UNVISITED));
	}

	@Test
	public void testHilbertCurve() {
		assertAllCells(false);
		traverse(new HilbertCurve(K), grid, grid.cell(TOP_RIGHT), this::markEdge);
		assertAllCells(true);
	}

	@Test
	public void testHilbertLCurve() {
		assertAllCells(false);
		traverse(new HilbertLCurve(K), grid, grid.cell(BOTTOM_LEFT), this::markEdge);
		assertAllCells(true);
	}

	@Test
	public void testHilbertLCurveWirth() {
		assertAllCells(false);
		traverse(new HilbertLCurveWirth(K), grid, grid.cell(TOP_RIGHT), this::markEdge);
		assertAllCells(true);
	}

	@Test
	public void testMooreLCurve() {
		assertAllCells(false);
		traverse(new MooreLCurve(K), grid, grid.cell(N / 2, N - 1), this::markEdge);
		assertAllCells(true);
	}

	@Test
	public void testPeanoCurve() {
		grid = GridFactory.emptyGrid(243, 243, Top4.get());
		grid.setDefaultVertexLabel(v -> false);
		assertAllCells(false);
		traverse(new PeanoCurve(5), grid, grid.cell(BOTTOM_LEFT), this::markEdge);
		assertAllCells(true);
	}

	@Test
	public void testCurveStream() {
		cells(new HilbertCurve(K), grid, grid.cell(TOP_RIGHT)).forEach(cell -> grid.set(cell, true));
		assertAllCells(true);
	}
}