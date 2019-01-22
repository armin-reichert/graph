package de.amr.demos.grid.iterators;

import static de.amr.easy.graph.grid.api.GridPosition.CENTER;
import static de.amr.easy.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.easy.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.easy.graph.pathfinder.api.TraversalState.VISITED;

import de.amr.demos.grid.SwingGridSampleApp;
import de.amr.easy.graph.grid.traversals.Spiral;

public class SpiralApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new SpiralApp());
	}

	public SpiralApp() {
		super(2);
		setAppName("Spiral");
	}

	@Override
	public void run() {
		getGrid().vertices().forEach(cell -> {
			getGrid().set(cell, COMPLETED);
		});
		Spiral spiral = new Spiral(getGrid(), getGrid().cell(CENTER));
		Integer prevCell = null;
		for (Integer cell : spiral) {
			getGrid().set(cell, VISITED);
			if (prevCell != null) {
				getGrid().set(prevCell, UNVISITED);
			}
			prevCell = cell;
		}
	}
}
