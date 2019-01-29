package de.amr.demos.grid.iterators;

import static de.amr.graph.grid.api.GridPosition.CENTER;
import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static java.lang.Math.min;

import de.amr.demos.grid.SwingGridSampleApp;
import de.amr.graph.grid.traversals.ExpandingCircle;

public class ExpandingCircleApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new ExpandingCircleApp());
	}

	public ExpandingCircleApp() {
		super(2);
		setAppName("Expanding Circle");
	}

	@Override
	public void run() {
		int n = min(getGrid().numCols() / 2, getGrid().numRows() / 2);
		new ExpandingCircle(getGrid(), getGrid().cell(CENTER), 0, n)
				.forEach(cell -> getGrid().set(cell, COMPLETED));
	}
}