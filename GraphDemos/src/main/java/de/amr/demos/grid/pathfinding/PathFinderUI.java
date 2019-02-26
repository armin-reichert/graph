package de.amr.demos.grid.pathfinding;

import static de.amr.demos.grid.pathfinding.PathFinderDemoApp.Tile.FREE;
import static de.amr.demos.grid.pathfinding.PathFinderDemoApp.Tile.WALL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.amr.demos.grid.pathfinding.PathFinderDemoApp.PathFinderAlgorithm;
import de.amr.demos.grid.pathfinding.PathFinderDemoApp.Tile;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.graph.pathfinder.api.TraversalState;
import de.amr.graph.pathfinder.impl.AStarSearch;
import de.amr.graph.pathfinder.impl.BreadthFirstSearch;
import net.miginfocom.swing.MigLayout;

public class PathFinderUI extends JFrame {

	private PathFinderDemoApp app;

	private JComboBox<PathFinderAlgorithm> comboAlgorithm;
	private JComboBox<String> comboTopology;
	private JSlider sliderPassageWidth;
	private GridCanvas canvas;
	private int draggedCell;
	private int popupCell;
	private JPopupMenu popupMenu;

	public void setApp(PathFinderDemoApp app) {
		this.app = app;

		canvas = new GridCanvas(app.getMap(), app.getCellSize());
		add(canvas, BorderLayout.CENTER);
		canvas.pushRenderer(createRenderer());
		canvas.addMouseListener(mouseHandler);
		canvas.addMouseMotionListener(mouseMotionHandler);
		canvas.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		canvas.requestFocus();

		comboAlgorithm.setSelectedItem(app.getAlgorithm());
		comboTopology.setSelectedItem(app.getTopology() == Top4.get() ? "4 Neighbors" : "8 Neighbors");
		sliderPassageWidth.setValue(app.getPassageWidthPct());
	}

	public PathFinderUI() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Pathfinder Demo");

		JPanel settingsPanel = new JPanel();
		settingsPanel.setPreferredSize(new Dimension(300, 10));
		settingsPanel.setMinimumSize(new Dimension(300, 10));
		getContentPane().add(settingsPanel, BorderLayout.EAST);
		settingsPanel.setLayout(new MigLayout("", "[][grow]", "[][][]"));

		JLabel lblAlgorithm = new JLabel("Algorithm");
		settingsPanel.add(lblAlgorithm, "cell 0 0,alignx trailing");

		comboAlgorithm = new JComboBox<>();
		comboAlgorithm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				app.setAlgorithm(comboAlgorithm.getItemAt(comboAlgorithm.getSelectedIndex()));
			}
		});
		comboAlgorithm.setModel(new DefaultComboBoxModel<>(PathFinderAlgorithm.values()));
		settingsPanel.add(comboAlgorithm, "cell 1 0,growx");

		JLabel lblTopology = new JLabel("Topology");
		settingsPanel.add(lblTopology, "cell 0 1,alignx trailing");

		comboTopology = new JComboBox<>();
		comboTopology.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ("4 Neighbors".equals(comboTopology.getSelectedItem())) {
					app.setTopology(Top4.get());
				} else if ("8 Neighbors".equals(comboTopology.getSelectedItem())) {
					app.setTopology(Top8.get());
				}
			}
		});
		comboTopology.setModel(new DefaultComboBoxModel<>(new String[] { "4 Neighbors", "8 Neighbors" }));
		settingsPanel.add(comboTopology, "cell 1 1,growx");

		JLabel lblPassageWidth = new JLabel("Passage Width");
		settingsPanel.add(lblPassageWidth, "cell 0 2");

		sliderPassageWidth = new JSlider();
		sliderPassageWidth.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (app == null) {
					return;
				}
				app.setPassageWidthPct(sliderPassageWidth.getValue());
				redraw(true);
			}
		});
		sliderPassageWidth.setMinorTickSpacing(10);
		sliderPassageWidth.setMinimum(1);
		sliderPassageWidth.setPaintTicks(true);
		settingsPanel.add(sliderPassageWidth, "cell 1 2");

		popupCell = -1;
		draggedCell = -1;

		popupMenu = new JPopupMenu();
		popupMenu.add(actionSetSource);
		popupMenu.add(actionSetTarget);
		popupMenu.addSeparator();
		popupMenu.add(actionResetScene);
	}

	public void redraw(boolean clear) {
		if (clear) {
			canvas.clear();
		}
		canvas.drawGrid();
	}

	private ConfigurableGridRenderer createRenderer() {
		// ConfigurableGridRenderer r = new PearlsGridRenderer();
		ConfigurableGridRenderer r = new WallPassageGridRenderer();
		r.fnCellSize = () -> app.getCellSize();
		r.fnCellBgColor = cell -> {
			if (cell == app.getSource()) {
				return Color.GREEN.darker();
			}
			if (cell == app.getTarget()) {
				return Color.BLUE;
			}
			if (app.getSolution() != null && app.getSolution().get(cell)) {
				return Color.RED.brighter();
			}
			if (app.getPathFinder() != null) {
				if (app.getPathFinder().getState(cell) == TraversalState.COMPLETED) {
					return new Color(160, 160, 160);
				}
				if (app.getPathFinder().getState(cell) == TraversalState.VISITED) {
					return new Color(220, 220, 220);
				}
			}
			if (app.getMap().get(cell) == WALL) {
				return new Color(139, 69, 19);
			}
			return Color.WHITE;
		};
		r.fnText = this::cellText;
		r.fnTextColor = cell -> {
			if (cell == app.getSource() || cell == app.getTarget()) {
				return Color.WHITE;
			}
			if (app.getSolution() != null && app.getSolution().get(cell)) {
				return Color.WHITE;
			}
			return Color.BLUE;

		};
		r.fnTextFont = () -> new Font("Arial Narrow", Font.BOLD, app.getCellSize() * 40 / 100);
		r.fnMinFontSize = () -> 4;
		r.fnPassageWidth = (u, v) -> app.getCellSize() * app.getPassageWidthPct() / 100;
		r.fnPassageColor = (cell, dir) -> Color.WHITE;
		return r;
	}

	private MouseListener mouseHandler = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent mouse) {
			if (mouse.getButton() == MouseEvent.BUTTON1) {
				int cell = app.cellAt(mouse.getX(), mouse.getY());
				app.setTile(cell, app.getMap().get(cell) == WALL ? FREE : WALL);
				app.updatePath();
			}
		}

		@Override
		public void mouseReleased(MouseEvent mouse) {
			if (draggedCell != -1) {
				// dragging ends
				draggedCell = -1;
				app.updatePath();
			} else if (mouse.isPopupTrigger()) {
				popupCell = app.cellAt(mouse.getX(), mouse.getY());
				popupMenu.show(canvas, mouse.getX(), mouse.getY());
			}
		}
	};

	private MouseMotionListener mouseMotionHandler = new MouseMotionAdapter() {

		@Override
		public void mouseDragged(MouseEvent mouse) {
			int cell = app.cellAt(mouse.getX(), mouse.getY());
			if (cell != draggedCell) {
				// drag enters new cell
				draggedCell = cell;
				app.setTile(cell, mouse.isShiftDown() ? FREE : WALL);
				app.updatePath();
			}
		}
	};
	private Action actionSetSource = new AbstractAction("Set Source Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.setSource(popupCell);
			popupCell = -1;
			app.updatePath();
		}
	};

	private Action actionSetTarget = new AbstractAction("Set Target Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.setTarget(popupCell);
			popupCell = -1;
			app.updatePath();
		}
	};

	private Action actionResetScene = new AbstractAction("Reset Scene") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.resetScene();
			app.updatePath();
		}
	};

	private String cellText(int cell) {
		if (app.getPathFinder() == null || app.getPathFinder().getState(cell) == TraversalState.UNVISITED) {
			return "";
		}
		if (app.getPathFinder() instanceof AStarSearch) {
			AStarSearch<Tile, Double> astar = (AStarSearch<Tile, Double>) app.getPathFinder();
			return String.format("%.0f", astar.getScore(cell));
		} else if (app.getPathFinder() instanceof BreadthFirstSearch) {
			BreadthFirstSearch<Tile, Double> bfs = app.getPathFinder();
			return String.format("%.0f", bfs.getCost(cell));
		}
		return "";
	}

}