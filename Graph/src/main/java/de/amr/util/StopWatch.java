package de.amr.util;

/**
 * Simple stopwatch with nanosecond precision.
 * 
 * @author Armin Reichert
 */
public class StopWatch {

	private long startNanos;
	private float measuredNanos;

	/**
	 * Starts the watch.
	 */
	public void start() {
		measuredNanos = 0;
		startNanos = System.nanoTime();
	}

	/**
	 * Stops the watch and stores the time passed since start.
	 */
	public void stop() {
		measuredNanos = System.nanoTime() - startNanos;
	}

	/**
	 * Measures execution of given code.
	 * 
	 * @param code
	 *               code that is executed and measured
	 */
	public void measure(Runnable code) {
		start();
		code.run();
		stop();
	}

	/**
	 * @return measured time in seconds
	 */
	public float getSeconds() {
		return measuredNanos / 1_000_000_000f;
	}

	/**
	 * @return measured time in nanoseconds
	 */
	public float getNanos() {
		return measuredNanos;
	}
}