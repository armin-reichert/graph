package de.amr.demos.graph.pathfinding.controller;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;

import de.amr.demos.graph.pathfinding.model.Model;
import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.view.MainView;
import de.amr.graph.grid.api.Topology;

/**
 * Demo application controller.
 * 
 * @author Armin Reichert
 */
public class Controller {

	private final Model model;
	private MainView view;

	private PathFinderAlgorithm selectedAlgorithm;
	private boolean autoRunPathFinders;

	public Controller(Model model) {
		this.model = model;
		selectedAlgorithm = PathFinderAlgorithm.values()[0];
		autoRunPathFinders = false;
	}

	private void maybeRunPathFinder() {
		if (autoRunPathFinders) {
			model.runPathFinders();
		} else {
			model.clearResult(selectedAlgorithm);
		}
		view.updateView();
	}

	public void clearResults() {
		model.clearResults();
		view.updateView();
	}

	public void runPathFinders() {
		model.runPathFinders();
		view.updateView();
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

	public void setView(MainView view) {
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
		view.updateCanvas();
		maybeRunPathFinder();
	}

	public void setTopology(Topology topology) {
		model.setTopology(topology);
		view.updateCanvas();
		maybeRunPathFinder();
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
		if (cell != model.getSource() && cell != model.getTarget()) {
			model.setTile(cell, tile);
			maybeRunPathFinder();
		}
	}

	public void flipTileAt(int cell) {
		Tile flippedTile = model.getMap().get(cell) == WALL ? BLANK : WALL;
		setTileAt(cell, flippedTile);
	}
}