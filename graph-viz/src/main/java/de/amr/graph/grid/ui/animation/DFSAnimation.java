package de.amr.graph.grid.ui.animation;

import java.awt.Color;
import java.util.BitSet;
import java.util.function.IntSupplier;

import de.amr.graph.core.api.TraversalState;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.GridRenderer;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.ObservableGraphSearch;
import de.amr.graph.pathfinder.api.Path;

/**
 * Animation of depth-first search based algorithms.
 * 
 * @author Armin Reichert
 */
public class DFSAnimation implements GraphSearchObserver {

	public static class Builder {

		private final DFSAnimation anim;

		public Builder() {
			anim = new DFSAnimation();
		}

		public Builder canvas(GridCanvas canvas) {
			anim.canvas = canvas;
			return this;
		}

		public Builder delay(IntSupplier fnDelay) {
			anim.delay.setMillis(fnDelay);
			return this;
		}

		public Builder pathColor(Color color) {
			anim.pathColor = color;
			return this;
		}

		public DFSAnimation build() {
			return anim;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private ObservableGraphSearch dfs;
	private GridCanvas canvas;
	private Color pathColor;
	private final BitSet inPath;
	private final DelayedRunner delay;

	private DFSAnimation() {
		pathColor = Color.RED;
		delay = new DelayedRunner();
		inPath = new BitSet();
	}

	@Override
	public void vertexAddedToFrontier(int vertex) {
		delay.run(() -> drawPath(vertex));
	}

	@Override
	public void vertexRemovedFromFrontier(int vertex) {
		delay.run(() -> drawPath(vertex));
	}

	private void drawPath(int vertex) {
		inPath.clear();
		int v = vertex;
		while (v != -1) {
			inPath.set(v);
			v = dfs.getParent(v);
		}
		canvas.getGrid().vertices().forEach(cell -> {
			if (dfs.getState(cell) != TraversalState.UNVISITED) {
				canvas.drawGridCell(cell);
			}
		});
	}

	private ConfigurableGridRenderer createPathRenderer(GridRenderer base) {
		ConfigurableGridRenderer r = base instanceof PearlsGridRenderer ? new PearlsGridRenderer()
				: new WallPassageGridRenderer();
		r.fnCellSize = base.getModel()::getCellSize;
		r.fnPassageWidth = (u, v) -> base.getModel().getPassageWidth(u, v) > 5 ? base.getModel().getPassageWidth(u, v) / 2
				: base.getModel().getPassageWidth(u, v);
		r.fnCellBgColor = cell -> {
			if (inPath.get(cell)) {
				return pathColor;
			}
			return Color.WHITE;
		};
		r.fnPassageColor = (cell, dir) -> {
			int neighbor = (int) canvas.getGrid().neighbor(cell, dir).get();
			if (inPath.get(cell) && inPath.get(neighbor)) {
				return pathColor;
			}
			return Color.WHITE;
		};
		if (base instanceof PearlsGridRenderer br) {
			PearlsGridRenderer pr = (PearlsGridRenderer) r;
			pr.fnRelativePearlSize = br.fnRelativePearlSize;
		}
		return r;
	}

	public void run(ObservableGraphSearch search, int source, int target) {
		this.dfs = search;
		GridRenderer canvasRenderer = canvas.getRenderer();
		canvas.pushRenderer(createPathRenderer(canvasRenderer));
		search.addObserver(this);
		Path path = search.findPath(source, target);
		search.removeObserver(this);
		canvas.drawGrid();
		path.forEach(v -> {
			int w = search.getParent(v);
			canvas.drawGridCell(v);
			if (w != -1) {
				canvas.drawGridPassage(v, w, true);
			}
		});
		canvas.popRenderer();
	}
}