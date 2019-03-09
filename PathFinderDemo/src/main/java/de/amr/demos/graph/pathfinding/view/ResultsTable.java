package de.amr.demos.graph.pathfinding.view;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import de.amr.demos.graph.pathfinding.model.Model;
import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderResult;

public class ResultsTable extends JTable {

	private static class ResultsTableModel extends AbstractTableModel {

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

		private final Model model;

		public ResultsTableModel(Model model) {
			this.model = model;
		}

		@Override
		public int getRowCount() {
			return model.numPathFinders();
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

	private ResultsTableModel tableModel;

	public void init(Model model) {
		tableModel = new ResultsTableModel(model);
		setModel(tableModel);
		getColumnModel().getColumn(0).setPreferredWidth(140);
		setDefaultRenderer(Float.class, createNumberFormatter("%.2f"));
		setDefaultRenderer(Double.class, createNumberFormatter("%.2f"));
	}

	public void update() {
		if (tableModel != null) {
			tableModel.fireTableDataChanged();
		}
	}

	private static TableCellRenderer createNumberFormatter(String fmt) {
		return new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
						column);
				label.setHorizontalAlignment(TRAILING);
				if (value.getClass() == Double.class) {
					label.setText(String.format(fmt, (double) value));
				} else if (value.getClass() == Float.class) {
					label.setText(String.format(fmt, (float) value));
				}
				return label;
			}
		};
	}
}