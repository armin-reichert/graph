package de.amr.graph.grid.curves;

/**
 * Curve using a "turtle".
 * 
 * @author Armin Reichert
 */
public abstract class TurtleCurve extends Curve {

	private Turtle4 turtle = new Turtle4();

	/**
	 * Turns right.
	 */
	protected void r() {
		turtle.turnRight();
	}

	/**
	 * Turns left.
	 */
	protected void l() {
		turtle.turnLeft();
	}

	/**
	 * Moves forward.
	 */
	protected void f() {
		go(turtle.ahead());
	}
}