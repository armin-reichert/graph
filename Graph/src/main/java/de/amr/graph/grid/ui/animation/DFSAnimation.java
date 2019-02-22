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

		public Builder visitedCellColor(Color color) {
			anim.visitedCellColor = color;
			return this;
		}

		public DFSAnimation build() {
			return anim;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private GridCanvas canvas;
	private Color pathColor = Color.RED;
	private Color visitedCellColor = Color.BLUE;
	private List<Integer> path;

	private DFSAnimation() {
	}

	private ConfigurableGridRenderer createRenderer(GraphSearch<?, ?> dfs, BitSet inPath, GridRenderer base) {
		ConfigurableGridRenderer r = base instanceof PearlsGridRenderer ? new PearlsGridRenderer()
				: new WallPassageGridRenderer();
		r.fnCellSize = base.getModel()::getCellSize;
		r.fnPassageWidth = () -> base.getModel().getPassageWidth() > 5 ? base.getModel().getPassageWidth() / 2
				: base.getModel().getPassageWidth();
		r.fnCellBgColor = cell -> {
			if (inPath.get(cell)) {
				return pathColor;
			}
			if (dfs.partOfFrontier(cell)) {
				return visitedCellColor;
			}
			return Color.WHITE;
		};
		r.fnPassageColor = (cell, dir) -> {
			int neighbor = canvas.getGrid().neighbor(cell, dir).getAsInt();
			if (inPath.get(cell) && inPath.get(neighbor)) {
				return pathColor;
			}
			if (dfs.partOfFrontier(cell) && dfs.partOfFrontier(neighbor)) {
				return visitedCellColor;
			}
			return Color.WHITE;
		};
		return r;
	}

	public void run(GraphSearch<?, ?> dfs, int source, int target) {
		GraphSearchObserver canvasUpdater = new GraphSearchObserver() {

			@Override
			public void edgeTraversed(int either, int other) {
//				delayed(() -> canvas.drawGridPassage(either, other, true));
			}

			@Override
			public void vertexStateChanged(int v, TraversalState oldState, TraversalState newState) {
//				delayed(() -> canvas.drawGridCell(v));
			}

			@Override
			public void vertexAddedToFrontier(int vertex) {
				int parent = dfs.getParent(vertex);
				if (parent != -1) {
					delayed(() -> canvas.drawGridPassage(parent, vertex, true));
				}
			}

			@Override
			public void vertexRemovedFromFrontier(int vertex) {
				int parent = dfs.getParent(vertex);
				if (parent != -1) {
					delayed(() -> canvas.drawGridPassage(parent, vertex, false));
				}
			}

		};

		BitSet inPath = new BitSet();
		canvas.pushRenderer(createRenderer(dfs, inPath, canvas.getRenderer().get()));
		dfs.addObserver(canvasUpdater);
		path = dfs.findPath(source, target);
		dfs.removeObserver(canvasUpdater);
		path.forEach(inPath::set);
		path.forEach(canvas::drawGridCell);
		canvas.popRenderer();
	}
}