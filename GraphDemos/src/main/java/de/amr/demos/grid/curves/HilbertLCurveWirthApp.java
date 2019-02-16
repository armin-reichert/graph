package de.amr.demos.grid.curves;

import static de.amr.graph.grid.curves.CurveUtils.traverse;
import static de.amr.graph.grid.ui.animation.BFSAnimation.floodFill;
import static de.amr.graph.util.GraphUtils.log;

import java.util.stream.IntStream;

import de.amr.demos.grid.SwingGridSampleApp;
import de.amr.graph.grid.api.GridPosition;
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
			traverse(curve, getGrid(), getGrid().cell(GridPosition.TOP_RIGHT), this::addEdge);
			floodFill(getCanvas(), getGrid().cell(GridPosition.TOP_RIGHT), false);
			sleep(1000);
		});
	}
}