package de.amr.graph.grid.impl;

import java.util.Arrays;
import java.util.stream.IntStream;

import de.amr.graph.grid.api.GridTopology;

/**
 * 4-direction topology for orthogonal grid.
 * 
 * @author Armin Reichert
 */
public final class Grid4Topology implements GridTopology {

	private static final Grid4Topology SINGLE = new Grid4Topology();

	/**
	 * @return singleton
	 */
	public static final Grid4Topology get() {
		return SINGLE;
	}

	/** North */
	public static final byte N = 0;

	/** East */
	public static final byte E = 1;

	/** South */
	public static final byte S = 2;

	/** West */
	public static final byte W = 3;

	private static final byte[][] VECTORS = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };

	private Grid4Topology() {
	}

	private void rangeCheck(int dir) {
		if (!isValid(dir)) {
			throw new IllegalArgumentException("Direction out-of-range: " + dir);
		}
	}

	@Override
	public boolean isValid(int dir) {
		return dir >= N && dir <= W;
	}
	
	@Override
	public IntStream dirs() {
		return IntStream.of(N, E, S, W);
	}

	@Override
	public byte dirCount() {
		return 4;
	}

	@Override
	public String name(int dir) {
		rangeCheck(dir);
		return Arrays.asList("N", "E", "S", "W").get(dir);
	}

	@Override
	public byte inv(int dir) {
		rangeCheck(dir);
		return (byte) ((dir + 2) % 4);
	}

	@Override
	public byte left(int dir) {
		rangeCheck(dir);
		return (byte) ((dir + 3) % 4);
	}

	@Override
	public byte right(int dir) {
		rangeCheck(dir);
		return (byte) ((dir + 1) % 4);
	}

	@Override
	public byte dx(int dir) {
		rangeCheck(dir);
		return VECTORS[dir][0];
	}

	@Override
	public byte dy(int dir) {
		rangeCheck(dir);
		return VECTORS[dir][1];
	};
}