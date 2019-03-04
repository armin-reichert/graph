package de.amr.demos.graph.pathfinding.view;

import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.Result;

public class PathFinderTableModel extends AbstractTableModel {

	private static final Object[][] COLUMNS = {
		//@formatter:off
			{ "Algorithm", String.class }, 
			{ "Time [ms]", Float.class }, 
			{ "Path length", Integer.class }, 
			{ "Path cost", Double.class },
			{ "Loss (%)", Double.class },
			{ "Visited", Integer.class } 
		//@formatter:on
	};

	private Map<PathFinderAlgorithm, Result> results;

	public PathFinderTableModel(Map<PathFinderAlgorithm, Result> results) {
		this.results = results;
	}

	@Override
	public int getRowCount() {
		return results.size();
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public String getColumnName(int column) {
		return (String) COLUMNS[column][0];
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return (Class<?>) COLUMNS[column][1];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PathFinderAlgorithm algorithm = PathFinderAlgorithm.values()[rowIndex];
		Result result = results.get(algorithm);
		switch (columnIndex) {
		case 0:
			return algorithm;
		case 1:
			return result.runningTimeMillis;
		case 2:
			return result.pathLength;
		case 3:
			return result.pathCost;
		case 4:
			double optimalCost = results.get(PathFinderAlgorithm.AStar).pathCost;
			return 100 * (result.pathCost - optimalCost) / optimalCost;
		case 5:
			return result.numVisitedVertices;
		}
		throw new IllegalArgumentException();
	}
}