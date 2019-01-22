package de.amr.easy.graph.grid.impl;

import java.util.stream.IntStream;

import de.amr.easy.graph.grid.api.Topology;

/**
 * 4-direction topology for orthogonal grid.
 * 
 * @author Armin Reichert
 */
public class Top4 implements Topology {

	/** North */
	public static final int N = 0;
	
	/** East */
	public static final int E = 1;
	
	/** South */
	public static final int S = 2;
	
	/** West */
	public static final int W = 3;

	private static final int[][] VEC = { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };

	public static String name(int dir) {
		return String.valueOf("NESW".charAt(dir));
	}

	@Override
	public IntStream dirs() {
		return IntStream.of(N, E, S, W);
	}

	@Override
	public int dirCount() {
		return 4;
	}

	@Override
	public int inv(int dir) {
		return (dir + 2) % 4;
	}

	@Override
	public int left(int dir) {
		return (dir + 3) % 4;
	}

	@Override
	public int right(int dir) {
		return (dir + 1) % 4;
	}

	@Override
	public int dx(int dir) {
		return VEC[dir][0];
	}

	@Override
	public int dy(int dir) {
		return VEC[dir][1];
	};
}