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
import de.amr.graph.grid.api.Topology;
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

	private class AnimationTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			model.getPathFinder(controller.getSelectedAlgorithm()).addObserver(animation);
			model.runPathFinder(controller.getSelectedAlgorithm());
			return null;
		}

		@Override
		protected void done() {
			model.getPathFinder(controller.getSelectedAlgorithm()).removeObserver(animation);
			updateUI();
		}
	}

	private Action actionRunAnimatedPathFinder = new AbstractAction("Run Pathfinder Animation") {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (model.getResults().get(controller.getSelectedAlgorithm()) != null) {
				model.getResults().get(controller.getSelectedAlgorithm()).clear();
			}
			model.getPathFinder(controller.getSelectedAlgorithm()).init();
			canvas.clear();
			canvas.drawGrid();
			new AnimationTask().execute();
		}
	};

	private Action actionClear = new AbstractAction("Clear") {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.getResults().get(controller.getSelectedAlgorithm()).clear();
			model.getPathFinder(controller.getSelectedAlgorithm()).init();
			canvas.drawGrid();
		}
	};

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
			PathFinderAlgorithm algorithm = comboAlgorithm.getItemAt(comboAlgorithm.getSelectedIndex());
			controller.setSelectedAlgorithm(algorithm);
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

	private Action actionTogglePathFinding = new AbstractAction("Run automatically") {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean checked = cbAutoRunPathFinder.isSelected();
			controller.setAutoRunPathFinders(checked);
			actionRunAnimatedPathFinder.setEnabled(!checked);
			actionClear.setEnabled(!checked);
			tableResults.setVisible(checked);
			if (checked) {
				model.runPathFinders();
				updateUI();
			} else {
				canvas.clear();
				canvas.drawGrid();
			}
		}
	};

	private Action actionChangeStyle = new AbstractAction("Style") {

		@Override
		public void actionPerformed(ActionEvent e) {
			style = (RenderingStyle) comboStyle.getSelectedItem();
			canvas.popRenderer();
			canvas.pushRenderer(createMapRenderer());
			canvas.clear();
			canvas.drawGrid();
		}
	};

	private Action actionShowCost = new AbstractAction("Show Cost") {

		@Override
		public void actionPerformed(ActionEvent e) {
			canvas.clear();
			canvas.drawGrid();
		}
	};

	private ChangeListener onMapSizeChange = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			controller.setMapSize((int) spinnerMapSize.getValue());
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
					model.runPathFinders();
				}
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

	private JComboBox<PathFinderAlgorithm> comboAlgorithm;
	private JComboBox<String> comboTopology;
	private JTable tableResults;
	private PathFinderTableModel tableModelResults;
	private JPopupMenu popupMenu;
	private JSpinner spinnerMapSize;
	private JCheckBox cbShowCost;
	private JLabel lblPathFinding;
	private JPanel settingsPanel;
	private JCheckBox cbAutoRunPathFinder;
	private JComboBox<RenderingStyle> comboStyle;
	private JSlider sliderDelay;

	public PathFinderDemoView() {

		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Pathfinder Demo");

		settingsPanel = new JPanel();
		settingsPanel.setPreferredSize(new Dimension(500, 10));
		settingsPanel.setMinimumSize(new Dimension(500, 10));
		getContentPane().add(settingsPanel, BorderLayout.CENTER);
		settingsPanel.setLayout(
				new MigLayout("", "[grow,trailing][5px:n:5px,fill][grow]", "[][][][][][][][][][][][grow][grow]"));

		Component verticalStrut = Box.createVerticalStrut(20);
		settingsPanel.add(verticalStrut, "cell 0 4");

		lblPathFinding = new JLabel("Path Finding");
		lblPathFinding.setFont(new Font("SansSerif", Font.BOLD, 14));
		settingsPanel.add(lblPathFinding, "cell 0 5");

		JLabel lblMapSize = new JLabel("Rows/Cols");
		settingsPanel.add(lblMapSize, "cell 0 1,alignx trailing");

		spinnerMapSize = new JSpinner();
		settingsPanel.add(spinnerMapSize, "cell 2 1");

		JPanel panel = new JPanel();
		settingsPanel.add(panel, "flowx,cell 2 7,growx");

		JButton btnRun = new JButton("Run");
		panel.add(btnRun);
		btnRun.setAction(actionRunAnimatedPathFinder);

		JButton btnNewButton = new JButton();
		panel.add(btnNewButton);
		btnNewButton.setAction(actionClear);

		JLabel lblDelay = new JLabel("Delay [ms]");
		settingsPanel.add(lblDelay, "cell 0 8,alignx trailing,aligny top");

		sliderDelay = new JSlider();
		sliderDelay.setMaximum(50);
		sliderDelay.setMinorTickSpacing(2);
		sliderDelay.setPaintTicks(true);
		sliderDelay.setPaintLabels(true);
		sliderDelay.setMajorTickSpacing(10);
		settingsPanel.add(sliderDelay, "cell 2 8,growx");

		cbAutoRunPathFinder = new JCheckBox("Run Automatically");
		settingsPanel.add(cbAutoRunPathFinder, "cell 2 9,alignx leading,aligny center");

		JLabel lblAlgorithm = new JLabel("Algorithm");
		settingsPanel.add(lblAlgorithm, "cell 0 6,alignx trailing");

		comboAlgorithm = new JComboBox<>();
		settingsPanel.add(comboAlgorithm, "cell 2 6,growx");

		JLabel lblMap = new JLabel("Map");
		lblMap.setFont(new Font("SansSerif", Font.BOLD, 14));
		settingsPanel.add(lblMap, "cell 0 0");

		JLabel lblTopology = new JLabel("Topology");
		settingsPanel.add(lblTopology, "flowy,cell 0 2,alignx trailing");

		comboTopology = new JComboBox<>();
		settingsPanel.add(comboTopology, "cell 2 2,growx");

		JLabel lblStyle = new JLabel("Display Style");
		settingsPanel.add(lblStyle, "cell 0 3,alignx trailing");

		style = RenderingStyle.BLOCKS;
		comboStyle = new JComboBox<>();
		comboStyle.setAction(actionChangeStyle);
		comboStyle.setModel(new DefaultComboBoxModel<>(RenderingStyle.values()));
		settingsPanel.add(comboStyle, "cell 2 3,growx");

		cbShowCost = new JCheckBox("Show Cost");
		cbShowCost.setAction(actionShowCost);
		settingsPanel.add(cbShowCost, "cell 2 10,alignx leading,aligny bottom");

		JScrollPane scrollPaneTableResults = new JScrollPane();
		scrollPaneTableResults.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		settingsPanel.add(scrollPaneTableResults, "cell 0 11 3 1,growx,aligny top");

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
		settingsPanel.add(textLegend, "cell 0 12 3 1,grow");

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
		int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height * 90 / 100;
		cellSize = windowHeight / model.getMapSize();
		canvas = new GridCanvas(model.getMap(), cellSize);
		canvas.pushRenderer(createMapRenderer());
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseMotionListener(mouseMotionHandler);
		canvas.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		canvas.requestFocus();
		getContentPane().add(canvas, BorderLayout.WEST);

		// path finder results table
		tableModelResults = new PathFinderTableModel(model.getResults());
		tableResults.setModel(tableModelResults);
		tableResults.setVisible(controller.isAutoRunPathFinders());
		tableResults.getColumnModel().getColumn(0).setPreferredWidth(140);

		// controls for different settings

		spinnerMapSize.setModel(new SpinnerNumberModel(model.getMapSize(), 2, 100, 1));
		spinnerMapSize.addChangeListener(onMapSizeChange);

		comboTopology.setModel(new DefaultComboBoxModel<>(new String[] { "4 Neighbors", "8 Neighbors" }));
		comboTopology.setSelectedItem(model.getMap().getTopology() == Top4.get() ? "4 Neighbors" : "8 Neighbors");
		comboTopology.setAction(actionChangeTopology);

		comboAlgorithm.setModel(new DefaultComboBoxModel<>(PathFinderAlgorithm.values()));
		comboAlgorithm.setSelectedItem(controller.getSelectedAlgorithm());
		comboAlgorithm.setAction(actionChangeAlgorithm);

		animation = new Animation();
		animation.setFnDelay(sliderDelay::getValue);
		sliderDelay.setValue(5);

		cbAutoRunPathFinder.setSelected(controller.isAutoRunPathFinders());
		cbAutoRunPathFinder.setAction(actionTogglePathFinding);

		actionClear.setEnabled(!controller.isAutoRunPathFinders());
		actionRunAnimatedPathFinder.setEnabled(!controller.isAutoRunPathFinders());
	}

	public void updateUI() {
		if (tableModelResults != null) {
			tableModelResults.fireTableDataChanged();
		}
		if (canvas != null) {
			canvas.clear();
			canvas.drawGrid();
		}
	}

	public void updateUIAndResetCanvas() {
		if (tableModelResults != null) {
			tableModelResults.fireTableDataChanged();
		}
		if (canvas != null) {
			canvas.setGrid(model.getMap());
			int newCellSize = getContentPane().getHeight() / model.getMapSize();
			if (newCellSize > 0) {
				cellSize = newCellSize;
				canvas.setCellSize(cellSize);
			}
			canvas.clear();
			canvas.drawGrid();
		}
	}

	private ConfigurableGridRenderer createMapRenderer() {
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
		PathFinderResult result = model.getResults().get(controller.getSelectedAlgorithm());
		return result != null && result.solutionCells.get(cell);
	}

	private int cellAt(int x, int y) {
		int gridX = min(x / cellSize, model.getMap().numCols() - 1);
		int gridY = min(y / cellSize, model.getMap().numRows() - 1);
		return model.getMap().cell(gridX, gridY);
	}
}