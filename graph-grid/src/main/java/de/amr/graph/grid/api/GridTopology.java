package de.amr.graph.grid.api;

import java.util.stream.Stream;

/**
 * The topology of a grid.
 * 
 * @author Armin Reichert
 */
public interface GridTopology {

	/**
	 * @return stream of the directions of this topology
	 */
	Stream<Byte> dirs();

	/**
	 * @param dir direction
	 * @return <code>true</code> if the specified value is a valid direction
	 */
	boolean isValid(int dir);

	/**
	 * @param dir direction
	 * @return <code>true</code> if the specified direction is horizontal or
	 *         vertical and not diagonal.
	 */
	boolean isOrthogonal(int dir);

	/**
	 * @return the number of directions of this topology
	 */
	byte dirCount();

	/**
	 * Readable name of direction.
	 * 
	 * @param dir direction vaue
	 * @return readable name
	 */
	String name(int dir);

	/**
	 * TODO: this make no sense for odd number of directions
	 * 
	 * @param dir direction
	 * @return opposite of given direction
	 */
	byte inv(int dir);

	/**
	 * @param dir direction
	 * @return direction left (counter-clockwise) of given direction
	 */
	byte left(int dir);

	/**
	 * @param dir direction
	 * @return direction right (clockwise) of given direction
	 */
	byte right(int dir);

	/**
	 * @param dir direction
	 * @return x-difference when moving towards given direction
	 */
	byte dx(int dir);

	/**
	 * 
	 * @param dir direction
	 * @return y-difference when moving towards given direction
	 */
	byte dy(int dir);
}