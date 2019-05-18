package de.amr.graph.grid.curves;

/**
 * Implements a Hilbert curve using the following L-system:
 * <p>
 * <code>
 * A → − B f + A f A + f B − <br/>
 * B → + A f − B f B − f A + <br/>
 * <p>
 * </code> with non-terminal symbols <code>{A,B}</code>, axiom <code>A</code> and terminal symbols
 * <code>{f,+,-}</code>.
 * <p>
 * The terminal symbols are interpreted as follows:
 * <ul>
 * <li><code>f</code> = go forward
 * <li><code>+</code> = turn left (90&deg; counter-clockwise)
 * <li><code>-</code> = turn right (90&deg; clockwise).
 * </ul>
 * <p>
 * As given, the curve starts at the lower left corner and ends at the upper right corner of the
 * grid.
 * 
 * @author Armin Reichert
 */
public class HilbertLCurve extends TurtleCurve {

	public HilbertLCurve(int depth) {
		A(depth);
	}

	/*@formatter:off*/
	void A(int i) { if (i > 0) { r(); B(i-1); f(); l(); A(i-1); f(); A(i-1); l(); f(); B(i-1); r(); }}
	void B(int i) { if (i > 0) { l(); A(i-1); f(); r(); B(i-1); f(); B(i-1); r(); f(); A(i-1); l(); }}
	/*@formatter:on*/
}