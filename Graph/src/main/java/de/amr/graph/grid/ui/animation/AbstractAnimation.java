package de.amr.graph.grid.ui.animation;

import java.util.function.IntSupplier;

public abstract class AbstractAnimation {

	private IntSupplier fnDelay = () -> 0;

	protected void setFnDelay(IntSupplier fnDelay) {
		this.fnDelay = fnDelay;
	}

	protected void delayed(Runnable code) {
		try {
			Thread.sleep(fnDelay.getAsInt());
		} catch (InterruptedException e) {
			throw new AnimationInterruptedException();
		}
		code.run();
	}
}
