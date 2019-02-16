package de.amr.graph.grid.ui.animation;

import static java.lang.String.format;

import java.awt.Color;
import java.awt.Font;
import java.util.BitSet;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import de.amr.graph.event.api.GraphTraversalObserver;
import de.amr.graph.grid.api.GridPosition;
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

	/**
	 * Runs a "flood-fill" on the given grid.
	 * 
	 * @param canvas
	 *                          grid canvas
	 * @param source
	 *                          cell where flood-fill starts
	 * @param distanceVisible
	 *                          if distances should be displayed as text
	 */
	public static void floodFill(GridCanvas canvas, int source, boolean distanceVisible) {
		BreadthFirstSearch<?, ?> bfs = new BreadthFirstSearch<>(canvas.getGrid());
		BFSAnimation anim = new BFSAnimation(canvas);
		anim.setDistanceVisible(distanceVisible);
		anim.run(bfs, source, -1);
	}

	/**
	 * Runs a "flood-fill" on the given grid.
	 * 
	 * @param canvas
	 *                          grid canvas
	 * @param sourcePosition
	 *                          position of cell where flood-fill starts
	 * @param distanceVisible
	 *                          if distances should be displayed as text
	 */
	public static void floodFill(GridCanvas canvas, GridPosition sourcePosition, boolean distanceVisible) {
		floodFill(canvas, canvas.getGrid().cell(sourcePosition), distanceVisible);
	}

	private final GridCanvas canvas;
	private ConfigurableGridRenderer floodFillRenderer;
	private boolean distanceVisible;

	public IntSupplier fnDelay = () -> 0;
	public Supplier<Color> fnPathColor = () -> Color.RED;

	public BFSAnimation(GridCanvas canvas) {
		this.canvas = canvas;
		distanceVisible = true;
	}

	public boolean isDistanceVisible() {
		return distanceVisible;
	}

	public void setDistanceVisible(boolean visible) {
		this.distanceVisible = visible;
	}

	public Color getPathColor() {
		return fnPathColor.get();
	}

	public void run(GraphSearch<?, ?> bfs, int source, int target) {
		canvas.getRenderer().ifPresent(canvasRenderer -> {
			// 1. traverse complete graph for computing distances from source
			BreadthFirstSearch<?, ?> distancesComputation = new BreadthFirstSearch<>(canvas.getGrid());
			distancesComputation.exploreGraph(source);

			// Create renderer using computed distances for coloring
			floodFillRenderer = createFloodFillRenderer(canvasRenderer, distancesComputation);
			canvas.pushRenderer(floodFillRenderer);

			// 2. traverse graph again, now with events enabled
			GraphTraversalObserver canvasUpdater = new GraphTraversalObserver() {

				private void delayed(Runnable code) {
					if (fnDelay.getAsInt() > 0) {
						try {
							Thread.sleep(fnDelay.getAsInt());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
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

	public void showPath(GraphSearch<?, ?> bfs, int source, int target) {
		canvas.getRenderer().ifPresent(canvasRenderer -> {
			Iterable<Integer> path = bfs.findPath(source, target);
			if (floodFillRenderer != null) {
				canvas.pushRenderer(createPathRenderer(floodFillRenderer, bfs, path));
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

	private ConfigurableGridRenderer createFloodFillRenderer(GridRenderer base,
			BreadthFirstSearch<?, ?> distanceMap) {
		ConfigurableGridRenderer r = base instanceof PearlsGridRenderer ? new PearlsGridRenderer()
				: new WallPassageGridRenderer();
		r.fnCellBgColor = cell -> colorByDist(cell, distanceMap);
		r.fnCellSize = base.getModel()::getCellSize;
		r.fnGridBgColor = () -> base.getModel().getGridBgColor();
		r.fnPassageColor = (u, v) -> colorByDist(u, distanceMap);
		r.fnPassageWidth = base.getModel()::getPassageWidth;
		r.fnText = cell -> distanceVisible ? format("%.0f", distanceMap.getCost(cell)) : "";
		r.fnTextFont = () -> new Font(Font.SANS_SERIF, Font.PLAIN, r.getPassageWidth() / 2);
		r.fnTextColor = cell -> Color.BLACK;
		return r;
	}

	private ConfigurableGridRenderer createPathRenderer(ConfigurableGridRenderer base, GraphSearch<?, ?> search,
			Iterable<Integer> path) {
		BitSet inPath = new BitSet();
		path.forEach(inPath::set);
		ConfigurableGridRenderer r = base instanceof PearlsGridRenderer ? new PearlsGridRenderer()
				: new WallPassageGridRenderer();
		r.fnCellBgColor = cell -> inPath.get(cell) ? getPathColor() : base.getCellBgColor(cell);
		r.fnCellSize = () -> base.getCellSize();
		r.fnGridBgColor = () -> base.getGridBgColor();
		r.fnPassageColor = (cell, dir) -> {
			int neighbor = canvas.getGrid().neighbor(cell, dir).getAsInt();
			return inPath.get(cell) && inPath.get(neighbor) ? getPathColor() : base.getCellBgColor(cell);
		};
		r.fnPassageWidth = () -> base.getPassageWidth() > 5 ? base.getPassageWidth() / 2 : base.getPassageWidth();
		r.fnText = cell -> distanceVisible ? format("%.0f", search.getCost(cell)) : "";
		r.fnTextFont = () -> new Font(Font.SANS_SERIF, Font.PLAIN, r.getPassageWidth() / 2);
		r.fnTextColor = cell -> Color.WHITE;
		return r;
	}

	private static Color colorByDist(int cell, BreadthFirstSearch<?, ?> distancesComputation) {
		float hue = 0.16f;
		if (distancesComputation.getMaxCost() > 0) {
			hue += 0.7f * distancesComputation.getCost(cell) / distancesComputation.getMaxCost();
		}
		return Color.getHSBColor(hue, 0.5f, 1f);
	}
}