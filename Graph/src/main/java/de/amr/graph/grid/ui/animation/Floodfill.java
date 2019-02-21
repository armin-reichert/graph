package de.amr.graph.grid.ui.animation;

import java.util.function.IntSupplier;

import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;

/**
 * Configurable flood-fill on a grid.
 * 
 * @author Armin Reichert
 */
public class Floodfill {

	public static class Builder {

		private final Floodfill ff;

		public Builder() {
			ff = new Floodfill();
		}

		public Builder canvas(GridCanvas canvas) {
			ff.canvas = canvas;
			return this;
		}

		public Builder source(int source) {
			ff.source = source;
			return this;
		}

		public Builder source(GridPosition sourcePosition) {
			ff.source = ff.canvas.getGrid().cell(sourcePosition);
			return this;
		}

		public Builder distanceVisible(boolean distanceVisible) {
			ff.distanceVisible = distanceVisible;
			return this;
		}

		public Builder delay(IntSupplier fnDelay) {
			ff.fnDelay = fnDelay;
			return this;
		}

		public Floodfill build() {
			if (ff.canvas == null) {
				throw new IllegalStateException();
			}
			return ff;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private GridCanvas canvas;
	private int source = 0;
	private boolean distanceVisible = false;
	private IntSupplier fnDelay = () -> 0;

	private Floodfill() {
	}

	public void run() {
		BFSAnimation.builder().canvas(canvas).delay(fnDelay).distanceVisible(distanceVisible).build()
				.run(new BreadthFirstSearch<>(canvas.getGrid()), source, -1);
	}
}
