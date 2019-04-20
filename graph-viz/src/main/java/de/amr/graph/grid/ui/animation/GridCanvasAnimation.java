package de.amr.graph.grid.ui.animation;

import java.util.function.IntSupplier;

import de.amr.graph.core.api.ObservableGraph;
import de.amr.graph.event.EdgeEvent;
import de.amr.graph.event.GraphObserver;
import de.amr.graph.event.VertexEvent;
import de.amr.graph.grid.ui.rendering.GridCanvas;

public class GridCanvasAnimation<V, E> implements GraphObserver<V, E> {

	public static void pause(float seconds) {
		try {
			Thread.sleep(Math.round(seconds * 1000));
		} catch (InterruptedException e) {
			throw new AnimationInterruptedException();
		}
	}

	private final GridCanvas canvas;
	private boolean enabled;

	/** Function supplying the delay in milliseconds. */
	public IntSupplier fnDelay;

	public GridCanvasAnimation(GridCanvas canvas) {
		this.canvas = canvas;
		enabled = true;
		fnDelay = () -> 0;
	}

	public int getDelay() {
		return fnDelay.getAsInt();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void vertexChanged(VertexEvent<V, E> event) {
		if (enabled) {
			delayed(() -> canvas.drawGridCell(event.getVertex()));
		}
	}

	@Override
	public void edgeAdded(EdgeEvent<V, E> event) {
		if (enabled) {
			delayed(() -> canvas.drawGridPassage(event.getEither(), event.getOther(), true));
		}
	}

	@Override
	public void edgeRemoved(EdgeEvent<V, E> event) {
		if (enabled) {
			delayed(() -> canvas.drawGridPassage(event.getEither(), event.getOther(), false));
		}
	}

	@Override
	public void edgeChanged(EdgeEvent<V, E> event) {
		if (enabled) {
			delayed(() -> canvas.drawGridPassage(event.getEither(), event.getOther(), true));
		}
	}

	@Override
	public void graphChanged(ObservableGraph<V, E> graph) {
		if (enabled) {
			canvas.drawGrid();
		}
	}

	private void delayed(Runnable code) {
		try {
			Thread.sleep(fnDelay.getAsInt());
		} catch (InterruptedException e) {
			throw new AnimationInterruptedException();
		}
		code.run();
		canvas.repaint();
	}
}