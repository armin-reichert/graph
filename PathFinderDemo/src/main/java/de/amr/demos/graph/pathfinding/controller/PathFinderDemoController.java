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

	private PathFinderDemoModel model;
	private PathFinderDemoView view;

	private PathFinderAlgorithm selectedAlgorithm;
	private boolean autoRunPathFinders;

	public PathFinderDemoController() {
		selectedAlgorithm = PathFinderAlgorithm.AStar;
		autoRunPathFinders = false;
	}

	public void setModel(PathFinderDemoModel model) {
		this.model = model;
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

	public void setMapSize(int size) {
		model.resizeMap(size);
		model.newPathFinders();
		if (autoRunPathFinders) {
			model.runPathFinders();
		}
		view.updateCanvas();
		view.updateUI();
	}

	public void setTopology(Topology topology) {
		model.setTopology(topology);
		if (autoRunPathFinders) {
			model.runPathFinders();
		}
		view.updateCanvas();
		view.updateUI();
	}

	public void setSource(int source) {
		model.setSource(source);
		if (autoRunPathFinders) {
			model.runPathFinders();
		}
		view.updateUI();
	}

	public void setTarget(int target) {
		model.setTarget(target);
		if (autoRunPathFinders) {
			model.runPathFinders();
		}
		view.updateUI();
	}

	public void setSelectedAlgorithm(PathFinderAlgorithm algorithm) {
		selectedAlgorithm = algorithm;
		if (model.getResults().get(getSelectedAlgorithm()) != null) {
			model.getResults().get(getSelectedAlgorithm()).clear();
		}
		model.getPathFinder(getSelectedAlgorithm()).init();
		if (autoRunPathFinders) {
			model.runPathFinders();
		}
		view.updateUI();
	}

	public void resetScene() {
		model.getMap().vertices().forEach(cell -> model.changeTile(cell, BLANK));
		if (autoRunPathFinders) {
			model.runPathFinders();
		}
		view.updateUI();
	}

	public void setTileAt(int cell, Tile tile) {
		model.changeTile(cell, tile);
		if (autoRunPathFinders) {
			model.runPathFinders();
		} else {
			model.getResults().get(getSelectedAlgorithm()).clear();
			model.getPathFinder(getSelectedAlgorithm()).init();
		}
		view.updateUI();
	}

	public void flipTileAt(int cell) {
		setTileAt(cell, model.getMap().get(cell) == WALL ? BLANK : WALL);
	}

	public PathFinderAlgorithm getSelectedAlgorithm() {
		return selectedAlgorithm;
	}
}