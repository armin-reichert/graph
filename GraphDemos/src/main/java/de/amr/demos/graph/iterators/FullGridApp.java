package de.amr.demos.graph.iterators;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import de.amr.demos.graph.SwingGridSampleApp;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;

public class FullGridApp extends SwingGridSampleApp {

	public static void main(String[] args) {
		launch(new FullGridApp());
	}

	public FullGridApp() {
		super(1024, 1024, 512);
		setAppName("Full Grid");
	}

	@Override
	public void run() {
		setCanvasAnimation(false);
		Stream.of(Top8.get(), Top4.get()).forEach(topology -> {
			IntStream.of(512, 256, 128, 64, 32, 16, 8, 4, 2).forEach(cellSize -> {
				setCellSize(cellSize);
				setGridTopology(topology);
				getGrid().setDefaultVertexLabel(v -> COMPLETED);
				getGrid().fill();
				watch.measure(getCanvas()::drawGrid);
				System.out.println(String.format("Grid (%d cells @%d) rendered in %.2f seconds",
						getGrid().numVertices(), cellSize, watch.getSeconds()));
				sleep(1000);
			});
		});
		System.exit(0);
	}
}