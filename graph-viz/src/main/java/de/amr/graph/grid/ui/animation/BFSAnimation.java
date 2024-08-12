package de.amr.graph.grid.ui.animation;

import static java.lang.String.format;

import java.awt.Color;
import java.awt.Font;
import java.util.BitSet;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.ToDoubleFunction;

import de.amr.graph.core.api.TraversalState;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.GridRenderer;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.GraphSearch;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;

/**
 * Animation of BFS based grid traversals (BFS, A* etc.).
 * <p>
 * Grid cells are colored depending on their distance from the search start cell. The cell coloring always follows the
 * gradient from yellow to red in the HSV color model.
 * 
 * @author Armin Reichert
 */
public class BFSAnimation implements GraphSearchObserver {

	public static class Builder {

		private final BFSAnimation animation;

		private Builder() {
			animation = new BFSAnimation();
		}

		public Builder canvas(GridCanvas canvas) {
			animation.canvas = Objects.requireNonNull(canvas);
			return this;
		}

		public Builder distanceVisible(boolean distanceVisible) {
			animation.distanceVisible = distanceVisible;
			return this;
		}

		public Builder delay(IntSupplier fnDelay) {
			animation.delay.setMillis(Objects.requireNonNull(fnDelay));
			return this;
		}

		public Builder pathColor(Color pathColor) {
			animation.pathColor = Objects.requireNonNull(pathColor);
			return this;
		}

		public BFSAnimation build() {
			return animation;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private boolean distanceVisible;
	private Color pathColor;
	private ConfigurableGridRenderer mapRenderer;
	private GridCanvas canvas;
	private final DelayedRunner delay;

	private BFSAnimation() {
		delay = new DelayedRunner();
		distanceVisible = false;
		pathColor = Color.RED;
	}

	@Override
	public void edgeTraversed(int either, int other) {
		delay.run(() -> canvas.drawGridPassage(either, other, true));
	}

	@Override
	public void vertexAddedToFrontier(int vertex) {
		delay.run(() -> canvas.drawGridCell(vertex));
	}

	@Override
	public void vertexRemovedFromFrontier(int vertex) {
		delay.run(() -> canvas.drawGridCell(vertex));
	}

	@Override
	public void vertexStateChanged(int vertex, TraversalState oldState, TraversalState newState) {
		delay.run(() -> canvas.drawGridCell(vertex));
	}

	public void floodFill(GridPosition sourcePosition) {
		floodFill(canvas.getGrid().cell(sourcePosition));
	}

	public void floodFill(int source) {
		run(new BreadthFirstSearch(canvas.getGrid()), source, -1);
	}

	/**
	 * Runs an animation of a BFS from the given source vertex to the given target vertex. Cells are colored according to
	 * their distance from the source. Optionally the distance value is displayed.
	 * 
	 * @param bfs    BFS to be animated
	 * @param source source vertex
	 * @param target target vertex
	 */
	public void run(ObservableGraphSearch bfs, int source, int target) {
		// 1. explore graph to measure distances of all vertices reachable from source
		BreadthFirstSearch explorer = new BreadthFirstSearch(canvas.getGrid());
		explorer.exploreGraph(source);
		mapRenderer = deriveMapRenderer(canvas.getRenderer(), explorer);

		// 2. traverse graph again animating canvas
		canvas.pushRenderer(mapRenderer);
		bfs.addObserver(this);
		bfs.findPath(source, target);
		bfs.removeObserver(this);
		canvas.popRenderer();
	}

	/**
	 * Highlights the path from the source to the target cell. The explorer must have been run before calling this method.
	 * 
	 * @param explorer BFS used to explore the graph for computing distances
	 * @param source   source cell
	 * @param target   target cell
	 */
	public void showPath(GraphSearch explorer, int source, int target) {
		Path path = explorer.buildPath(target);
		if (path.numEdges() == 0) {
			return; // nothing to draw
		}
		// derive path renderer from map renderer or from current renderer
		if (mapRenderer != null) {
			canvas.pushRenderer(derivePathRenderer(mapRenderer, path, explorer::getCost));
		} else if (canvas.getRenderer() instanceof ConfigurableGridRenderer cgr) {
			canvas.pushRenderer(derivePathRenderer(cgr, path, explorer::getCost));
		} else {
			throw new IllegalStateException("Cannot derive path renderer from current grid renderer");
		}
		// draw path
		int prev = -1;
		for (int cell : path) {
			canvas.drawGridCell(cell);
			if (prev != -1) {
				canvas.drawGridPassage(prev, cell, true);
			}
			prev = cell;
		}

		canvas.popRenderer();
	}

	private ConfigurableGridRenderer deriveMapRenderer(GridRenderer base, GraphSearch explorer) {
		ConfigurableGridRenderer r = base instanceof PearlsGridRenderer ? new PearlsGridRenderer()
				: new WallPassageGridRenderer();
		r.fnCellBgColor = cell -> cellColorByDistance(cell, explorer::getCost, explorer.getMaxCost());
		r.fnCellSize = base.getModel()::getCellSize;
		r.fnGridBgColor = () -> base.getModel().getGridBgColor();
		r.fnPassageColor = (u, v) -> cellColorByDistance(u, explorer::getCost, explorer.getMaxCost());
		r.fnPassageWidth = base.getModel()::getPassageWidth;
		r.fnText = cell -> distanceVisible ? format("%.0f", explorer.getCost(cell)) : "";
		r.fnTextFont = cell -> new Font("Arial Narrow", Font.PLAIN, r.getPassageWidth(0, 0) / 2);
		r.fnTextColor = cell -> Color.BLACK;
		if (base instanceof PearlsGridRenderer br) {
			PearlsGridRenderer pr = (PearlsGridRenderer) r;
			pr.fnRelativePearlSize = br.fnRelativePearlSize;
		}
		return r;
	}

	private ConfigurableGridRenderer derivePathRenderer(ConfigurableGridRenderer base, Path path,
			ToDoubleFunction<Integer> fnDistance) {
		BitSet inPath = new BitSet();
		path.forEach(inPath::set);
		ConfigurableGridRenderer r = base instanceof PearlsGridRenderer ? new PearlsGridRenderer()
				: new WallPassageGridRenderer();
		r.fnCellBgColor = cell -> inPath.get(cell) ? pathColor : base.getCellBgColor(cell);
		r.fnCellSize = base::getCellSize;
		r.fnGridBgColor = base::getGridBgColor;
		r.fnPassageColor = (cell, dir) -> {
			int neighbor = (int) canvas.getGrid().neighbor(cell, dir).get();
			return inPath.get(cell) && inPath.get(neighbor) ? pathColor : base.getCellBgColor(cell);
		};
		r.fnPassageWidth = (u, v) -> base.getPassageWidth(u, v) > 5 ? base.getPassageWidth(u, v) / 2
				: base.getPassageWidth(u, v);
		r.fnText = cell -> distanceVisible ? format("%.0f", fnDistance.applyAsDouble(cell)) : "";
		r.fnTextFont = cell -> new Font("Arial Narrow", Font.PLAIN, r.getPassageWidth(0, 0) * 80 / 100);
		r.fnTextColor = cell -> Color.WHITE;
		if (base instanceof PearlsGridRenderer br) {
			PearlsGridRenderer pr = (PearlsGridRenderer) r;
			pr.fnRelativePearlSize = br.fnRelativePearlSize;
		}
		return r;
	}

	private Color cellColorByDistance(int cell, ToDoubleFunction<Integer> fnDistance, double maxDistance) {
		float hue = 0.16f;
		if (maxDistance > 0) {
			hue += 0.7f * fnDistance.applyAsDouble(cell) / maxDistance;
		}
		return Color.getHSBColor(hue, 0.5f, 1f);
	}
}