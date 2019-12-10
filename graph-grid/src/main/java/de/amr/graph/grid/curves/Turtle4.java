package de.amr.graph.grid.curves;

import static de.amr.graph.grid.impl.Grid4Topology.N;

import de.amr.graph.grid.impl.Grid4Topology;

/**
 * A turtle that can walk to 4 directions (N, E, S, W).
 * 
 * @author Armin Reichert
 */
public class Turtle4 {

	private byte orientation;

	public Turtle4() {
		orientation = N;
	}

	public void turn(byte dir) {
		orientation = dir;
	}

	public void turnLeft() {
		turn(Grid4Topology.get().left(orientation));
	}

	public void turnRight() {
		turn(Grid4Topology.get().right(orientation));
	}

	public byte ahead() {
		return orientation;
	}

	public int behind() {
		return Grid4Topology.get().inv(orientation);
	}

	public int atLeft() {
		return Grid4Topology.get().left(orientation);
	}

	public int atRight() {
		return Grid4Topology.get().right(orientation);
	}
}