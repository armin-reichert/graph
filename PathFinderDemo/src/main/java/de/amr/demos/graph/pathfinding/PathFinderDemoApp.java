package de.amr.demos.graph.pathfinding;

import java.awt.EventQueue;

import de.amr.demos.graph.pathfinding.controller.PathFinderDemoController;
import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderDemoModel;
import de.amr.demos.graph.pathfinding.view.PathFinderDemoView;
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
	private final PathFinderDemoView view;
	private final PathFinderDemoController controller;

	public PathFinderDemoApp() {

		model = new PathFinderDemoModel(19, Top8.get());
		model.setSource(model.getMap().cell(2, 9));
		model.setTarget(model.getMap().cell(16, 9));
		model.newPathFinders();

		controller = new PathFinderDemoController(model);

		view = new PathFinderDemoView();

		controller.setView(view);
		controller.setSelectedAlgorithm(PathFinderAlgorithm.Dijkstra);

		view.init(model, controller);
		view.updateUI();

		view.pack();
		view.setLocationRelativeTo(null);
		view.setVisible(true);
	}
}