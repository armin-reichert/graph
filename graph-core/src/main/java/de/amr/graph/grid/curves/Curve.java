package de.amr.graph.grid.curves;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Curve base class.
 * <p>
 * A curve is defined by a list of directions.
 * 
 * @author Armin Reichert
 */
public abstract class Curve implements Iterable<Integer> {

	private final List<Integer> dirs = new ArrayList<>();

	protected void go(int dir) {
		dirs.add(dir);
	}

	@Override
	public Iterator<Integer> iterator() {
		return dirs.iterator();
	}
}