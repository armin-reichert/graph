package de.amr.demos.grid.curves;

import static de.amr.easy.grid.impl.curves.CurveUtils.traverse;
import static de.amr.easy.grid.ui.swing.animation.BreadthFirstTraversalAnimation.floodFill;
import static de.amr.easy.util.GraphUtils.log;

import java.util.stream.IntStream;

import de.amr.demos.grid.SwingGridSampleApp;
import de.amr.easy.grid.impl.curves.MooreLCurve;

/**
 * Creates Moore curves of different sizes and shows an animation of the creation and BFS-traversal
 * of the underlying graph.
 * 
 * @author Armin Reichert
 */
public class MooreCurveApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new MooreCurveApp());
	}

	public MooreCurveApp() {
		super(512, 512, 512);
		setAppName("Moore Curve");
	}

	@Override
	public void run() {
		IntStream.of(2, 4, 8, 16, 32, 64, 128, 256).forEach(n -> {
			setCellSize(getCanvas().getWidth() / n);
			int startCol = n / 2, startRow = n - 1;
			int startCell = getGrid().cell(startCol, startRow);
			traverse(new MooreLCurve(log(2, n)), getGrid(), startCell, this::addEdge);
			floodFill(getCanvas(), getGrid(), startCell, false);
			sleep(1000);
		});
	}
}