package de.amr.graph.grid.curves;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
}