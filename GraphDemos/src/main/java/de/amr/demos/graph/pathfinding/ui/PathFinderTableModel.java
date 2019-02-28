package de.amr.demos.graph.pathfinding.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.amr.demos.graph.pathfinding.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.Tile;
import de.amr.graph.grid.impl.GridGraph;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import de.amr.util.StopWatch;

public class PathFinderTableModel extends AbstractTableModel {

	private static class Result {

		float runningTimeMillis;
		int pathLength;
		double pathCost;
		long numVisitedVertices;
	}

	private static final String[] columnTitles = { "Pathfinder", "Time [millis]", "Path length", "Path cost",
			"Visited Cells" };

	private List<BreadthFirstSearch<Tile, Double>> pathFinders;
	private List<Result> results;

	public void setPathFinders(List<BreadthFirstSearch<Tile, Double>> pathFinders) {
		this.pathFinders = pathFinders;
		results = Collections.emptyList();
	}

	public void updateResults(GridGraph<Tile, Double> map, int source, int target) {
		results = new ArrayList<>();
		for (BreadthFirstSearch<Tile, Double> pathFinder : pathFinders) {
			StopWatch watch = new StopWatch();
			watch.start();
			List<Integer> path = pathFinder.findPath(source, target);
			watch.stop();
			Result r = new Result();
			r.pathLength = path.size() - 1;
			r.pathCost = pathFinder.getCost(target);
			r.runningTimeMillis = watch.getNanos() / 1_000_000;
			r.numVisitedVertices = map.vertices().filter(v -> pathFinder.getState(v) != TraversalState.UNVISITED)
					.count();
			results.add(r);
		}
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return (int) pathFinders.size();
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
		if (rowIndex < results.size()) {
			Result result = results.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return PathFinderAlgorithm.values()[rowIndex].name();
			case 1:
				return String.format("%.2f", result.runningTimeMillis);
			case 2:
				return result.pathLength;
			case 3:
				return String.format("%.2f", result.pathCost);
			case 4:
				return result.numVisitedVertices;
			}
		}
		return null;
	}
}