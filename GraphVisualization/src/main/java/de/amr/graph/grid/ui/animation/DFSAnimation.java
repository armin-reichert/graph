package de.amr.graph.grid.ui.animation;

import java.awt.Color;
import java.util.BitSet;
import java.util.List;
import java.util.function.IntSupplier;

import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.GridRenderer;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * Animation of depth-first search based algorithms.
 * 
 * @author Armin Reichert
 */
public class DFSAnimation extends AbstractAnimation {

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
			anim.setFnDelay(fnDelay);
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

	private GraphSearch<?, ?> dfs;
	private GridCanvas canvas;
	private Color pathColor = Color.RED;
	private BitSet inPath = new BitSet();
	private GraphSearchObserver canvasUpdater = new GraphSearchObserver() {

		@Override
		public void vertexAddedToFrontier(int vertex) {
			delayed(() -> drawPath(vertex));
		}

		@Override
		public void vertexRemovedFromFrontier(int vertex) {
			delayed(() -> drawPath(vertex));
		}
	};

	private DFSAnimation() {
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
		r.fnPassageWidth = (u, v) -> base.getModel().getPassageWidth(u, v) > 5
				? base.getModel().getPassageWidth(u, v) / 2
				: base.getModel().getPassageWidth(u, v);
		r.fnCellBgColor = cell -> {
			if (inPath.get(cell)) {
				return pathColor;
			}
			return Color.WHITE;
		};
		r.fnPassageColor = (cell, dir) -> {
			int neighbor = canvas.getGrid().neighbor(cell, dir).getAsInt();
			if (inPath.get(cell) && inPath.get(neighbor)) {
				return pathColor;
			}
			return Color.WHITE;
		};
		return r;
	}

	public void run(GraphSearch<?, ?> dfs, int source, int target) {
		this.dfs = dfs;
		canvas.getRenderer().ifPresent(canvasRenderer -> {
			canvas.pushRenderer(createPathRenderer(canvasRenderer));
			dfs.addObserver(canvasUpdater);
			List<Integer> path = dfs.findPath(source, target);
			dfs.removeObserver(canvasUpdater);
			canvas.drawGrid();
			path.forEach(v -> {
				int w = dfs.getParent(v);
				canvas.drawGridCell(v);
				if (w != -1) {
					canvas.drawGridPassage(v, w, true);
				}
			});
			canvas.popRenderer();
		});
	}
}