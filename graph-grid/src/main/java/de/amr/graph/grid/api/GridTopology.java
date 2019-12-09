package de.amr.graph.grid.api;

import java.util.stream.IntStream;

/**
 * The topology of a grid.
 * 
 * @author Armin Reichert
 */
public interface GridTopology {

	/**
	 * @return stream of the directions of this topology
	 */
	IntStream dirs();

	/**
	 * @return the number of directions of this topology
	 */
	byte dirCount();

	/**
	 * Readable name of direction.
	 * 
	 * @param dir
	 *              direction vaue
	 * @return readable name
	 */
	String name(int dir);

	/**
	 * TODO: this make no sense for odd number of directions
	 * 
	 * @param dir
	 *              direction
	 * @return opposite of given direction
	 */
	byte inv(int dir);

	/**
	 * @param dir
	 *              direction
	 * @return direction left (counter-clockwise) of given direction
	 */
	byte left(int dir);

	/**
	 * @param dir
	 *              direction
	 * @return direction right (clockwise) of given direction
	 */
	byte right(int dir);

	/**
	 * @param dir
	 *              direction
	 * @return x-difference when moving towards given direction
	 */
	byte dx(int dir);

	/**
	 * 
	 * @param dir
	 *              direction
	 * @return y-difference when moving towards given direction
	 */
	byte dy(int dir);
}