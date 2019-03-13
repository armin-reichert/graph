package de.amr.demos.graph.iterators;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;

import java.util.stream.IntStream;

import de.amr.graph.grid.ui.SwingGridSampleApp;

public class FullGridApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new FullGridApp());
	}

	public FullGridApp() {
		super(1024, 1024, 512);
		setAppName("Full Grid");
		setCanvasAnimation(false);
	}

	@Override
	public void run() {
		IntStream.of(512, 256, 128, 64, 32, 16, 8, 4, 2).forEach(cellSize -> {
			setCellSize(cellSize);
			getGrid().setDefaultVertexLabel(v -> COMPLETED);
			getGrid().fill();
			getCanvas().clear();
			watch.measure(getCanvas()::drawGrid);
			System.out.println(String.format("Grid (%d cells @%d) rendered in %.2f seconds",
					getGrid().numVertices(), cellSize, watch.getSeconds()));
			sleep(1000);
		});
		System.exit(0);
	}
}