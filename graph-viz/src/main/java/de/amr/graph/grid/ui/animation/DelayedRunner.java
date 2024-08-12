package de.amr.graph.grid.ui.animation;

import java.util.function.IntSupplier;

public final class DelayedRunner {

	private static final long ONE_MILLION = 1_000_000L;

	private IntSupplier fnMillis = () -> 0;
	private long lastCallTime;

	/**
	 * Sets the supply function for the delay time in milliseconds.
	 * 
	 * @param fnMillis function supplying delay time in milliseconds
	 */
	public void setMillis(IntSupplier fnMillis) {
		this.fnMillis = fnMillis;
	}

	/**
	 * Runs the given code after waiting for the current delay time. If the last call to this method happened before more
	 * than one delay interval, it is run immediately.
	 * 
	 * @param code code to be executed
	 */
	public void run(Runnable code) {
		long delayNanos = fnMillis.getAsInt() * ONE_MILLION;
		code.run();
		long timeSinceLastCall = System.nanoTime() - lastCallTime;
		lastCallTime = System.nanoTime();
		if (timeSinceLastCall < delayNanos) {
			long sleepMillis = (delayNanos - timeSinceLastCall) / ONE_MILLION;
			try {
				Thread.sleep(sleepMillis);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}