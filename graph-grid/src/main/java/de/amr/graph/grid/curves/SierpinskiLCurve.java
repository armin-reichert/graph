package de.amr.graph.grid.curves;

import static de.amr.graph.grid.impl.Grid8Topology.E;
import static de.amr.graph.grid.impl.Grid8Topology.N;
import static de.amr.graph.grid.impl.Grid8Topology.NE;
import static de.amr.graph.grid.impl.Grid8Topology.NW;
import static de.amr.graph.grid.impl.Grid8Topology.S;
import static de.amr.graph.grid.impl.Grid8Topology.SE;
import static de.amr.graph.grid.impl.Grid8Topology.SW;
import static de.amr.graph.grid.impl.Grid8Topology.W;

/**
 * Sierpinski curve (as defined in Niklaus Wirth, "Algorithmen und Datenstrukturen").
 * 
 * @author Armin Reichert
 */
public class SierpinskiLCurve extends Curve {

	public SierpinskiLCurve(int depth) {
		s(depth);
	}

	/*@formatter:off*/
	void s(int i) { if (i > 0) { a(i-1); go(SE); b(i-1); go(SW); c(i-1); go(NW); d(i-1); go(NE); }}
	void a(int i) { if (i > 0) { a(i-1); go(SE); b(i-1); go(E); go(E); d(i-1); go(NE); a(i-1); }}
	void b(int i) { if (i > 0) { b(i-1); go(SW); c(i-1); go(S); go(S); a(i-1); go(SE); b(i-1); }}
	void c(int i) { if (i > 0) { c(i-1); go(NW); d(i-1); go(W); go(W); b(i-1); go(SW); c(i-1); }}
	void d(int i) { if (i > 0) { d(i-1); go(NE); a(i-1); go(N); go(N); c(i-1); go(NW); d(i-1); }}
	/*@formatter:on*/
}