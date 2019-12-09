package de.amr.graph.grid.curves;

import static de.amr.graph.grid.impl.Grid4Topology.E;
import static de.amr.graph.grid.impl.Grid4Topology.N;
import static de.amr.graph.grid.impl.Grid4Topology.S;
import static de.amr.graph.grid.impl.Grid4Topology.W;

/**
 * Computes a Hilbert curve using recursive procedure calls.
 * <p>
 * The curve starts at the upper right corner of and ends at the lower right corner of its
 * containing rectangle.
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