package de.amr.demos.graph.pathfinding.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.demos.graph.pathfinding.controller.Controller;
import de.amr.demos.graph.pathfinding.model.PathFinderModel;
import de.amr.demos.graph.pathfinding.model.PathFinderAlgorithm;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
import net.miginfocom.swing.MigLayout;

/**
 * Main view of path finder demo app.
 * 
 * @author Armin Reichert
 */
public class MainView extends JPanel {

	private static final String _4_NEIGHBORS = "4 Neighbors";
	private static final String _8_NEIGHBORS = "8 Neighbors";

	private Action actionRunSelectedPathFinder = new AbstractAction("Run Selected Path Finder") {

		@Override
		public void actionPerformed(ActionEvent e) {
			canvasView.runPathFinderAnimation();
		}
	};

	private Action actionClear = new AbstractAction("Clear") {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.newRun(controller.getSelectedAlgorithm());
			canvasView.drawGrid();
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

	private Action actionToggleAutoPathFinding = new AbstractAction("Run All") {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean auto = cbAutoRunPathFinder.isSelected();
			actionRunSelectedPathFinder.setEnabled(!auto);
			actionClear.setEnabled(!auto);
			sliderDelay.setEnabled(!auto);
			scrollPaneTableResults.setVisible(auto);
			controller.setAutoRunPathFinders(auto);
			if (auto) {
				controller.runAllPathFinders();
			} else {
				model.newRun(controller.getSelectedAlgorithm());
				canvasView.drawGrid();
			}
		}
	};

	private Action actionSelectMapStyle = new AbstractAction("Style") {

		@Override
		public void actionPerformed(ActionEvent e) {
			canvasView.setStyle(comboStyle.getItemAt(comboStyle.getSelectedIndex()));
		}
	};

	private Action actionShowCost = new AbstractAction("Show Cost") {

		@Override
		public void actionPerformed(ActionEvent e) {
			canvasView.setShowCost(cbShowCost.isSelected());
		}
	};

	private ChangeListener onMapSizeChange = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			// System.out.println("Map size changed to " + spinnerMapSize.getValue());
			controller.resizeMap((int) spinnerMapSize.getValue());
		}
	};

	private PathFinderModel model;
	private Controller controller;

	private CanvasView canvasView;
	private JComboBox<PathFinderAlgorithm> comboAlgorithm;
	private JComboBox<String> comboTopology;
	private ResultsTable tableResults;
	private JSpinner spinnerMapSize;
	private JCheckBox cbShowCost;
	private JLabel lblPathFinding;
	private JPanel panelActions;
	private JCheckBox cbAutoRunPathFinder;
	private JComboBox<RenderingStyle> comboStyle;
	private JSlider sliderDelay;
	private JPanel panelMap;
	private JScrollPane scrollPaneTableResults;
	private HelpPanel helpPanel;

	public MainView() {
		setOpaque(false);
		setLayout(new MigLayout("", "[][grow]", "[grow,fill]"));

		panelMap = new JPanel();
		panelMap.setPreferredSize(new Dimension(500, 10));
		panelMap.setBackground(Color.WHITE);
		add(panelMap, "cell 0 0,growy");
		panelMap.setLayout(new BorderLayout(0, 0));

		panelActions = new JPanel();
		panelActions.setMinimumSize(new Dimension(550, 10));
		panelActions.setBackground(Color.WHITE);
		panelActions.setPreferredSize(new Dimension(500, 50));
		add(panelActions, "cell 1 0,grow");
		panelActions.setLayout(new MigLayout("", "[grow,center][grow]", "[][][][][][][][][][][][grow,bottom]"));

		JLabel lblMap = new JLabel("Map");
		panelActions.add(lblMap, "cell 0 0 2 1,alignx leading");
		lblMap.setForeground(SystemColor.textHighlight);
		lblMap.setFont(new Font("Arial Black", Font.PLAIN, 16));

		lblPathFinding = new JLabel("Path Finding");
		lblPathFinding.setForeground(SystemColor.textHighlight);
		lblPathFinding.setFont(new Font("Arial Black", Font.PLAIN, 16));
		panelActions.add(lblPathFinding, "cell 0 5 2 1,alignx leading");

		JLabel lblMapSize = new JLabel("Rows/Cols");
		panelActions.add(lblMapSize, "cell 0 1,alignx trailing");

		spinnerMapSize = new JSpinner();
		panelActions.add(spinnerMapSize, "cell 1 1");

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panelActions.add(panel, "flowx,cell 1 7,growx");

		JButton btnRun = new JButton("Run");
		panel.add(btnRun);
		btnRun.setAction(actionRunSelectedPathFinder);

		JButton btnNewButton = new JButton();
		panel.add(btnNewButton);
		btnNewButton.setAction(actionClear);

		JLabel lblDelay = new JLabel("Delay [ms]");
		panelActions.add(lblDelay, "cell 0 8,alignx trailing,aligny top");

		sliderDelay = new JSlider();
		sliderDelay.setValue(10);
		sliderDelay.setMaximum(50);
		sliderDelay.setMinorTickSpacing(1);
		sliderDelay.setPaintTicks(true);
		sliderDelay.setPaintLabels(true);
		sliderDelay.setMajorTickSpacing(5);
		panelActions.add(sliderDelay, "cell 1 8,growx");

		JLabel lblAlgorithm = new JLabel("Algorithm");
		panelActions.add(lblAlgorithm, "cell 0 6,alignx trailing");

		comboAlgorithm = new JComboBox<>();
		panelActions.add(comboAlgorithm, "cell 1 6,growx");

		JLabel lblTopology = new JLabel("Topology");
		panelActions.add(lblTopology, "flowy,cell 0 2,alignx trailing");

		comboTopology = new JComboBox<>();
		panelActions.add(comboTopology, "cell 1 2,growx");

		JLabel lblStyle = new JLabel("Display Style");
		panelActions.add(lblStyle, "cell 0 3,alignx trailing");

		comboStyle = new JComboBox<>();
		comboStyle.setAction(actionSelectMapStyle);
		comboStyle.setModel(new DefaultComboBoxModel<>(RenderingStyle.values()));
		panelActions.add(comboStyle, "cell 1 3,growx");

		cbAutoRunPathFinder = new JCheckBox("Run Automatically");
		panelActions.add(cbAutoRunPathFinder, "cell 0 9,alignx leading");

		cbShowCost = new JCheckBox("Show Cost");
		cbShowCost.setAction(actionShowCost);
		panelActions.add(cbShowCost, "cell 1 9,alignx leading,aligny bottom");

		scrollPaneTableResults = new JScrollPane();
		scrollPaneTableResults.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		panelActions.add(scrollPaneTableResults, "cell 0 10 2 1,growx,aligny top");

		tableResults = new ResultsTable();
		tableResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		tableResults.setPreferredScrollableViewportSize(new Dimension(500, 64));
		tableResults.setFillsViewportHeight(true);
		tableResults.setEnabled(false);
		tableResults.setShowVerticalLines(false);
		scrollPaneTableResults.setViewportView(tableResults);

		helpPanel = new HelpPanel();
		panelActions.add(helpPanel, "cell 0 11 2 1,growx,aligny bottom");
	}

	public void init(PathFinderModel model, Controller controller) {
		this.model = model;
		this.controller = controller;

		// canvas
		int height = Toolkit.getDefaultToolkit().getScreenSize().height * 85 / 100;
		canvasView = new CanvasView(model.getMap(), height);
		canvasView.init(model, controller);
		canvasView.setStyle(comboStyle.getItemAt(comboStyle.getSelectedIndex()));
		canvasView.setShowCost(cbShowCost.isSelected());
		canvasView.getAnimation().setFnDelay(sliderDelay::getValue);
		canvasView.fixHeight(canvasView.getSize().height);
		panelMap.add(canvasView, BorderLayout.CENTER);

		// path finder results table
		scrollPaneTableResults.setVisible(controller.isAutoRunPathFinders());
		tableResults.init(model);

		// others controls
		spinnerMapSize.setModel(new SpinnerNumberModel(model.getMapSize(), 2, 100, 1));
		spinnerMapSize.addChangeListener(onMapSizeChange);

		comboTopology.setModel(new DefaultComboBoxModel<>(new String[] { _4_NEIGHBORS, _8_NEIGHBORS }));
		comboTopology.setSelectedItem(model.getMap().getTopology() == Top4.get() ? _4_NEIGHBORS : _8_NEIGHBORS);
		comboTopology.setAction(actionSelectTopology);

		comboAlgorithm.setModel(new DefaultComboBoxModel<>(PathFinderAlgorithm.values()));
		comboAlgorithm.setSelectedItem(controller.getSelectedAlgorithm());
		comboAlgorithm.setAction(actionSelectAlgorithm);

		cbAutoRunPathFinder.setSelected(controller.isAutoRunPathFinders());
		cbAutoRunPathFinder.setAction(actionToggleAutoPathFinding);

		actionClear.setEnabled(!controller.isAutoRunPathFinders());
		actionRunSelectedPathFinder.setEnabled(!controller.isAutoRunPathFinders());
	}

	public void updateView() {
		tableResults.dataChanged();
		if (canvasView != null) {
			canvasView.clear();
			canvasView.drawGrid();
		}
	}

	public void updateCanvas() {
		if (canvasView != null) {
			canvasView.setGrid(model.getMap());
		}
	}
}