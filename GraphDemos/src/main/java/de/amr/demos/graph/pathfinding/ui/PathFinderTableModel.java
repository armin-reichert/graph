package de.amr.demos.graph.pathfinding.ui;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.amr.demos.graph.pathfinding.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.Tile;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.util.StopWatch;

public class PathFinderTableModel extends AbstractTableModel {

	private static class Result {

		List<Integer> path;
		float runningTimeMillis;
		int pathLength;
		double pathCost;
		long numVisitedVertices;
	}

	private static final String[] columnTitles = { "Pathfinder", "Time [millis]", "Path length", "Path cost",
			"Visited Cells" };

	private Map<PathFinderAlgorithm, BreadthFirstSearch<Tile, Double>> pathFinders;
	private Map<PathFinderAlgorithm, Result> results;

	public void setPathFinders(Map<PathFinderAlgorithm, BreadthFirstSearch<Tile, Double>> pathFinders) {
		this.pathFinders = pathFinders;
		results = new EnumMap<>(PathFinderAlgorithm.class);
	}

	public void updateResults(GridGraph<Tile, Double> map, int source, int target) {
		for (PathFinderAlgorithm algorithm : PathFinderAlgorithm.values()) {
			BreadthFirstSearch<Tile, Double> pathFinder = pathFinders.get(algorithm);
			Result r = new Result();
			StopWatch watch = new StopWatch();
			watch.start();
			r.path = pathFinder.findPath(source, target);
			watch.stop();
			r.pathLength = r.path.size() - 1;
			r.pathCost = pathFinder.getCost(target);
			r.runningTimeMillis = watch.getNanos() / 1_000_000;
			r.numVisitedVertices = map.vertices().filter(v -> pathFinder.getState(v) != TraversalState.UNVISITED)
					.count();
			results.put(algorithm, r);
		}
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return pathFinders.size();
	}

	@Override
	public int getColumnCount() {
		return columnTitles.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnTitles[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PathFinderAlgorithm algorithm = PathFinderAlgorithm.values()[rowIndex];
		Result result = results.get(algorithm);
		switch (columnIndex) {
		case 0:
			return algorithm.name();
		case 1:
			return String.format("%.2f", result.runningTimeMillis);
		case 2:
			return result.pathLength;
		case 3:
			return String.format("%.2f", result.pathCost);
		case 4:
			return result.numVisitedVertices;
		}
		return null;
	}
}