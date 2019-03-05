package de.amr.demos.graph.sfc;

import static de.amr.graph.grid.api.GridPosition.BOTTOM_LEFT;
import static de.amr.graph.grid.curves.CurveUtils.traverse;
import static de.amr.graph.util.GraphUtils.log;

import java.util.stream.IntStream;

import de.amr.demos.graph.SwingGridSampleApp;
import de.amr.graph.grid.curves.HilbertLCurve;

public class HilbertLCurveApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new HilbertLCurveApp());
	}

	public HilbertLCurveApp() {
		super(512, 512, 256);
		setAppName("Hilbert Curve (L-system)");
	}

	@Override
	public void run() {
		IntStream.of(256, 128, 64, 32, 16, 8, 4, 2).forEach(cellSize -> {
			setCellSize(cellSize);
			HilbertLCurve curve = new HilbertLCurve(log(2, getGrid().numCols()));
			traverse(curve, getGrid(), getGrid().cell(BOTTOM_LEFT), this::addEdge);
			floodfill(BOTTOM_LEFT);
			sleep(1000);
		});
	}
}