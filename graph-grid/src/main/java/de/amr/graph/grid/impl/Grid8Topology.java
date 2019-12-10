package de.amr.graph.grid.impl;

import java.util.Arrays;
import java.util.stream.Stream;

import de.amr.graph.grid.api.GridTopology;

/**
 * 8-direction topology for orthogonal grid.
 * 
 * @author Armin Reichert
 */
public final class Grid8Topology implements GridTopology {

	private static final Grid8Topology SINGLE = new Grid8Topology();

	/**
	 * @return singleton
	 */
	public static final Grid8Topology get() {
		return SINGLE;
	}

	public static final byte N = 0;
	public static final byte NE = 1;
	public static final byte E = 2;
	public static final byte SE = 3;
	public static final byte S = 4;
	public static final byte SW = 5;
	public static final byte W = 6;
	public static final byte NW = 7;

	private static final byte[][] VECTORS = {
		/*@formatter:off*/
		{ 0, -1 }, 
		{ 1, -1 }, 
		{ 1, 0 }, 
		{ 1, 1 }, 
		{ 0, 1 }, 
		{ -1, 1 },
		{ -1, 0 }, 
		{ -1, -1 } 
		/*@formatter:on*/
	};

	private Grid8Topology() {
	}

	private void rangeCheck(int dir) {
		if (!isValid(dir)) {
			throw new IllegalArgumentException("Direction out-of-range: " + dir);
		}
	}

	@Override
	public Stream<Byte> dirs() {
		return Stream.of(N, NE, E, SE, S, SW, W, NW);
	}

	@Override
	public boolean isValid(int dir) {
		return dir >= N && dir <= NW;
	}

	@Override
	public byte dirCount() {
		return 8;
	}

	@Override
	public String name(int dir) {
		rangeCheck(dir);
		return Arrays.asList("N", "NE", "E", "SE", "S", "SW", "W", "NW").get(dir);
	}

	@Override
	public byte inv(int dir) {
		rangeCheck(dir);
		return (byte) ((dir + 4) % 8);
	}

	@Override
	public byte left(int dir) {
		rangeCheck(dir);
		return (byte) ((dir + 7) % 8);
	}

	@Override
	public byte right(int dir) {
		rangeCheck(dir);
		return (byte) ((dir + 1) % 8);
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