package de.amr.demos.graph.pathfinding.view;

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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.demos.graph.pathfinding.controller.PathFinderDemoController;
import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.model.PathFinderDemoModel;
import de.amr.demos.graph.pathfinding.model.PathFinderResult;
import de.amr.demos.graph.pathfinding.model.Tile;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.grid.ui.animation.AbstractAnimation;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.GraphSearchObserver;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import net.miginfocom.swing.MigLayout;

/**
 * UI for path finder demo app.
 * 
 * @author Armin Reichert
 */
public class PathFinderDemoView extends JFrame {

	private static final String _4_NEIGHBORS = "4 Neighbors";
	private static final String _8_NEIGHBORS = "8 Neighbors";

	private class Animation extends AbstractAnimation implements GraphSearchObserver {

		@Override
		public void vertexAddedToFrontier(int v) {
			delayed(() -> canvas.drawGridCell(v));
		}

		@Override
		public void vertexRemovedFromFrontier(int v) {
			delayed(() -> canvas.drawGridCell(v));
		}

		@Override
		public void vertexStateChanged(int v, TraversalState oldState, TraversalState newState) {
			delayed(() -> canvas.drawGridCell(v));
		}

		@Override
		public void edgeTraversed(int either, int other) {
			delayed(() -> {
				canvas.drawGridPassage(either, other, true);
				canvas.drawGridCell(either);
				canvas.drawGridCell(other);
			});
		}
	}

	private class PathFinderAnimationTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			PathFinderAlgorithm algorithm = controller.getSelectedAlgorithm();
			model.clearResult(algorithm);
			model.newPathFinder(algorithm);
			canvas.drawGrid();
			model.getPathFinder(algorithm).addObserver(animation);
			model.runPathFinder(algorithm);
			model.getPathFinder(algorithm).removeObserver(animation);
			return null;
		}

		@Override
		protected void done() {
			canvas.drawGrid(); // redraw to highlight solution
		}
	}

	private Action actionRunSelectedPathFinder = new AbstractAction("Run Selected Path Finder") {

		@Override
		public void actionPerformed(ActionEvent e) {
			new PathFinderAnimationTask().execute();
		}
	};

	private Action actionClear = new AbstractAction("Clear") {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.clearResult(controller.getSelectedAlgorithm());
			canvas.drawGrid();
		}
	};

	private Action actionSetSource = new AbstractAction("Start Search Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.setSource(selectedCell);
		}
	};

	private Action actionSetTarget = new AbstractAction("End Search Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.setTarget(selectedCell);
		}
	};

	private Action actionSelectAlgorithm = new AbstractAction("Select Algorithm") {

		@Override
		public void actionPerformed(ActionEvent e) {
			PathFinderAlgorithm algorithm = comboAlgorithm.getItemAt(comboAlgorithm.getSelectedIndex());
			controller.selectAlgorithm(algorithm);
		}
	};

	private Action actionSelectTopology = new AbstractAction("Select Topology") {

		@Override
		public void actionPerformed(ActionEvent e) {
			String topology = comboTopology.getItemAt(comboTopology.getSelectedIndex());
			switch (topology) {
			case _4_NEIGHBORS:
				controller.setTopology(Top4.get());
				break;
			case _8_NEIGHBORS:
				controller.setTopology(Top8.get());
				break;
			default:
				throw new IllegalArgumentException("Unknown topology: " + topology);
			}
		}
	};

	private Action actionResetScene = new AbstractAction("Reset Scene") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.resetScene();
		}
	};

	private Action actionToggleAutoPathFinding = new AbstractAction("Run Path Finders Automatically") {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean auto = cbAutoRunPathFinder.isSelected();
			actionRunSelectedPathFinder.setEnabled(!auto);
			actionClear.setEnabled(!auto);
			sliderDelay.setEnabled(!auto);
			tableResults.setVisible(auto);
			controller.setAutoRunPathFinders(auto);
			if (auto) {
				controller.runPathFinders();
			} else {
				model.clearResult(controller.getSelectedAlgorithm());
				canvas.drawGrid();
			}
		}
	};

	private Action actionSelectMapStyle = new AbstractAction("Style") {

		@Override
		public void actionPerformed(ActionEvent e) {
			style = comboStyle.getItemAt(comboStyle.getSelectedIndex());
			canvas.clear();
			canvas.popRenderer();
			canvas.pushRenderer(createMapRenderer());
			canvas.drawGrid();
		}
	};

	private Action actionShowCost = new AbstractAction("Show Cost") {

		@Override
		public void actionPerformed(ActionEvent e) {
			canvas.drawGrid();
		}
	};

	private ChangeListener onMapSizeChange = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			controller.resizeMap((int) spinnerMapSize.getValue());
		}
	};

	private MouseListener mouseHandler = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent mouse) {
			if (mouse.getButton() == MouseEvent.BUTTON1 && mouse.isShiftDown()) {
				int cell = cellAt(mouse.getX(), mouse.getY());
				controller.flipTileAt(cell);
			}
		}

		@Override
		public void mouseReleased(MouseEvent mouse) {
			if (draggedCell != -1) {
				// end dragging
				draggedCell = -1;
				if (controller.isAutoRunPathFinders()) {
					controller.runPathFinders();
				}
			} else if (mouse.isPopupTrigger()) {
				// open popup menu
				selectedCell = cellAt(mouse.getX(), mouse.getY());
				boolean blank = model.getMap().get(selectedCell) == Tile.BLANK;
				actionSetSource.setEnabled(blank);
				actionSetTarget.setEnabled(blank);
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
				if (mouse.isShiftDown()) {
					controller.flipTileAt(cell);
				}
			}
		}
	};

	private PathFinderDemoModel model;
	private PathFinderDemoController controller;

	// UI specific
	private RenderingStyle style;
	private int cellSize;
	private int draggedCell;
	private int selectedCell;
	private GridCanvas canvas;
	private Animation animation;
	private int initialHeight;

	private JComboBox<PathFinderAlgorithm> comboAlgorithm;
	private JComboBox<String> comboTopology;
	private JTable tableResults;
	private PathFinderResultsTableModel pathFinderResults;
	private JPopupMenu popupMenu;
	private JSpinner spinnerMapSize;
	private JCheckBox cbShowCost;
	private JLabel lblPathFinding;
	private JPanel panelActions;
	private JCheckBox cbAutoRunPathFinder;
	private JComboBox<RenderingStyle> comboStyle;
	private JSlider sliderDelay;
	private JPanel panelMap;

	public PathFinderDemoView() {
		getContentPane().setBackground(Color.WHITE);
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Pathfinder Demo");
		getContentPane().setLayout(new MigLayout("", "[grow][]", "[grow]"));

		panelMap = new JPanel();
		panelMap.setBackground(Color.WHITE);
		getContentPane().add(panelMap, "cell 0 0,grow");
		panelMap.setLayout(new BorderLayout(0, 0));

		panelActions = new JPanel();
		panelActions.setBackground(Color.WHITE);
		panelActions.setPreferredSize(new Dimension(500, 10));
		panelActions.setMinimumSize(new Dimension(500, 10));
		getContentPane().add(panelActions, "cell 1 0,alignx left,growy");
		panelActions
				.setLayout(new MigLayout("", "[grow,trailing][grow]", "[][][][][][][][][][][][][grow][][grow]"));

		JLabel lblMap = new JLabel("Map");
		panelActions.add(lblMap, "cell 0 1 2 1,alignx leading");
		lblMap.setForeground(Color.BLACK);
		lblMap.setFont(new Font("SansSerif", Font.BOLD, 20));

		Component verticalStrut = Box.createVerticalStrut(20);
		panelActions.add(verticalStrut, "cell 0 5");

		lblPathFinding = new JLabel("Path Finding");
		lblPathFinding.setForeground(Color.BLACK);
		lblPathFinding.setFont(new Font("SansSerif", Font.BOLD, 20));
		panelActions.add(lblPathFinding, "cell 0 6 2 1,alignx leading");

		JLabel lblMapSize = new JLabel("Rows/Cols");
		panelActions.add(lblMapSize, "cell 0 2,alignx trailing");

		spinnerMapSize = new JSpinner();
		panelActions.add(spinnerMapSize, "cell 1 2");

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panelActions.add(panel, "flowx,cell 1 8,growx");

		JButton btnRun = new JButton("Run");
		panel.add(btnRun);
		btnRun.setAction(actionRunSelectedPathFinder);

		JButton btnNewButton = new JButton();
		panel.add(btnNewButton);
		btnNewButton.setAction(actionClear);

		JLabel lblDelay = new JLabel("Delay [ms]");
		panelActions.add(lblDelay, "cell 0 9,alignx trailing,aligny top");

		sliderDelay = new JSlider();
		sliderDelay.setMaximum(50);
		sliderDelay.setMinorTickSpacing(2);
		sliderDelay.setPaintTicks(true);
		sliderDelay.setPaintLabels(true);
		sliderDelay.setMajorTickSpacing(10);
		panelActions.add(sliderDelay, "cell 1 9,growx");

		cbAutoRunPathFinder = new JCheckBox("Run Automatically");
		panelActions.add(cbAutoRunPathFinder, "cell 1 10,alignx leading,aligny center");

		JLabel lblAlgorithm = new JLabel("Algorithm");
		panelActions.add(lblAlgorithm, "cell 0 7,alignx trailing");

		comboAlgorithm = new JComboBox<>();
		panelActions.add(comboAlgorithm, "cell 1 7,growx");

		JLabel lblTopology = new JLabel("Topology");
		panelActions.add(lblTopology, "flowy,cell 0 3,alignx trailing");

		comboTopology = new JComboBox<>();
		panelActions.add(comboTopology, "cell 1 3,growx");

		JLabel lblStyle = new JLabel("Display Style");
		panelActions.add(lblStyle, "cell 0 4,alignx trailing");

		style = RenderingStyle.BLOCKS;
		comboStyle = new JComboBox<>();
		comboStyle.setAction(actionSelectMapStyle);
		comboStyle.setModel(new DefaultComboBoxModel<>(RenderingStyle.values()));
		panelActions.add(comboStyle, "cell 1 4,growx");

		cbShowCost = new JCheckBox("Show Cost");
		cbShowCost.setAction(actionShowCost);
		panelActions.add(cbShowCost, "cell 1 11,alignx leading,aligny bottom");

		JScrollPane scrollPaneTableResults = new JScrollPane();
		scrollPaneTableResults.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		panelActions.add(scrollPaneTableResults, "cell 0 12 2 1,growx,aligny top");

		tableResults = new JTable();
		tableResults.setEnabled(false);
		tableResults.setShowVerticalLines(false);
		scrollPaneTableResults.setViewportView(tableResults);

		JTextPane textLegend = new JTextPane();
		textLegend.setEditable(false);
		textLegend.setFont(new Font("SansSerif", Font.PLAIN, 14));
		textLegend.setContentType("text/html");
		textLegend.setText(
				"<div style=\"padding:10px\">\r\nPress <em>SHIFT</em> and drag the mouse to add or remove walls. Right-click opens a context menu where you can change the source and target cells and reset the scene.\r\n<p>\r\n\"Open\" cells are shown in <span style=\"background-color:yellow\">yellow</span>, \"closed\" cells in <span style=\"background-color:orange\">orange</span>. The source cell is shown in <span style=\"background-color:blue;color:white\">blue</span>, the target cell in <span style=\"background-color:green;color:white\">green</span>.\r\n<p>\r\nSource code on GitHub: <b>https://github.com/armin-reichert/graph</b>\r\n</div>");
		panelActions.add(textLegend, "cell 0 14 2 1,grow");

		popupMenu = new JPopupMenu();
		popupMenu.add(actionSetSource);
		popupMenu.add(actionSetTarget);
		popupMenu.addSeparator();
		popupMenu.add(actionResetScene);
	}

	public void init(PathFinderDemoModel model, PathFinderDemoController controller) {
		this.model = model;
		this.controller = controller;

		selectedCell = -1;
		draggedCell = -1;

		// canvas
		cellSize = (Toolkit.getDefaultToolkit().getScreenSize().height * 90 / 100) / model.getMapSize();
		canvas = new GridCanvas(model.getMap(), cellSize);
		canvas.pushRenderer(createMapRenderer());
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseMotionListener(mouseMotionHandler);
		canvas.requestFocus();
		canvas
				.setBorder(BorderFactory.createLineBorder(canvas.getRenderer().get().getModel().getGridBgColor(), 1));
		panelMap.add(canvas, BorderLayout.CENTER);
		initialHeight = canvas.getHeight();

		// path finder results table
		pathFinderResults = new PathFinderResultsTableModel(model);
		tableResults.setModel(pathFinderResults);
		tableResults.setVisible(controller.isAutoRunPathFinders());
		tableResults.getColumnModel().getColumn(0).setPreferredWidth(140);

		// others controls
		spinnerMapSize.setModel(new SpinnerNumberModel(model.getMapSize(), 2, 100, 1));
		spinnerMapSize.addChangeListener(onMapSizeChange);

		comboTopology.setModel(new DefaultComboBoxModel<>(new String[] { _4_NEIGHBORS, _8_NEIGHBORS }));
		comboTopology.setSelectedItem(model.getMap().getTopology() == Top4.get() ? _4_NEIGHBORS : _8_NEIGHBORS);
		comboTopology.setAction(actionSelectTopology);

		comboAlgorithm.setModel(new DefaultComboBoxModel<>(PathFinderAlgorithm.values()));
		comboAlgorithm.setSelectedItem(controller.getSelectedAlgorithm());
		comboAlgorithm.setAction(actionSelectAlgorithm);

		animation = new Animation();
		animation.setFnDelay(sliderDelay::getValue);
		sliderDelay.setValue(5);

		cbAutoRunPathFinder.setSelected(controller.isAutoRunPathFinders());
		cbAutoRunPathFinder.setAction(actionToggleAutoPathFinding);

		actionClear.setEnabled(!controller.isAutoRunPathFinders());
		actionRunSelectedPathFinder.setEnabled(!controller.isAutoRunPathFinders());
	}

	public void updateUI() {
		if (pathFinderResults != null) {
			pathFinderResults.fireTableDataChanged();
		}
		if (canvas != null) {
			canvas.clear();
			canvas.drawGrid();
		}
	}

	public void updateCanvasAndUI() {
		if (canvas != null) {
			cellSize = initialHeight / model.getMapSize();
			canvas.setGrid(model.getMap());
			canvas.setCellSize(cellSize);
		}
		updateUI();
	}

	private ConfigurableGridRenderer createMapRenderer() {
		ConfigurableGridRenderer r = style == RenderingStyle.BLOCKS ? new WallPassageGridRenderer()
				: new PearlsGridRenderer();
		r.fnGridBgColor = () -> new Color(160, 160, 160);
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
			if (isCellPartOfSolution(cell)) {
				return Color.RED.brighter();
			}
			BreadthFirstSearch<Tile, Double> pf = model.getPathFinder(controller.getSelectedAlgorithm());
			if (pf.getState(cell) == COMPLETED) {
				return Color.ORANGE;
			}
			if (pf.getState(cell) == VISITED) {
				return Color.YELLOW;
			}
			return Color.WHITE;
		};
		r.fnText = cell -> {
			if (!cbShowCost.isSelected()) {
				return "";
			}
			if (model.getMap().get(cell) == Tile.WALL) {
				return "";
			}
			BreadthFirstSearch<Tile, Double> pf = model.getPathFinder(controller.getSelectedAlgorithm());
			if (pf.getState(cell) == UNVISITED) {
				return "";
			}
			double cost = pf.getCost(cell);
			if (pf instanceof AStarSearch) {
				AStarSearch<Tile, Double> astar = (AStarSearch<Tile, Double>) pf;
				cost = astar.getScore(cell);
			}
			return cost == Double.MAX_VALUE ? "" : String.format("%.0f", cost);
		};
		r.fnTextColor = cell -> {
			if (cell == model.getSource() || cell == model.getTarget() || isCellPartOfSolution(cell)) {
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

	private boolean isCellPartOfSolution(int cell) {
		PathFinderResult result = model.getResult(controller.getSelectedAlgorithm());
		return result != null && result.solutionCells.get(cell);
	}

	private int cellAt(int x, int y) {
		int gridX = min(x / cellSize, model.getMap().numCols() - 1);
		int gridY = min(y / cellSize, model.getMap().numRows() - 1);
		return model.getMap().cell(gridX, gridY);
	}
}