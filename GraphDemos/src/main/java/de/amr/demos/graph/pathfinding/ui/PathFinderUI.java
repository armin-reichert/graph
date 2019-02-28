package de.amr.demos.graph.pathfinding.ui;

import static java.lang.Math.min;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.amr.demos.graph.pathfinding.PathFinderAlgorithm;
import de.amr.demos.graph.pathfinding.PathFinderDemoApp;
import de.amr.demos.graph.pathfinding.Tile;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.AStarSearch;
import net.miginfocom.swing.MigLayout;

public class PathFinderUI extends JFrame {

	private Action actionSetSource = new AbstractAction("Search From Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.setSource(selectedCell);
			updatePath();
		}
	};

	private Action actionSetTarget = new AbstractAction("Search To Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.setTarget(selectedCell);
			updatePath();
		}
	};

	private Action actionChangeAlgorithm = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.setAlgorithm(comboAlgorithm.getItemAt(comboAlgorithm.getSelectedIndex()));
			updatePath();
		}
	};

	private Action actionChangeTopology = new AbstractAction() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if ("4 Neighbors".equals(comboTopology.getSelectedItem())) {
				app.setTopology(Top4.get());
			} else if ("8 Neighbors".equals(comboTopology.getSelectedItem())) {
				app.setTopology(Top8.get());
			}
			canvas.setGrid(app.getMap());
			canvas.clear();
			canvas.drawGrid();
		}
	};

	private Action actionResetScene = new AbstractAction("Reset Scene") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.resetScene();
			updatePath();
		}
	};

	private MouseListener mouseHandler = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent mouse) {
			if (mouse.getButton() == MouseEvent.BUTTON1) {
				int cell = cellAt(mouse.getX(), mouse.getY());
				app.changeTile(cell, app.getMap().get(cell) == Tile.WALL ? Tile.BLANK : Tile.WALL);
				updatePath();
			}
		}

		@Override
		public void mouseReleased(MouseEvent mouse) {
			if (draggedCell != -1) {
				// end dragging
				draggedCell = -1;
				updatePath();
			} else if (mouse.isPopupTrigger()) {
				selectedCell = cellAt(mouse.getX(), mouse.getY());
				int cell = cellAt(mouse.getX(), mouse.getY());
				actionSetSource.setEnabled(app.getMap().get(cell) == Tile.BLANK);
				actionSetTarget.setEnabled(app.getMap().get(cell) == Tile.BLANK);
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
				app.changeTile(cell, mouse.isShiftDown() ? Tile.BLANK : Tile.WALL);
				updatePath();
			}
		}
	};

	private PathFinderDemoApp app;
	private RenderingStyle style;
	private boolean costShown;
	private int cellSize;
	private int draggedCell;
	private int selectedCell;
	private GridCanvas canvas;

	private JComboBox<PathFinderAlgorithm> comboAlgorithm;
	private JComboBox<String> comboTopology;
	private JTable tablePathfinders;
	private JPopupMenu popupMenu;

	public PathFinderUI(PathFinderDemoApp app) {
		this();

		this.app = app;

		selectedCell = -1;
		draggedCell = -1;
		int windowHeight = Toolkit.getDefaultToolkit().getScreenSize().height * 90 / 100;
		cellSize = windowHeight / app.getMap().numCols();

		canvas = new GridCanvas(app.getMap(), cellSize);
		canvas.pushRenderer(createRenderer());
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseMotionListener(mouseMotionHandler);
		canvas.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		canvas.requestFocus();
		getContentPane().add(canvas, BorderLayout.CENTER);

		tablePathfinders.setModel(app.getPathFinderTableModel());

		popupMenu = new JPopupMenu();
		popupMenu.add(actionSetSource);
		popupMenu.add(actionSetTarget);
		popupMenu.addSeparator();
		popupMenu.add(actionResetScene);
	}

	public PathFinderUI() {

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Pathfinder Demo");

		JPanel settingsPanel = new JPanel();
		settingsPanel.setPreferredSize(new Dimension(500, 10));
		settingsPanel.setMinimumSize(new Dimension(500, 10));
		getContentPane().add(settingsPanel, BorderLayout.EAST);
		settingsPanel.setLayout(new MigLayout("", "[grow][grow]", "[][][][][][grow]"));

		JLabel lblAlgorithm = new JLabel("Algorithm");
		settingsPanel.add(lblAlgorithm, "cell 0 0,alignx trailing");

		comboAlgorithm = new JComboBox<>();
		comboAlgorithm.setAction(actionChangeAlgorithm);
		comboAlgorithm.setModel(new DefaultComboBoxModel<>(PathFinderAlgorithm.values()));
		settingsPanel.add(comboAlgorithm, "cell 1 0,growx");

		JLabel lblTopology = new JLabel("Topology");
		settingsPanel.add(lblTopology, "flowy,cell 0 1,alignx trailing");

		comboTopology = new JComboBox<>();
		comboTopology.setAction(actionChangeTopology);
		comboTopology.setModel(new DefaultComboBoxModel<>(new String[] { "4 Neighbors", "8 Neighbors" }));
		settingsPanel.add(comboTopology, "cell 1 1,growx");

		JLabel lblStyle = new JLabel("Style");
		settingsPanel.add(lblStyle, "cell 0 2,alignx trailing");

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
		settingsPanel.add(comboStyle, "cell 1 2,growx");

		JLabel lblShowCost = new JLabel("Show Cost");
		settingsPanel.add(lblShowCost, "cell 0 3,alignx trailing");

		costShown = false;
		JCheckBox cbShowCost = new JCheckBox("");
		cbShowCost.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				costShown = cbShowCost.isSelected();
				canvas.drawGrid();
			}
		});
		cbShowCost.setSelected(costShown);
		settingsPanel.add(cbShowCost, "cell 1 3,alignx center,aligny bottom");

		JScrollPane scrollPaneTable = new JScrollPane();
		settingsPanel.add(scrollPaneTable, "cell 0 5 2 1,growx,aligny top");

		tablePathfinders = new JTable();
		scrollPaneTable.setViewportView(tablePathfinders);
	}

	public void initState() {
		comboAlgorithm.setSelectedItem(app.getAlgorithm());
		comboTopology.setSelectedItem(app.getMap().getTopology() == Top4.get() ? "4 Neighbors" : "8 Neighbors");
	}

	private void updatePath() {
		app.runPathFinders();
		canvas.drawGrid();
	}

	private ConfigurableGridRenderer createRenderer() {
		ConfigurableGridRenderer r = style == RenderingStyle.BLOCKS ? new WallPassageGridRenderer()
				: new PearlsGridRenderer();
		r.fnGridBgColor = () -> Color.LIGHT_GRAY;
		r.fnCellSize = () -> cellSize;
		r.fnCellBgColor = cell -> {
			if (cell == app.getSource()) {
				return Color.BLUE;
			}
			if (cell == app.getTarget()) {
				return Color.GREEN.darker();
			}
			if (app.getSolution().get(cell)) {
				return Color.RED.brighter();
			}
			if (app.getSelectedPathFinder().getState(cell) == TraversalState.COMPLETED) {
				return Color.ORANGE;
			}
			if (app.getSelectedPathFinder().getState(cell) == TraversalState.VISITED) {
				return Color.YELLOW;
			}
			if (app.getMap().get(cell) == Tile.WALL) {
				return new Color(139, 69, 19);
			}
			return Color.WHITE;
		};
		r.fnText = this::cellText;
		r.fnTextColor = cell -> {
			if (cell == app.getSource() || cell == app.getTarget()) {
				return Color.WHITE;
			}
			if (app.getSolution().get(cell)) {
				return Color.WHITE;
			}
			return Color.BLUE;

		};
		r.fnTextFont = () -> new Font("Arial Narrow", Font.BOLD,
				style == RenderingStyle.PEARLS ? cellSize * 30 / 100 : cellSize * 50 / 100);
		r.fnMinFontSize = () -> 4;
		r.fnPassageWidth = (u, v) -> style == RenderingStyle.PEARLS ? 1 : cellSize - 1;
		r.fnPassageColor = (cell, dir) -> Color.WHITE;
		return r;
	}

	private int cellAt(int x, int y) {
		int gridX = min(x / cellSize, app.getMap().numCols() - 1);
		int gridY = min(y / cellSize, app.getMap().numRows() - 1);
		return app.getMap().cell(gridX, gridY);
	}

	private String cellText(int cell) {
		if (!costShown && cell != app.getTarget()) {
			return "";
		}
		if (app.getSelectedPathFinder().getState(cell) == TraversalState.UNVISITED) {
			return "";
		}
		if (app.getSelectedPathFinder() instanceof AStarSearch) {
			AStarSearch<Tile, Double> astar = (AStarSearch<Tile, Double>) app.getSelectedPathFinder();
			return String.format("%.0f", astar.getScore(cell));
		} else {
			return String.format("%.0f", app.getSelectedPathFinder().getCost(cell));
		}
	}
}