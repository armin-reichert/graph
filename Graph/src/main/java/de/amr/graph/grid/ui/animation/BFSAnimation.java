package de.amr.graph.grid.ui.animation;

import static java.lang.String.format;

import java.awt.Color;
import java.awt.Font;
import java.util.BitSet;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.ToDoubleFunction;

import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.GridRenderer;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
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
public class BFSAnimation extends AbstractAnimation {

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
			anim.setFnDelay(fnDelay);
			return this;
		}

		public Builder pathColor(Color pathColor) {
			anim.pathColor = pathColor;
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
	private Color pathColor;
	private ConfigurableGridRenderer mapRenderer;
	private GraphSearchObserver canvasUpdater = new GraphSearchObserver() {

		@Override
		public void edgeTraversed(int either, int other) {
			delayed(() -> canvas.drawGridPassage(either, other, true));
		}

		@Override
		public void vertexStateChanged(int v, TraversalState oldState, TraversalState newState) {
			delayed(() -> canvas.drawGridCell(v));
		}

		@Override
		public void vertexAddedToFrontier(int vertex) {
		}

		@Override
		public void vertexRemovedFromFrontier(int vertex) {
		}
	};

	private BFSAnimation() {
		pathColor = Color.RED;
	}
	
	//TODO
	public void setMapRenderer(ConfigurableGridRenderer mapRenderer) {
		this.mapRenderer = mapRenderer;
	}
	

	/**
	 * Runs an animation of a BFS from the given source vertex to the given target vertex. Cells are
	 * colored according to their distance from the source. Optionally the distance value is displayed.
	 * 
	 * @param bfs
	 *                 BFS to be animated
	 * @param source
	 *                 source vertex
	 * @param target
	 *                 target vertex
	 */
	public void run(GraphSearch<?, ?> bfs, int source, int target) {
		canvas.getRenderer().ifPresent(canvasRenderer -> {
			// 1. explore graph to measure distances of all vertices reachable from source
			BreadthFirstSearch<?, ?> distMeasurer = new BreadthFirstSearch<>(canvas.getGrid());
			distMeasurer.exploreGraph(source);
			mapRenderer = createMapRenderer(canvasRenderer, distMeasurer::getCost, distMeasurer.getMaxCost());
			canvas.pushRenderer(mapRenderer);

			// 2. traverse graph with events enabled
			bfs.addObserver(canvasUpdater);
			bfs.exploreGraph(source, target);
			bfs.removeObserver(canvasUpdater);
			canvas.popRenderer();
		});
	}

	/**
	 * Highlights the path from the source to the target cell. The BFS search must have been run before
	 * calling this method.
	 * 
	 * @param bfs
	 *                 BFS
	 * @param source
	 *                 source cell
	 * @param target
	 *                 target cell
	 */
	public void showPath(BreadthFirstSearch<?, ?> bfs, int source, int target) {
		canvas.getRenderer().ifPresent(canvasRenderer -> {
			List<Integer> path = bfs.buildPath(source, target);
			if (path.isEmpty()) {
				return;
			}
			if (mapRenderer != null) {
				canvas.pushRenderer(createPathRenderer(mapRenderer, path, bfs::getCost));
			} else if (canvasRenderer instanceof ConfigurableGridRenderer) {
				canvas
						.pushRenderer(createPathRenderer((ConfigurableGridRenderer) canvasRenderer, path, bfs::getCost));
			} else {
				throw new IllegalStateException();
			}
			path.forEach(canvas::drawGridCell);
			canvas.popRenderer();
		});
	}

	private ConfigurableGridRenderer createMapRenderer(GridRenderer base, ToDoubleFunction<Integer> distance,
			double maxDistance) {
		ConfigurableGridRenderer r = base instanceof PearlsGridRenderer ? new PearlsGridRenderer()
				: new WallPassageGridRenderer();
		r.fnCellBgColor = cell -> cellColorByDistance(cell, distance, maxDistance);
		r.fnCellSize = base.getModel()::getCellSize;
		r.fnGridBgColor = () -> base.getModel().getGridBgColor();
		r.fnPassageColor = (u, v) -> cellColorByDistance(u, distance, maxDistance);
		r.fnPassageWidth = base.getModel()::getPassageWidth;
		r.fnText = cell -> distanceVisible ? format("%.0f", distance.applyAsDouble(cell)) : "";
		r.fnTextFont = () -> new Font(Font.SANS_SERIF, Font.PLAIN, r.getPassageWidth(0, 0) / 2);
		r.fnTextColor = cell -> Color.BLACK;
		return r;
	}

	private ConfigurableGridRenderer createPathRenderer(ConfigurableGridRenderer base, List<Integer> path,
			ToDoubleFunction<Integer> distance) {
		BitSet inPath = new BitSet();
		path.forEach(inPath::set);
		ConfigurableGridRenderer r = base instanceof PearlsGridRenderer ? new PearlsGridRenderer()
				: new WallPassageGridRenderer();
		r.fnCellBgColor = cell -> inPath.get(cell) ? pathColor : base.getCellBgColor(cell);
		r.fnCellSize = () -> base.getCellSize();
		r.fnGridBgColor = () -> base.getGridBgColor();
		r.fnPassageColor = (cell, dir) -> {
			int neighbor = canvas.getGrid().neighbor(cell, dir).getAsInt();
			return inPath.get(cell) && inPath.get(neighbor) ? pathColor : base.getCellBgColor(cell);
		};
		r.fnPassageWidth = (u, v) -> base.getPassageWidth(u, v) > 5 ? base.getPassageWidth(u, v) / 2
				: base.getPassageWidth(u, v);
		r.fnText = cell -> distanceVisible ? format("%.0f", distance.applyAsDouble(cell)) : "";
		r.fnTextFont = () -> new Font(Font.SANS_SERIF, Font.PLAIN, r.getPassageWidth(0, 0) / 2);
		r.fnTextColor = cell -> Color.WHITE;
		return r;
	}

	private Color cellColorByDistance(int cell, ToDoubleFunction<Integer> distance, double maxDistance) {
		float hue = 0.16f;
		if (maxDistance > 0) {
			hue += 0.7f * distance.applyAsDouble(cell) / maxDistance;
		}
		return Color.getHSBColor(hue, 0.5f, 1f);
	}
}