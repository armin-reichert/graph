package de.amr.demos.graph.pathfinding.ui;

import java.util.List;
import java.util.function.Supplier;

import javax.swing.table.AbstractTableModel;

import de.amr.demos.graph.pathfinding.Tile;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;

public class PathFinderTableModel extends AbstractTableModel {

	private static final String[] columnTitles = { "Pathfinder", "Time", "Path length", "Path cost",
			"Visited Cells" };

	private final Supplier<List<BreadthFirstSearch<Tile, Double>>> fnPathFinders;

	public PathFinderTableModel(Supplier<List<BreadthFirstSearch<Tile, Double>>> fnPathFinders) {
		this.fnPathFinders = fnPathFinders;
	}

	@Override
	public int getRowCount() {
		return fnPathFinders.get().size();
	}

	@Override
	public int getColumnCount() {
		return columnTitles.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return null;
	}
}