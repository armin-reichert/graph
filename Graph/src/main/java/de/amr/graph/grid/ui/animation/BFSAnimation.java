package de.amr.graph.grid.ui.animation;

import static java.lang.String.format;

import java.awt.Color;
import java.awt.Font;
import java.util.BitSet;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import de.amr.graph.event.api.GraphTraversalObserver;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.GridRenderer;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * Animation of BFS based grid traversals (BFS, A* etc.).
 * <p>
 * Grid cells are colored depending on their distance from the search start cell. The cell coloring
 * always follows the gradient from yellow to red in the HSV color model.
 * 
 * @author Armin Reichert
 */
public class BFSAnimation {

	public static class Builder {

		private final BFSAnimation anim;

		private Builder() {
			anim = new BFSAnimation();
		}

		public Builder canvas(GridCanvas canvas) {
			anim.canvas = canvas;
			return this;
		}

		public Builder distanceVisible(boolean distanceVisible) {
			anim.distanceVisible = distanceVisible;
			return this;
		}

		public Builder delay(IntSupplier fnDelay) {
			anim.fnDelay = fnDelay;
			return this;
		}

		public Builder pathColor(Supplier<Color> fnPathColor) {
			anim.fnPathColor = fnPathColor;
			return this;
		}

		public BFSAnimation build() {
			return anim;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private GridCanvas canvas;
	private boolean distanceVisible;
	private IntSupplier fnDelay = () -> 0;
	private Supplier<Color> fnPathColor = () -> Color.RED;
	private ConfigurableGridRenderer distColorRenderer;

	private BFSAnimation() {
	}

	public void run(GraphSearch<?, ?> bfs, int source, int target) {
		canvas.getRenderer().ifPresent(canvasRenderer -> {
			// 1. explore graph to compute distances from source
			BreadthFirstSearch<?, ?> distExplorer = new BreadthFirstSearch<>(canvas.getGrid());
			distExplorer.exploreGraph(source);

			// Create renderer using computed distances
			distColorRenderer = createDistColorRenderer(canvasRenderer, distExplorer);
			canvas.pushRenderer(distColorRenderer);

			// 2. traverse graph again, now with events enabled
			GraphTraversalObserver canvasUpdater = new GraphTraversalObserver() {

				private void delayed(Runnable code) {
					try {
						Thread.sleep(fnDelay.getAsInt());
					} catch (InterruptedException e) {
						throw new AnimationInterruptedException();
					}
					code.run();
					canvas.repaint();
				}

				@Override
				public void edgeTraversed(int either, int other) {
					delayed(() -> canvas.drawGridPassage(either, other, true));
				}

				@Override
				public void vertexTraversed(int v, TraversalState oldState, TraversalState newState) {
					delayed(() -> canvas.drawGridCell(v));
				}
			};
			bfs.addObserver(canvasUpdater);
			bfs.exploreGraph(source, target);
			bfs.removeObserver(canvasUpdater);
			canvas.popRenderer();
		});
	}

	public void showPath(BreadthFirstSearch<?, ?> bfs, int source, int target) {
		canvas.getRenderer().ifPresent(canvasRenderer -> {
			List<Integer> path = bfs.findPath(source, target);
			if (distColorRenderer != null) {
				canvas.pushRenderer(createPathRenderer(distColorRenderer, bfs, path));
			} else {
				if (canvas.getRenderer().get() instanceof ConfigurableGridRenderer) {
					canvas.pushRenderer(
							createPathRenderer((ConfigurableGridRenderer) canvas.getRenderer().get(), bfs, path));
				} else {
					throw new IllegalStateException();
				}
			}
			path.forEach(canvas::drawGridCell);
			canvas.popRenderer();
		});
	}

	private ConfigurableGridRenderer createDistColorRenderer(GridRenderer base,
			BreadthFirstSearch<?, ?> distExplorer) {
		ConfigurableGridRenderer r = base instanceof PearlsGridRenderer ? new PearlsGridRenderer()
				: new WallPassageGridRenderer();
		r.fnCellBgColor = cell -> cellColorByDist(cell, distExplorer);
		r.fnCellSize = base.getModel()::getCellSize;
		r.fnGridBgColor = () -> base.getModel().getGridBgColor();
		r.fnPassageColor = (u, v) -> cellColorByDist(u, distExplorer);
		r.fnPassageWidth = base.getModel()::getPassageWidth;
		r.fnText = cell -> distanceVisible ? format("%.0f", distExplorer.getCost(cell)) : "";
		r.fnTextFont = () -> new Font(Font.SANS_SERIF, Font.PLAIN, r.getPassageWidth() / 2);
		r.fnTextColor = cell -> Color.BLACK;
		return r;
	}

	private ConfigurableGridRenderer createPathRenderer(ConfigurableGridRenderer base,
			BreadthFirstSearch<?, ?> bfs, List<Integer> path) {
		BitSet inPath = new BitSet();
		path.forEach(inPath::set);
		ConfigurableGridRenderer r = base instanceof PearlsGridRenderer ? new PearlsGridRenderer()
				: new WallPassageGridRenderer();
		r.fnCellBgColor = cell -> inPath.get(cell) ? fnPathColor.get() : base.getCellBgColor(cell);
		r.fnCellSize = () -> base.getCellSize();
		r.fnGridBgColor = () -> base.getGridBgColor();
		r.fnPassageColor = (cell, dir) -> {
			int neighbor = canvas.getGrid().neighbor(cell, dir).getAsInt();
			return inPath.get(cell) && inPath.get(neighbor) ? fnPathColor.get() : base.getCellBgColor(cell);
		};
		r.fnPassageWidth = () -> base.getPassageWidth() > 5 ? base.getPassageWidth() / 2 : base.getPassageWidth();
		r.fnText = cell -> distanceVisible ? format("%.0f", bfs.getCost(cell)) : "";
		r.fnTextFont = () -> new Font(Font.SANS_SERIF, Font.PLAIN, r.getPassageWidth() / 2);
		r.fnTextColor = cell -> Color.WHITE;
		return r;
	}

	private Color cellColorByDist(int cell, BreadthFirstSearch<?, ?> distExplorer) {
		float hue = 0.16f;
		if (distExplorer.getMaxCost() > 0) {
			hue += 0.7f * distExplorer.getCost(cell) / distExplorer.getMaxCost();
		}
		return Color.getHSBColor(hue, 0.5f, 1f);
	}
}