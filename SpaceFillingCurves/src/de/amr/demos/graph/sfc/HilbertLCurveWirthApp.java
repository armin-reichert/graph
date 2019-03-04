package de.amr.demos.graph.sfc;

import static de.amr.graph.grid.api.GridPosition.TOP_RIGHT;
import static de.amr.graph.grid.curves.CurveUtils.traverse;
import static de.amr.graph.util.GraphUtils.log;

import java.util.stream.IntStream;

import de.amr.demos.graph.SwingGridSampleApp;
import de.amr.graph.grid.curves.HilbertLCurveWirth;

public class HilbertLCurveWirthApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new HilbertLCurveWirthApp());
	}

	public HilbertLCurveWirthApp() {
		super(512, 512, 256);
		setAppName("Hilbert Curve (L-system, Wirth)");
	}

	@Override
	public void run() {
		IntStream.of(256, 128, 64, 32, 16, 8, 4, 2).forEach(cellSize -> {
			setCellSize(cellSize);
			HilbertLCurveWirth curve = new HilbertLCurveWirth(log(2, getGrid().numCols()));
			traverse(curve, getGrid(), getGrid().cell(TOP_RIGHT), this::addEdge);
			floodfill(TOP_RIGHT);
			sleep(1000);
		});
	}
}