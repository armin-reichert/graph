package de.amr.graph.grid.curves;

/**
 * Computes a Moore curve from the following L-system:
 * <p>
 * <code>
 * S -> L f L l f l L f L <br/>
 * L -> r R f l L f L l f R r <br/>
 * R -> l L f r R f R r f L l </br>
 * </code>
 * <p>
 * with nonterminals <code>{S, L, R}</code>, axiom <code>S</code> and terminals
 * <code>{f, +, -}</code>.
 * <p>
 * The terminals are interpreted as follows:
 * <ul>
 * <li><code>f</code> = go forward
 * <li><code>l</code> = turn 90&deg; left (counter-clockwise)
 * <li><code>r</code> = turn 90&deg; right (clockwise).
 * </ul>
 * <p>
 * On a <code>(n x n)</code>-grid, the curve starts at <code>column = n / 2 - 1, row = n - 1</code>
 * where <code>n</code> has to be a power of 2.
 *
 * @author Armin Reichert
 * 
 * @see http://cph.phys.spbu.ru/ACOPhys/materials/bader/sfc.pdf
 * @see https://en.wikipedia.org/wiki/Moore_curve
 */
public class MooreLCurve extends Curve {

	public MooreLCurve(int depth) {
		S(depth);
	}

	Compass4 head = new Compass4();

	/*@formatter:off*/

	// Terminals
	void l() { head.turnLeft(); }
	void r() { head.turnRight(); }
	void f() { go(head.ahead()); }

	// Rules
	void S(int i) {	if (i > 0) { L(i-1); f(); L(i-1); l(); f(); l(); L(i-1); f(); L(i-1); }}
	void L(int i) { if (i > 0) { r(); R(i-1); f(); l(); L(i-1); f(); L(i-1); l(); f(); R(i-1); r(); }}
	void R(int i) { if (i > 0) { l(); L(i-1); f(); r(); R(i-1); f(); R(i-1); r(); f(); L(i-1); l(); }}
	
	/*@formatter:on*/
}