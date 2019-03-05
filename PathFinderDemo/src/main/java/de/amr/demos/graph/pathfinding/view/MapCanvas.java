package de.amr.demos.graph.pathfinding.view;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;

import de.amr.demos.graph.pathfinding.controller.PathFinderDemoController;
import de.amr.demos.graph.pathfinding.model.PathFinderDemoModel;
import de.amr.demos.graph.pathfinding.model.PathFinderResult;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.grid.api.GridGraph2D;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.GridRenderer;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;

public class MapCanvas extends GridCanvas {

	private PathFinderDemoModel model;
	private PathFinderDemoController controller;
	private RenderingStyle style;
	private boolean showCost;

	public MapCanvas(GridGraph2D<?, ?> grid, int cellSize) {
		super(grid, cellSize);
		ConfigurableGridRenderer r = createMapRenderer(cellSize);
		pushRenderer(r);
		setBorder(BorderFactory.createLineBorder(r.getModel().getGridBgColor(), 1));
	}

	public void setModel(PathFinderDemoModel model) {
		this.model = model;
	}

	public void setController(PathFinderDemoController controller) {
		this.controller = controller;
	}

	public void setStyle(RenderingStyle style) {
		this.style = style;
		clear();
		GridRenderer oldRenderer = popRenderer();
		pushRenderer(createMapRenderer(oldRenderer.getModel().getCellSize()));
		drawGrid();
	}

	public void setShowCost(boolean showCost) {
		this.showCost = showCost;
		drawGrid();
	}

	private ConfigurableGridRenderer createMapRenderer(int cellSize) {
		ConfigurableGridRenderer r = style == RenderingStyle.BLOCKS ? new WallPassageGridRenderer()
				: new PearlsGridRenderer();
		r.fnGridBgColor = () -> new Color(160, 160, 160);
		r.fnCellSize = () -> cellSize;
		r.fnCellBgColor = cell -> {
			if (model.getMap().get(cell) == Tile.WALL) {
				return new Color(139, 69, 19);
			}
			if (cell == model.getSource()) {
				return Color.BLUE;
			}
			if (cell == model.getTarget()) {
				return Color.GREEN.darker();
			}
			if (isCellPartOfSolution(cell)) {
				return Color.RED.brighter();
			}
			BreadthFirstSearch<Tile, Double> pf = model.getPathFinder(controller.getSelectedAlgorithm());
			if (pf.getState(cell) == COMPLETED) {
				return Color.ORANGE;
			}
			if (pf.getState(cell) == VISITED) {
				return Color.YELLOW;
			}
			return Color.WHITE;
		};
		r.fnText = cell -> {
			if (!showCost) {
				return "";
			}
			if (model.getMap().get(cell) == Tile.WALL) {
				return "";
			}
			BreadthFirstSearch<Tile, Double> pf = model.getPathFinder(controller.getSelectedAlgorithm());
			if (pf.getState(cell) == UNVISITED) {
				return "";
			}
			double cost = pf.getCost(cell);
			if (pf instanceof AStarSearch) {
				AStarSearch<Tile, Double> astar = (AStarSearch<Tile, Double>) pf;
				cost = astar.getScore(cell);
			}
			return cost == Double.MAX_VALUE ? "" : String.format("%.0f", cost);
		};
		r.fnTextColor = cell -> {
			if (cell == model.getSource() || cell == model.getTarget() || isCellPartOfSolution(cell)) {
				return Color.WHITE;
			}
			return Color.BLUE;
		};
		r.fnTextFont = () -> new Font("Arial", Font.PLAIN,
				style == RenderingStyle.PEARLS ? cellSize * 30 / 100 : cellSize * 50 / 100);
		r.fnMinFontSize = () -> 4;
		r.fnPassageWidth = (u, v) -> style == RenderingStyle.PEARLS ? 1 : cellSize - 1;
		r.fnPassageColor = (cell, dir) -> Color.WHITE;
		return r;
	}

	private boolean isCellPartOfSolution(int cell) {
		PathFinderResult result = model.getResult(controller.getSelectedAlgorithm());
		return result != null && result.solutionCells.get(cell);
	}

}
