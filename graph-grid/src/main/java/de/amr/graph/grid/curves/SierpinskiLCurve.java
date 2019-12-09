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
		S(depth);
	}

	/*@formatter:off*/
	void S(int i) { if (i > 0) { A(i-1); go(SE); B(i-1); go(SW); C(i-1); go(NW); D(i-1); go(NE); }}
	void A(int i) { if (i > 0) { A(i-1); go(SE); B(i-1); go(E); go(E); D(i-1); go(NE); A(i-1); }}
	void B(int i) { if (i > 0) { B(i-1); go(SW); C(i-1); go(S); go(S); A(i-1); go(SE); B(i-1); }}
	void C(int i) { if (i > 0) { C(i-1); go(NW); D(i-1); go(W); go(W); B(i-1); go(SW); C(i-1); }}
	void D(int i) { if (i > 0) { D(i-1); go(NE); A(i-1); go(N); go(N); C(i-1); go(NW); D(i-1); }}
	/*@formatter:on*/
}