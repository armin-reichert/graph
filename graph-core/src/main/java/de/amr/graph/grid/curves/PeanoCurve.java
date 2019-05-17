package de.amr.graph.grid.curves;

import static de.amr.graph.grid.impl.Top4.E;
import static de.amr.graph.grid.impl.Top4.N;
import static de.amr.graph.grid.impl.Top4.S;
import static de.amr.graph.grid.impl.Top4.W;

/**
 * Computes a Peano-curve.
 * 
 * @author Armin Reichert
 */
public class PeanoCurve extends Curve {

	public PeanoCurve(int depth) {
		peano(depth, N, E, S, W);
	}

	private void peano(int depth, int n, int e, int s, int w) {
		if (depth > 0) {
			peano(depth - 1, n, e, s, w);
			go(n);
			peano(depth - 1, n, w, s, e);
			go(n);
			peano(depth - 1, n, e, s, w);
			go(e);
			peano(depth - 1, s, e, n, w);
			go(s);
			peano(depth - 1, s, w, n, e);
			go(s);
			peano(depth - 1, s, e, n, w);
			go(e);
			peano(depth - 1, n, e, s, w);
			go(n);
			peano(depth - 1, n, w, s, e);
			go(n);
			peano(depth - 1, n, e, s, w);
		}
	}
}