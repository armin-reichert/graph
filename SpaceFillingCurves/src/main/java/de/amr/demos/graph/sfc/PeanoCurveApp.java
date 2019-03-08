package de.amr.demos.graph.sfc;

import static de.amr.graph.grid.api.GridPosition.BOTTOM_LEFT;
import static de.amr.graph.grid.curves.CurveUtils.traverse;
import static de.amr.graph.util.GraphUtils.log;

import java.util.stream.IntStream;

import de.amr.graph.grid.curves.PeanoCurve;
import de.amr.graph.grid.ui.SwingGridSampleApp;

public class PeanoCurveApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new PeanoCurveApp());
	}

	public PeanoCurveApp() {
		super(243 * 4, 243 * 4, 4);
		setAppName("Peano Curve");
	}

	@Override
	public void run() {
		IntStream.of(3, 9, 81, 243).forEach(n -> {
			setCellSize(getCanvas().getWidth() / n);
			traverse(new PeanoCurve(log(3, n)), getGrid(), getGrid().cell(BOTTOM_LEFT), this::addEdge);
			floodfill(BOTTOM_LEFT);
			sleep(1000);
		});
	}
}