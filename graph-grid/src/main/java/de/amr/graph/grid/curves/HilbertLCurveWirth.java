package de.amr.graph.grid.curves;

import static de.amr.graph.grid.impl.Grid4Topology.E;
import static de.amr.graph.grid.impl.Grid4Topology.N;
import static de.amr.graph.grid.impl.Grid4Topology.S;
import static de.amr.graph.grid.impl.Grid4Topology.W;

/**
 * Implements a Hilbert curve using the following L-system (adapted from the book "Algorithmen und
 * Datenstrukturen" by Niklaus Wirth, Teubner 1983):
 * <p>
 * <code>
 * A -> D w A s A e B <br>
 * B -> C n B e B s A <br>
 * C -> B e C n C w D <br>
 * D -> A s D w D n C <br>
 * <br>
 * Axiom (start symbol): A
 * </code>
 * </p>
 * The terminals <code>n,e,s,w</code> are interpreted as walking towards the corresponding
 * direction.
 * <p>
 * As given, the curve starts at the upper right corner and ends at the lower right corner of the
 * grid.
 * 
 * @author Armin Reichert
 */
public class HilbertLCurveWirth extends Curve {

	public HilbertLCurveWirth(int depth) {
		a(depth);
	}

	/*@formatter:off*/
	void a(int i) { if (i > 0) { d(i-1); go(W); a(i-1); go(S); a(i-1); go(E); b(i-1); }}
	void b(int i) { if (i > 0) { c(i-1); go(N); b(i-1); go(E); b(i-1); go(S); a(i-1); }}
	void c(int i) { if (i > 0) { b(i-1); go(E); c(i-1); go(N); c(i-1); go(W); d(i-1); }}
	void d(int i) { if (i > 0) { a(i-1); go(S); d(i-1); go(W); d(i-1); go(N); c(i-1); }}
	/*@formatter:on*/
}