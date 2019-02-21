package de.amr.graph.grid.ui.animation;

import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import java.awt.Color;
import java.util.BitSet;
import java.util.List;
import java.util.function.IntSupplier;

import de.amr.graph.event.api.GraphTraversalObserver;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.GridRenderer;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.GraphSearch;

/**
 * Animation of depth-first search based algorithms.
 * 
 * @author Armin Reichert
 */
public class DFSAnimation {

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
			anim.fnDelay = fnDelay;
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
	private IntSupplier fnDelay = () -> 0;
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
			if (dfs.getState(cell) == VISITED || dfs.partOfFrontier(cell)) {
				return visitedCellColor;
			}
			return base.getModel().getCellBgColor(cell);
		};
		r.fnPassageColor = (cell, dir) -> {
			int neighbor = canvas.getGrid().neighbor(cell, dir).getAsInt();
			if (inPath.get(cell) && inPath.get(neighbor)) {
				return pathColor;
			}
			if (dfs.getState(cell) == VISITED && dfs.getState(neighbor) == VISITED) {
				return visitedCellColor;
			}
			if (r.getCellBgColor(cell) == visitedCellColor && r.getCellBgColor(neighbor) == visitedCellColor) {
				return visitedCellColor;
			}
			return base.getModel().getCellBgColor(cell);
		};
		return r;
	}

	public void run(GraphSearch<?, ?> dfs, int source, int target) {
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
		BitSet inPath = new BitSet();
		canvas.pushRenderer(createRenderer(dfs, inPath, canvas.getRenderer().get()));
		dfs.addObserver(canvasUpdater);
		path = dfs.findPath(source, target);
		path.forEach(inPath::set);
		path.forEach(canvas::drawGridCell);
		canvas.popRenderer();
		dfs.removeObserver(canvasUpdater);
	}
}