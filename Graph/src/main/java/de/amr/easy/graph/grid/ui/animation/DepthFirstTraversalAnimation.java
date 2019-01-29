package de.amr.easy.graph.grid.ui.animation;

import static de.amr.easy.graph.pathfinder.api.TraversalState.VISITED;

import java.awt.Color;
import java.util.BitSet;
import java.util.function.IntSupplier;

import de.amr.easy.graph.event.api.GraphTraversalObserver;
import de.amr.easy.graph.grid.impl.GridGraph;
import de.amr.easy.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.easy.graph.grid.ui.rendering.GridCanvas;
import de.amr.easy.graph.grid.ui.rendering.GridRenderer;
import de.amr.easy.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.easy.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.easy.graph.pathfinder.api.TraversalState;
import de.amr.easy.graph.pathfinder.impl.DepthFirstSearch;

/**
 * Animation of depth-first traversal.
 * 
 * @author Armin Reichert
 */
public class DepthFirstTraversalAnimation {

	private final GridGraph<?, ?> grid;
	private Iterable<Integer> path;
	private Color pathColor = Color.RED;
	private Color visitedCellColor = Color.BLUE;
	public IntSupplier fnDelay = () -> 0;

	public DepthFirstTraversalAnimation(GridGraph<?, ?> grid) {
		this.grid = grid;
	}

	private ConfigurableGridRenderer createRenderer(DepthFirstSearch dfs, BitSet inPath, GridRenderer base) {
		ConfigurableGridRenderer r = base instanceof PearlsGridRenderer ? new PearlsGridRenderer()
				: new WallPassageGridRenderer();
		r.fnCellSize = base.getModel()::getCellSize;
		r.fnPassageWidth = () -> base.getModel().getPassageWidth() > 5 ? base.getModel().getPassageWidth() / 2
				: base.getModel().getPassageWidth();
		r.fnCellBgColor = cell -> {
			if (inPath.get(cell)) {
				return pathColor;
			}
			if (dfs.getState(cell) == VISITED || dfs.isStacked(cell)) {
				return visitedCellColor;
			}
			return base.getModel().getCellBgColor(cell);
		};
		r.fnPassageColor = (cell, dir) -> {
			int neighbor = grid.neighbor(cell, dir).getAsInt();
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

	public void run(GridCanvas canvas, DepthFirstSearch dfs, int source, int target) {
		dfs.addObserver(new GraphTraversalObserver() {

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
		});
		BitSet inPath = new BitSet();
		canvas.pushRenderer(createRenderer(dfs, inPath, canvas.getRenderer().get()));
		dfs.traverseGraph(source, target);
		path = dfs.path(target);
		path.forEach(inPath::set);
		path.forEach(canvas::drawGridCell);
		canvas.popRenderer();
	}

	public Color getPathColor() {
		return pathColor;
	}

	public void setPathColor(Color pathColor) {
		this.pathColor = pathColor;
	}

	public Color getVisitedCellColor() {
		return visitedCellColor;
	}

	public void setVisitedCellColor(Color visitedCellColor) {
		this.visitedCellColor = visitedCellColor;
	}
}