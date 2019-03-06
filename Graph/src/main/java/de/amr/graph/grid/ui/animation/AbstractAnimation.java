package de.amr.graph.grid.ui.animation;

import java.util.function.IntSupplier;

public abstract class AbstractAnimation {

	private IntSupplier fnDelay = () -> 0;

	public void setFnDelay(IntSupplier fnDelay) {
		this.fnDelay = fnDelay;
	}

	protected void delayed(Runnable code) {
		long codeNanos = System.nanoTime();
		code.run();
		codeNanos = System.nanoTime() - codeNanos;
		int delayMillis = fnDelay.getAsInt();
		long sleep = Math.max(0, delayMillis - codeNanos / 1000000);
		if (sleep > 0) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				throw new AnimationInterruptedException();
			}
		}
	}
}