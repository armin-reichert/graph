package de.amr.graph.grid.ui.animation;

import java.util.function.IntSupplier;

public abstract class AbstractAnimation {

	private static final long ONE_MILLION = 1_000_000L;

	private IntSupplier fnDelay = () -> 0;
	private long lastTime;

	public void setFnDelay(IntSupplier fnDelay) {
		this.fnDelay = fnDelay;
	}

	protected void delayed(Runnable code) {
		long delayNanos = fnDelay.getAsInt() * ONE_MILLION;
		code.run();
		long deltaTime = System.nanoTime() - lastTime;
		lastTime = System.nanoTime();
		if (deltaTime < delayNanos) {
			long sleep = delayNanos - deltaTime;
			try {
				Thread.sleep(sleep / ONE_MILLION);
			} catch (InterruptedException e) {
				throw new AnimationInterruptedException();
			}
		}
	}
}