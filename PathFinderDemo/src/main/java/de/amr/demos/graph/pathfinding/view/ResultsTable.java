package de.amr.demos.graph.pathfinding.view;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import de.amr.demos.graph.pathfinding.model.Model;
import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderResult;

/**
 * Table with path finder results.
 * 
 * @author Armin Reichert
 */
public class ResultsTable extends JTable {

	private static class ColumnSpec {

		final String title;
		final Class<?> type;
		final String format;

		ColumnSpec(String title, Class<?> type, String format) {
			this.title = title;
			this.type = type;
			this.format = format;
		}
	}

	private static final ColumnSpec[] COLUMNS = {
		//@formatter:off
		new ColumnSpec("Algorithm", String.class, "%s"), 
		new ColumnSpec("Time [ms]", Float.class, "%.2f"), 
		new ColumnSpec("Path length", Integer.class, "%d"), 
		new ColumnSpec("Path cost", Double.class, "%.2f"),
		new ColumnSpec("Loss (%)", Double.class, "%.0f"),
		new ColumnSpec("Visited", Long.class, "%d"), 
		//@formatter:on
	};

	private static class ResultsTableModel extends AbstractTableModel {

		private final Model model;

		public ResultsTableModel(Model model) {
			this.model = model;
		}

		@Override
		public int getRowCount() {
			return PathFinderAlgorithm.values().length;
		}

		@Override
		public int getColumnCount() {
			return COLUMNS.length;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMNS[column].title;
		}

		@Override
		public Class<?> getColumnClass(int column) {
			return COLUMNS[column].type;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			PathFinderAlgorithm algorithm = PathFinderAlgorithm.values()[rowIndex];
			PathFinderResult result = model.getResult(algorithm);
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
				double optimalCost = model.getResult(PathFinderAlgorithm.AStar).pathCost;
				return 100 * (result.pathCost - optimalCost) / optimalCost;
			case 5:
				return result.numVisitedVertices;
			}
			throw new IllegalArgumentException();
		}
	}

	private void formatColumns() {
		for (int i = 0; i < COLUMNS.length; ++i) {
			TableColumn column = getColumnModel().getColumn(i);
			ColumnSpec columnSpec = COLUMNS[i];
			column.setCellRenderer(new DefaultTableCellRenderer() {

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (value == null) {
						return this;
					}
					if (columnSpec.type == String.class) {
						setHorizontalAlignment(LEADING);
						setText(String.format(columnSpec.format, value));
					} else if (columnSpec.type == Float.class) {
						setHorizontalAlignment(TRAILING);
						setText(String.format(columnSpec.format, (Float) value));
					} else if (columnSpec.type == Double.class) {
						setHorizontalAlignment(TRAILING);
						setText(String.format(columnSpec.format, (Double) value));
					} else if (columnSpec.type == Integer.class) {
						setHorizontalAlignment(TRAILING);
						setText(String.format(columnSpec.format, (Integer) value));
					} else if (columnSpec.type == Long.class) {
						setHorizontalAlignment(TRAILING);
						setText(String.format(columnSpec.format, (Long) value));
					}
					return this;
				}
			});
		}
		getColumnModel().getColumn(0).setPreferredWidth(140);
	}

	public void init(Model model) {
		setModel(new ResultsTableModel(model));
		formatColumns();
	}

	public void dataChanged() {
		if (getModel() != null) {
			((ResultsTableModel) getModel()).fireTableDataChanged();
		}
	}
}