package de.amr.graph.grid.ui.animation;

import java.util.function.IntSupplier;

public abstract class AbstractAnimation {

	private IntSupplier fnDelay = () -> 0;

	public void setFnDelay(IntSupplier fnDelay) {
		this.fnDelay = fnDelay;
	}

	protected void delayed(Runnable code) {
		int delay = fnDelay.getAsInt();
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			throw new AnimationInterruptedException();
		}
		code.run();
	}
}