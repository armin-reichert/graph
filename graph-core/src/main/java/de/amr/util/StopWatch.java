package de.amr.util;

/**
 * Simple stop-watch with nanosecond precision.
 * 
 * @author Armin Reichert
 */
public class StopWatch {

	private long start;
	private float nanos;

	/**
	 * Starts the watch.
	 */
	public void start() {
		nanos = 0;
		start = System.nanoTime();
	}

	/**
	 * Stops the watch and stores the time passed since start.
	 */
	public void stop() {
		nanos = System.nanoTime() - start;
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
		return nanos / 1_000_000_000f;
	}

	/**
	 * @return measured time in milliseconds
	 */
	public float getMillis() {
		return nanos / 1_000_000f;
	}

	/**
	 * @return measured time in milliseconds
	 */
	public float getMillis() {
		return measuredNanos / 1_000_000f;
	}

	/**
	 * @return measured time in nanoseconds
	 */
	public float getNanos() {
		return nanos;
	}
}