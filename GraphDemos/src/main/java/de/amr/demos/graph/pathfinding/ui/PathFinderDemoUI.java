package de.amr.demos.graph.pathfinding.ui;

import static de.amr.graph.pathfinder.api.TraversalState.COMPLETED;
import static de.amr.graph.pathfinder.api.TraversalState.UNVISITED;
import static de.amr.graph.pathfinder.api.TraversalState.VISITED;
import static java.lang.Math.min;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.demos.graph.pathfinding.PathFinderDemoApp;
import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderDemoModel;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.grid.api.Topology;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.impl.AStarSearch;
import net.miginfocom.swing.MigLayout;

public class PathFinderDemoUI extends JFrame {

	private Action actionSetSource = new AbstractAction("Search From Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.setSource(selectedCell);
		}
	};

	private Action actionSetTarget = new AbstractAction("Search To Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.setTarget(selectedCell);
		}
	};

	private Action actionChangeAlgorithm = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.setSelectedAlgorithm(comboAlgorithm.getItemAt(comboAlgorithm.getSelectedIndex()));
		}
	};

	private Action actionChangeTopology = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Topology top;
			switch ((String) comboTopology.getSelectedItem()) {
			case "4 Neighbors":
				top = Top4.get();
				break;
			case "8 Neighbors":
				top = Top8.get();
				break;
			default:
				throw new IllegalArgumentException();
			}
			controller.setTopology(top);
		}
	};

	private Action actionResetScene = new AbstractAction("Reset Scene") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.resetScene();
		}
	};

	private Action actionTogglePathFinding = new AbstractAction("Path Finding") {

		@Override
		public void actionPerformed(ActionEvent e) {
			actionChangeAlgorithm.setEnabled(isPathFindingEnabled());
			cbShowCost.setEnabled(isPathFindingEnabled());
			tableResults.setVisible(isPathFindingEnabled());
			if (isPathFindingEnabled()) {
				model.runPathFinders();
				updateUI();
			} else {
				canvas.clear();
				canvas.drawGrid();
			}
		}
	};

	private MouseListener mouseHandler = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent mouse) {
			if (mouse.getButton() == MouseEvent.BUTTON1) {
				int cell = cellAt(mouse.getX(), mouse.getY());
				controller.flipTileAt(cell);
			}
		}

		@Override
		public void mouseReleased(MouseEvent mouse) {
			if (draggedCell != -1) {
				// end dragging
				draggedCell = -1;
				model.runPathFinders();
				updateUI();
			} else if (mouse.isPopupTrigger()) {
				// open popup menu
				selectedCell = cellAt(mouse.getX(), mouse.getY());
				actionSetSource.setEnabled(model.getMap().get(selectedCell) == Tile.BLANK);
				actionSetTarget.setEnabled(model.getMap().get(selectedCell) == Tile.BLANK);
				popupMenu.show(canvas, mouse.getX(), mouse.getY());
			}
		}
	};

	private MouseMotionListener mouseMotionHandler = new MouseMotionAdapter() {

		@Override
		public void mouseDragged(MouseEvent mouse) {
			int cell = cellAt(mouse.getX(), mouse.getY());
			if (cell != draggedCell) {
				// drag enters new cell
				draggedCell = cell;
				controller.setTileAt(cell, mouse.isShiftDown() ? Tile.BLANK : Tile.WALL);
			}
		}
	};

	private PathFinderDemoModel model;
	private PathFinderDemoApp controller;

	// UI specific
	private RenderingStyle style;
	private int cellSize;
	private int draggedCell;
	private int selectedCell;
	private GridCanvas canvas;
	private JComboBox<PathFinderAlgorithm> comboAlgorithm;
	private JComboBox<String> comboTopology;
	private JTable tableResults;
	private PathFinderTableModel tableModelResults;
	private JPopupMenu popupMenu;
	private JSpinner spinnerMapSize;
	private JCheckBox cbShowCost;
	private JCheckBox cbPathFinding;

	public PathFinderDemoUI() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Pathfinder Demo");

		JPanel settingsPanel = new JPanel();
		settingsPanel.setPreferredSize(new Dimension(500, 10));
		settingsPanel.setMinimumSize(new Dimension(500, 10));
		getContentPane().add(settingsPanel, BorderLayout.EAST);
		settingsPanel.setLayout(new MigLayout("", "[grow][grow]", "[][][][][][][][][grow]"));

		Component verticalStrut = Box.createVerticalStrut(20);
		settingsPanel.add(verticalStrut, "cell 0 4");

		cbPathFinding = new JCheckBox("Path Finding");
		cbPathFinding.setAction(actionTogglePathFinding);
		cbPathFinding.setFont(new Font("SansSerif", Font.BOLD, 14));
		settingsPanel.add(cbPathFinding, "cell 0 5");

		JLabel lblGridSize = new JLabel("Rows/Cols");
		settingsPanel.add(lblGridSize, "cell 0 1,alignx trailing");

		spinnerMapSize = new JSpinner();
		spinnerMapSize.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				controller.setMapSize((int) spinnerMapSize.getValue());
			}
		});
		spinnerMapSize.setModel(new SpinnerNumberModel(20, 2, 80, 1));
		settingsPanel.add(spinnerMapSize, "cell 1 1");

		JLabel lblAlgorithm = new JLabel("Displayed Algorithm");
		settingsPanel.add(lblAlgorithm, "cell 0 6,alignx trailing");

		comboAlgorithm = new JComboBox<>();
		comboAlgorithm.setAction(actionChangeAlgorithm);
		comboAlgorithm.setModel(new DefaultComboBoxModel<>(PathFinderAlgorithm.values()));
		settingsPanel.add(comboAlgorithm, "cell 1 6,growx");

		JLabel lblMap = new JLabel("Map");
		lblMap.setFont(new Font("SansSerif", Font.BOLD, 14));
		settingsPanel.add(lblMap, "cell 0 0");

		JLabel lblTopology = new JLabel("Topology");
		settingsPanel.add(lblTopology, "flowy,cell 0 2,alignx trailing");

		comboTopology = new JComboBox<>();
		comboTopology.setAction(actionChangeTopology);
		comboTopology.setModel(new DefaultComboBoxModel<>(new String[] { "4 Neighbors", "8 Neighbors" }));
		settingsPanel.add(comboTopology, "cell 1 2,growx");

		JLabel lblStyle = new JLabel("Display Style");
		settingsPanel.add(lblStyle, "cell 0 3,alignx trailing");

		style = RenderingStyle.BLOCKS;
		JComboBox<RenderingStyle> comboStyle = new JComboBox<>();
		comboStyle.addActionListener(e -> {
			style = (RenderingStyle) comboStyle.getSelectedItem();
			canvas.popRenderer();
			canvas.pushRenderer(createRenderer());
			canvas.clear();
			canvas.drawGrid();
		});
		comboStyle.setModel(new DefaultComboBoxModel<>(RenderingStyle.values()));
		settingsPanel.add(comboStyle, "cell 1 3,growx");

		JLabel lblShowCost = new JLabel("Show Cost");
		settingsPanel.add(lblShowCost, "cell 0 7,alignx trailing");

		cbShowCost = new JCheckBox("");
		cbShowCost.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				canvas.drawGrid();
			}
		});
		settingsPanel.add(cbShowCost, "cell 1 7,alignx left,aligny bottom");

		JScrollPane scrollPaneTableResults = new JScrollPane();
		settingsPanel.add(scrollPaneTableResults, "cell 0 8 2 1,growx,aligny top");

		tableResults = new JTable();
		tableResults.setEnabled(false);
		tableResults.setShowVerticalLines(false);
		scrollPaneTableResults.setViewportView(tableResults);

		popupMenu = new JPopupMenu();
		popupMenu.add(actionSetSource);
		popupMenu.add(actionSetTarget);
		popupMenu.addSeparator();
		popupMenu.add(actionResetScene);
	}

	public void setModel(PathFinderDemoModel model) {
		this.model = model;
		selectedCell = -1;
		draggedCell = -1;
		int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height * 90 / 100;
		cellSize = windowHeight / model.getMap().numCols();

		canvas = new GridCanvas(model.getMap(), cellSize);
		canvas.pushRenderer(createRenderer());
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseMotionListener(mouseMotionHandler);
		canvas.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		canvas.requestFocus();
		getContentPane().add(canvas, BorderLayout.CENTER);

		tableModelResults = new PathFinderTableModel(model.getResults());
		tableResults.setModel(tableModelResults);
		spinnerMapSize.setValue(model.getMapSize());
		comboAlgorithm.setSelectedItem(model.getSelectedAlgorithm());
		comboTopology.setSelectedItem(model.getMap().getTopology() == Top4.get() ? "4 Neighbors" : "8 Neighbors");
		tableResults.getColumnModel().getColumn(0).setPreferredWidth(150);

		actionChangeAlgorithm.setEnabled(isPathFindingEnabled());
		cbShowCost.setEnabled(isPathFindingEnabled());
		tableResults.setVisible(isPathFindingEnabled());
	}

	public void setController(PathFinderDemoApp controller) {
		this.controller = controller;
	}

	public void updateUI() {
		tableModelResults.fireTableDataChanged();
		canvas.clear();
		canvas.drawGrid();
	}

	public void updateCanvas() {
		canvas.setGrid(model.getMap());
		int newCellSize = getContentPane().getHeight() / model.getMapSize();
		if (newCellSize > 0) {
			cellSize = newCellSize;
			canvas.setCellSize(cellSize);
		}
		canvas.clear();
		canvas.drawGrid();
	}

	private boolean isPathFindingEnabled() {
		return cbPathFinding.isSelected();
	}

	private ConfigurableGridRenderer createRenderer() {
		ConfigurableGridRenderer r = style == RenderingStyle.BLOCKS ? new WallPassageGridRenderer()
				: new PearlsGridRenderer();
		r.fnGridBgColor = () -> Color.LIGHT_GRAY;
		r.fnCellSize = () -> cellSize;
		r.fnCellBgColor = cell -> {
			if (model.getMap().get(cell) == Tile.WALL) {
				return new Color(139, 69, 19);
			}
			if (cell == model.getSource()) {
				return Color.BLUE;
			}
			if (cell == model.getTarget()) {
				return Color.GREEN.darker();
			}
			if (isPathFindingEnabled()) {
				if (model.getSelectedResult().solutionCells.get(cell)) {
					return Color.RED.brighter();
				}
				if (model.getSelectedPathFinder().getState(cell) == COMPLETED) {
					return Color.ORANGE;
				}
				if (model.getSelectedPathFinder().getState(cell) == VISITED) {
					return Color.YELLOW;
				}
			}
			return Color.WHITE;
		};
		r.fnText = this::cellText;
		r.fnTextColor = cell -> {
			if (cell == model.getSource() || cell == model.getTarget()
					|| model.getSelectedResult().solutionCells.get(cell)) {
				return Color.WHITE;
			}
			return Color.BLUE;

		};
		r.fnTextFont = () -> new Font("Arial", Font.PLAIN,
				style == RenderingStyle.PEARLS ? cellSize * 30 / 100 : cellSize * 50 / 100);
		r.fnMinFontSize = () -> 4;
		r.fnPassageWidth = (u, v) -> style == RenderingStyle.PEARLS ? 1 : cellSize - 1;
		r.fnPassageColor = (cell, dir) -> Color.WHITE;
		return r;
	}

	private int cellAt(int x, int y) {
		int gridX = min(x / cellSize, model.getMap().numCols() - 1);
		int gridY = min(y / cellSize, model.getMap().numRows() - 1);
		return model.getMap().cell(gridX, gridY);
	}

	private String cellText(int cell) {
		if (!isPathFindingEnabled() || !cbShowCost.isSelected() && cell != model.getTarget()) {
			return "";
		}
		if (model.getSelectedPathFinder().getState(cell) == UNVISITED) {
			return "";
		}
		if (model.getSelectedPathFinder() instanceof AStarSearch) {
			AStarSearch<Tile, Double> astar = (AStarSearch<Tile, Double>) model.getSelectedPathFinder();
			return String.format("%.0f", astar.getScore(cell));
		} else {
			return String.format("%.0f", model.getSelectedPathFinder().getCost(cell));
		}
	}
}