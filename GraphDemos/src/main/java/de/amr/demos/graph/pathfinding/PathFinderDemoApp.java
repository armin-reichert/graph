package de.amr.demos.graph.pathfinding;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderDemoModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.ui.PathFinderUI;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.api.Topology;
import de.amr.graph.grid.impl.Top8;

/**
 * Demo application for path finder algorithms.
 * 
 * @author Armin Reichert
 */
public class PathFinderDemoApp {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			int gridSize = args.length > 0 ? Integer.parseInt(args[0]) : 25;
			new PathFinderDemoApp(gridSize);
		});
	}

	private final PathFinderDemoModel model = new PathFinderDemoModel();
	private final PathFinderUI view;

	public PathFinderDemoApp(int gridSize) {

		// Model
		model.createMap(gridSize, gridSize, Top8.get());
		model.setSource(model.getMap().cell(GridPosition.TOP_LEFT));
		model.setTarget(model.getMap().cell(GridPosition.BOTTOM_RIGHT));
		model.setSelectedAlgorithm(PathFinderAlgorithm.AStar);
		model.newPathFinders();
		model.runPathFinders();

		// View
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		view = new PathFinderUI(model, this);
		view.initState();
		view.pack();
		view.setLocationRelativeTo(null);
		view.setVisible(true);
	}

	public void setTopology(Topology topology) {
		if (topology != model.getMap().getTopology()) {
			model.createMap(model.getMap().numCols(), model.getMap().numRows(), topology);
			model.newPathFinders();
			model.runPathFinders();
			view.updateCanvas();
			view.updateUI();
		}
	}

	public void setSource(int source) {
		model.setSource(source);
		model.runPathFinders();
		view.updateUI();
	}

	public void setTarget(int target) {
		model.setTarget(target);
		model.runPathFinders();
		view.updateUI();
	}

	public void setSelectedAlgorithm(PathFinderAlgorithm algorithm) {
		model.setSelectedAlgorithm(algorithm);
		model.runPathFinders();
		view.updateUI();
	}

	public void resetScene() {
		model.setSource(model.getMap().cell(GridPosition.TOP_LEFT));
		model.setTarget(model.getMap().cell(GridPosition.BOTTOM_RIGHT));
		model.getMap().vertices().forEach(cell -> model.changeTile(cell, BLANK));
		model.runPathFinders();
		view.updateUI();
	}

	public void setTileAt(int cell, Tile tile) {
		model.changeTile(cell, tile);
		model.runPathFinders();
		view.updateUI();
	}
	
	public void flipTileAt(int cell) {
		model.changeTile(cell, model.getMap().get(cell) == Tile.WALL ? Tile.BLANK : Tile.WALL);
		model.runPathFinders();
		view.updateUI();
	}
}