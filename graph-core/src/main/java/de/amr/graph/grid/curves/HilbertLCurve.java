package de.amr.graph.grid.curves;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.amr.graph.grid.api.CellSequence;

/**
 * Implementation of a Hilbert curve using the following L-system:
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
 * <li><code>+</code> = turn 90&deg; counter-clockwise
 * <li><code>-</code> = turn 90&deg; clockwise.
 * </ul>
 * <p>
 * As given, the curve starts at the lower left corner and ends at the upper right corner of the
 * grid.
 * 
 * @author Armin Reichert
 */
public class HilbertLCurve implements CellSequence {

	List<Integer> dirs = new ArrayList<>();
	Compass4 head = new Compass4();

	/*@formatter:off*/
	// terminal symbols:
	void m() {head.turnRight();}
	void p() {head.turnLeft();}
	void f() {dirs.add(head.ahead());}
	
	// non-terminal symbols:
	void A(int i) {if (i > 0) { m();B(i-1);f();p();A(i-1);f();A(i-1);p();f();B(i-1);m(); }}
	void B(int i) {if (i > 0) {	p();A(i-1);f();m();B(i-1);f();B(i-1);m();f();A(i-1);p(); }}
	/*@formatter:on*/

	@Override
	public Iterator<Integer> iterator() {
		return dirs.iterator();
	}

	public HilbertLCurve(int i) {
		A(i);
	}
}