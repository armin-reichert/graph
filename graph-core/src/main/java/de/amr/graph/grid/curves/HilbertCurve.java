package de.amr.graph.grid.curves;

import static de.amr.graph.grid.impl.Top4.E;
import static de.amr.graph.grid.impl.Top4.N;
import static de.amr.graph.grid.impl.Top4.S;
import static de.amr.graph.grid.impl.Top4.W;

/**
 * Computes a Hilbert curve on a grid.
 * <p>
 * The curve starts at the upper right corner of a grid and ends at the lower right corner.
 *
 * @author Armin Reichert
 */
public class HilbertCurve extends Curve {

	public HilbertCurve(int depth) {
		hilbert(depth, N, E, S, W);
	}

	public HilbertCurve(int depth, int n, int e, int s, int w) {
		hilbert(depth, n, e, s, w);
	}

	private void hilbert(int depth, int n, int e, int s, int w) {
		if (depth > 0) {
			hilbert(depth - 1, e, n, w, s);
			go(w);
			hilbert(depth - 1, n, e, s, w);
			go(s);
			hilbert(depth - 1, n, e, s, w);
			go(e);
			hilbert(depth - 1, w, s, e, n);
		}
	}
}