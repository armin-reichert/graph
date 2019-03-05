package de.amr.demos.graph.pathfinding.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
import net.miginfocom.swing.MigLayout;

/**
 * UI for path finder demo app.
 * 
 * @author Armin Reichert
 */
public class PathFinderDemoView extends JFrame {

	private static final String _4_NEIGHBORS = "4 Neighbors";
	private static final String _8_NEIGHBORS = "8 Neighbors";

	private class PathFinderAnimationTask extends SwingWorker<Void, Void> {

		@Override
		protected Void doInBackground() throws Exception {
			PathFinderAlgorithm algorithm = controller.getSelectedAlgorithm();
			model.clearResult(algorithm);
			model.newPathFinder(algorithm);
			canvas.drawGrid();
			model.getPathFinder(algorithm).addObserver(canvas.getAnimation());
			model.runPathFinder(algorithm);
			model.getPathFinder(algorithm).removeObserver(canvas.getAnimation());
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
			controller.setSource(canvas.getSelectedCell());
		}
	};

	private Action actionSetTarget = new AbstractAction("End Search Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.setTarget(canvas.getSelectedCell());
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
			canvas.setStyle(comboStyle.getItemAt(comboStyle.getSelectedIndex()));
		}
	};

	private Action actionShowCost = new AbstractAction("Show Cost") {

		@Override
		public void actionPerformed(ActionEvent e) {
			canvas.setShowCost(cbShowCost.isSelected());
		}
	};

	private ChangeListener onMapSizeChange = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			controller.resizeMap((int) spinnerMapSize.getValue());
		}
	};

	private PathFinderDemoModel model;
	private PathFinderDemoController controller;

	// UI specific
	private int cellSize;
	private MapCanvas canvas;
	private int initialHeight;

	private JComboBox<PathFinderAlgorithm> comboAlgorithm;
	private JComboBox<String> comboTopology;
	private JTable tableResults;
	private PathFinderResultsTableModel pathFinderResults;
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

	}

	public void init(PathFinderDemoModel model, PathFinderDemoController controller) {
		this.model = model;
		this.controller = controller;

		// canvas
		cellSize = (Toolkit.getDefaultToolkit().getScreenSize().height * 90 / 100) / model.getMapSize();
		canvas = new MapCanvas(model.getMap(), cellSize);
		canvas.setModel(model);
		canvas.setController(controller);
		canvas.setStyle(comboStyle.getItemAt(comboStyle.getSelectedIndex()));
		canvas.setShowCost(cbShowCost.isSelected());
		canvas.requestFocus();
		canvas.getAnimation().setFnDelay(sliderDelay::getValue);

		canvas.getContextMenu().add(actionSetSource);
		canvas.getContextMenu().add(actionSetTarget);
		canvas.getContextMenu().addSeparator();
		canvas.getContextMenu().add(actionResetScene);

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
}