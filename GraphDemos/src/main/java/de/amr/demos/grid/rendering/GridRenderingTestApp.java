package de.amr.demos.grid.rendering;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static java.lang.String.format;
import static java.lang.System.out;

import java.util.stream.IntStream;

import de.amr.demos.grid.SwingGridSampleApp;

public class GridRenderingTestApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new GridRenderingTestApp());
	}

	public GridRenderingTestApp() {
		super(256);
		setAppName("Grid Rendering Demo");
	}

	@Override
	public void run() {
		setCanvasAnimation(false);
		IntStream.of(256, 128, 64, 32, 16, 8, 4, 2).forEach(cellSize -> {
			setCellSize(cellSize);
			getGrid().setDefaultVertexLabel(v -> COMPLETED);
			getGrid().fill();
			watch.measure(getCanvas()::drawGrid);
			out.println(format("Rendering grid with %d cells took %.3f seconds", getGrid().numVertices(),
					watch.getSeconds()));
			floodfill();
			sleep(1000);
		});
		System.exit(0);
	}
}
