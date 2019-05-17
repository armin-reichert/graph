package de.amr.graph.grid.curves;

import static de.amr.graph.grid.impl.Top4.E;
import static de.amr.graph.grid.impl.Top4.N;
import static de.amr.graph.grid.impl.Top4.S;
import static de.amr.graph.grid.impl.Top4.W;

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
		A(depth);
	}

	/*@formatter:off*/
	
	// Terminals
	void n() { go(N); }
	void e() { go(E); }
	void s() { go(S); }
	void w() { go(W); }

	// Rules
	void A(int i) {if (i > 0) {D(i - 1); w(); A(i - 1); s(); A(i - 1); e(); B(i - 1); }}
	void B(int i) {if (i > 0) {C(i - 1); n(); B(i - 1); e(); B(i - 1); s(); A(i - 1); }}
	void C(int i) {if (i > 0) {B(i - 1); e(); C(i - 1); n(); C(i - 1); w(); D(i - 1); }}
	void D(int i) {if (i > 0) {A(i - 1); s(); D(i - 1); w(); D(i - 1); n(); C(i - 1); }}
	
	/*@formatter:on*/
}