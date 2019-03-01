package de.amr.demos.graph.pathfinding.ui;

import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.Result;

public class PathFinderTableModel extends AbstractTableModel {

	private static final String[] COLUMN_HEADERS = {
		//@formatter:off
		"Pathfinder", 
		"Time [millis]", 
		"Path length", 
		"Path cost",
		"Visited Cells" 
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
		return COLUMN_HEADERS.length;
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_HEADERS[column];
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
		throw new IllegalArgumentException();
	}
}