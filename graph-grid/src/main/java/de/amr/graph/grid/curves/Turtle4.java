package de.amr.graph.grid.curves;

import static de.amr.graph.grid.impl.Top4.N;

import de.amr.graph.grid.impl.Top4;

/**
 * A turtle that can walk to 4 directions (N, E, S, W).
 * 
 * @author Armin Reichert
 */
public class Turtle4 {

	private int orientation;

	public Turtle4() {
		orientation = N;
	}

	public void turn(int dir) {
		orientation = dir;
	}

	public void turnLeft() {
		turn(Top4.get().left(orientation));
	}

	public void turnRight() {
		turn(Top4.get().right(orientation));
	}

	public int ahead() {
		return orientation;
	}

	public int behind() {
		return Top4.get().inv(orientation);
	}

	public int atLeft() {
		return Top4.get().left(orientation);
	}

	public int atRight() {
		return Top4.get().right(orientation);
	}
}