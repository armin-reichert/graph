package de.amr.easy.grid.impl.curves;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.amr.easy.grid.api.CellSequence;

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

	private final List<Integer> dirs = new ArrayList<>();

	private final Compass4 compass = new Compass4();

	// non-terminal symbol interpretations:

	private void minus() {
		compass.turnRight();
	}

	private void plus() {
		compass.turnLeft();
	}

	private void f() {
		dirs.add(compass.ahead());
	}

	@Override
	public Iterator<Integer> iterator() {
		return dirs.iterator();
	}

	public HilbertLCurve(int i) {
		A(i);
	}

	/**
	 * <code>A → − B f + A f A + f B −</code>
	 * 
	 * @param i
	 *            the recursion depth
	 */
	private void A(int i) {
		if (i > 0) {
			minus();
			B(i - 1);
			f();
			plus();
			A(i - 1);
			f();
			A(i - 1);
			plus();
			f();
			B(i - 1);
			minus();
		}
	}

	/**
	 * <code>B → + A f − B f B − f A +</code>
	 * 
	 * @param i
	 *            the recursion depth
	 */
	private void B(int i) {
		if (i > 0) {
			plus();
			A(i - 1);
			f();
			minus();
			B(i - 1);
			f();
			B(i - 1);
			minus();
			f();
			A(i - 1);
			plus();
		}
	}
}