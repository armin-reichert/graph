package de.amr.demos.graph.pathfinding.controller;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderDemoModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.PathFinderDemoView;
import de.amr.graph.grid.api.Topology;

/**
 * Demo application controller.
 * 
 * @author Armin Reichert
 */
public class PathFinderDemoController {

	private final PathFinderDemoModel model;
	private PathFinderDemoView view;

	private PathFinderAlgorithm selectedAlgorithm;
	private boolean autoRunPathFinders;

	public PathFinderDemoController(PathFinderDemoModel model) {
		this.model = model;
		selectedAlgorithm = PathFinderAlgorithm.AStar;
		autoRunPathFinders = false;
	}

	private void maybeRunPathFinder() {
		if (autoRunPathFinders) {
			model.runPathFinders();
		} else {
			model.clearResult(selectedAlgorithm);
		}
		if (view != null) {
			view.updateUI();
		}
	}

	public void runPathFinders() {
		model.runPathFinders();
		view.updateUI();
	}

	public void resetScene() {
		model.getMap().vertices().forEach(cell -> model.setTile(cell, BLANK));
		maybeRunPathFinder();
	}

	public void selectAlgorithm(PathFinderAlgorithm algorithm) {
		selectedAlgorithm = algorithm;
		maybeRunPathFinder();
	}

	public PathFinderAlgorithm getSelectedAlgorithm() {
		return selectedAlgorithm;
	}

	public void setView(PathFinderDemoView view) {
		this.view = view;
	}

	public boolean isAutoRunPathFinders() {
		return autoRunPathFinders;
	}

	public void setAutoRunPathFinders(boolean autoRunPathFinders) {
		this.autoRunPathFinders = autoRunPathFinders;
	}

	public void resizeMap(int size) {
		model.resizeMap(size);
		maybeRunPathFinder();
		view.updateCanvasAndUI();
	}

	public void setTopology(Topology topology) {
		model.setTopology(topology);
		maybeRunPathFinder();
		view.updateCanvasAndUI();
	}

	public void setSource(int source) {
		model.setSource(source);
		maybeRunPathFinder();
	}

	public void setTarget(int target) {
		model.setTarget(target);
		maybeRunPathFinder();
	}

	public void setTileAt(int cell, Tile tile) {
		if (cell == model.getSource() || cell == model.getTarget()) {
			return;
		}
		model.setTile(cell, tile);
		maybeRunPathFinder();
	}

	public void flipTileAt(int cell) {
		Tile newTile = model.getMap().get(cell) == WALL ? BLANK : WALL;
		setTileAt(cell, newTile);
	}
}