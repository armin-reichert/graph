package de.amr.demos.graph.pathfinding;

import java.awt.EventQueue;

import de.amr.demos.graph.pathfinding.controller.PathFinderDemoController;
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

		model = new PathFinderDemoModel(11, Top8.get());
		view = new PathFinderDemoView();
		controller = new PathFinderDemoController();

		// initialize model
		model.setSource(model.getMap().cell(2, 5));
		model.setTarget(model.getMap().cell(8, 5));
		model.newPathFinders();

		// set associations
		controller.setModel(model);
		view.setModel(model);
		view.setController(controller);
		controller.setView(view);

		// display initial view
		view.updateUI();
		view.pack();
		view.setLocationRelativeTo(null);
		view.setVisible(true);
	}
}