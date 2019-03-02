package de.amr.demos.graph.pathfinding;

import static de.amr.demos.graph.pathfinding.model.Tile.BLANK;
import static de.amr.demos.graph.pathfinding.model.Tile.WALL;
import static de.amr.graph.grid.api.GridPosition.BOTTOM_RIGHT;
import static de.amr.graph.grid.api.GridPosition.TOP_LEFT;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderDemoModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.demos.graph.pathfinding.ui.PathFinderDemoUI;
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
		EventQueue.invokeLater(PathFinderDemoApp::new);
	}

	private final PathFinderDemoModel model;
	private final PathFinderDemoUI view;

	public PathFinderDemoApp() {

		model = new PathFinderDemoModel();
		model.setMapSize(25);
		model.setTopology(Top8.get());
		model.newMap();
		model.setSource(model.getMap().cell(GridPosition.TOP_LEFT));
		model.setTarget(model.getMap().cell(GridPosition.BOTTOM_RIGHT));
		model.setSelectedAlgorithm(PathFinderAlgorithm.AStar);
		model.newPathFinders();
		model.runPathFinders();

		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		view = new PathFinderDemoUI();
		view.setController(this);
		view.setModel(model);

		view.pack();
		view.setLocationRelativeTo(null);
		view.setVisible(true);
	}

	public void setMapSize(int size) {
		model.resizeMap(size);
		model.setSource(model.getMap().cell(TOP_LEFT));
		model.setTarget(model.getMap().cell(BOTTOM_RIGHT));
		model.newPathFinders();
		model.runPathFinders();
		view.updateCanvas();
		view.updateUI();
	}

	public void setTopology(Topology topology) {
		model.setTopology(topology);
		view.updateCanvas();
		view.updateUI();
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
		model.setSource(model.getMap().cell(TOP_LEFT));
		model.setTarget(model.getMap().cell(BOTTOM_RIGHT));
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
		setTileAt(cell, model.getMap().get(cell) == WALL ? BLANK : WALL);
	}
}