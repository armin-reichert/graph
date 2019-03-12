package de.amr.demos.graph.pathfinding;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.demos.graph.pathfinding.controller.Controller;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.view.MainView;
import de.amr.demos.graph.pathfinding.view.MainWindow;
import de.amr.graph.grid.impl.Top8;

/**
 * Demo application for path finder algorithms.
 * 
 * @author Armin Reichert
 */
public class PathFinderDemoApp {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(PathFinderDemoApp::new);
	}

	public PathFinderDemoApp() {
		PathFinderModel model = new PathFinderModel(23, Top8.get());
		Controller controller = new Controller(model);
		MainView view = new MainView();
		view.init(model, controller);
		controller.setView(view);
		MainWindow window = new MainWindow();
		window.setView(view);
		window.setVisible(true);
	}
}