package de.amr.graph.grid.curves;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.impl.Top4;

/**
 * Curve base class.
 * 
 * <p>
 * A curve is defined by a list of moves (N, E, S, W).
 * 
 * @author Armin Reichert
 */
public abstract class Curve implements Iterable<Integer> {

	private final List<Integer> moves = new ArrayList<>();

	protected void go(int dir) {
		moves.add(dir);
	}

	@Override
	public Iterator<Integer> iterator() {
		return moves.iterator();
	}

	@Override
	public String toString() {
		return moves.stream().map(Top4.get()::name).collect(Collectors.joining(","));
	}

	/**
	 * Traverses the given curve and executes the given action for each visited cell.
	 * 
	 * @param curve
	 *                 a curve
	 * @param grid
	 *                 the traversed grid
	 * @param start
	 *                 the start cell
	 * @param action
	 *                 the action executed for each cell
	 */
	public void traverse(GridGraph2D<?, ?> grid, int start, BiConsumer<Integer, Integer> action) {
		int current = start;
		for (int dir : moves) {
			int next = grid.neighbor(current, dir).getAsInt();
			action.accept(current, next);
			current = next;
		}
	}

	/**
	 * Returns the list of cells traversed by a curve on a grid when starting at a given cell.
	 * 
	 * @param curve
	 *                a curve
	 * @param grid
	 *                the traversed grid
	 * @param start
	 *                the start cell
	 * @return list of cells traversed by {@code curve} on {@code grid} when starting at {@code start}
	 */
	public List<Integer> cells(GridGraph2D<?, ?> grid, int start) {
		/*@formatter:off*/
		return moves.stream().collect(
			() -> new ArrayList<>(Arrays.asList(start)), 
			(cells, dir) -> cells.add(grid.neighbor(cells.get(cells.size() - 1), dir).getAsInt()),
			ArrayList<Integer>::addAll
		);
		/*@formatter:on*/
	}

	/**
	 * Returns a textual representation of the grid cells traversed by the given curve.
	 * 
	 * @param curve
	 *                a curve
	 * @param grid
	 *                the traversed grid
	 * @param start
	 *                the start cell
	 * @return textual representation of the grid cells traversed by the given curve
	 */
	public String cellsAsString(GridGraph2D<?, ?> grid, int start) {
		return cells(grid, start).stream().map(cell -> String.format("(%d,%d)", grid.col(cell), grid.row(cell)))
				.collect(Collectors.joining());
	}

}