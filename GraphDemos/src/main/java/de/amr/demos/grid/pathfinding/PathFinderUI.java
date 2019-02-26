package de.amr.demos.grid.pathfinding;

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
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
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
	private RenderingStyle style;

	public void setApp(PathFinderDemoApp app) {
		this.app = app;

		canvas = new GridCanvas(app.getMap(), app.getCellSize());
		getContentPane().add(canvas, BorderLayout.CENTER);
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
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getCanonicalName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Pathfinder Demo");

		JPanel settingsPanel = new JPanel();
		settingsPanel.setPreferredSize(new Dimension(300, 10));
		settingsPanel.setMinimumSize(new Dimension(300, 10));
		getContentPane().add(settingsPanel, BorderLayout.EAST);
		settingsPanel.setLayout(new MigLayout("", "[grow][grow]", "[][][][][grow]"));

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
		settingsPanel.add(lblTopology, "flowy,cell 0 1,alignx trailing");

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

		JLabel lblStyle = new JLabel("Style");
		settingsPanel.add(lblStyle, "cell 0 2,alignx trailing");

		style = RenderingStyle.WALL_PASSAGES;
		JComboBox<RenderingStyle> comboStyle = new JComboBox<>();
		comboStyle.addActionListener(e -> {
			style = (RenderingStyle) comboStyle.getSelectedItem();
			canvas.popRenderer();
			canvas.pushRenderer(createRenderer());
			redraw(true);
		});
		comboStyle.setModel(new DefaultComboBoxModel<>(RenderingStyle.values()));
		settingsPanel.add(comboStyle, "cell 1 2,growx");

		JLabel lblPassageWidth = new JLabel("Passage Width");
		settingsPanel.add(lblPassageWidth, "cell 0 3,alignx trailing");

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
		settingsPanel.add(sliderPassageWidth, "cell 1 3");

		JScrollPane scrollPane = new JScrollPane();
		settingsPanel.add(scrollPane, "cell 0 4 2 1,grow");

		textLog = new JTextArea();
		textLog.setFont(new Font("Monospaced", Font.PLAIN, 14));
		textLog.setEditable(false);
		scrollPane.setViewportView(textLog);

		popupCell = -1;
		draggedCell = -1;

		popupMenu = new JPopupMenu();
		popupMenu.add(actionSetSource);
		popupMenu.add(actionSetTarget);
		popupMenu.addSeparator();
		popupMenu.add(actionResetScene);
	}

	public GridCanvas getCanvas() {
		return canvas;
	}

	public void redraw(boolean clear) {
		if (clear) {
			canvas.clear();
		}
		canvas.drawGrid();
	}

	public void log(String line, Object... args) {
		textLog.append(String.format(line, args));
		textLog.append("\n");
	}

	private ConfigurableGridRenderer createRenderer() {
		ConfigurableGridRenderer r = style == RenderingStyle.WALL_PASSAGES ? new WallPassageGridRenderer()
				: new PearlsGridRenderer();
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
			if (app.getSolution() != null && app.getSolution().get(cell)) {
				return Color.WHITE;
			}
			return Color.BLUE;

		};
		r.fnTextFont = () -> new Font("Arial Narrow", Font.BOLD,
				style == RenderingStyle.PEARLS ? app.getCellSize() * 30 / 100 : app.getCellSize() * 40 / 100);
		r.fnMinFontSize = () -> 4;
		r.fnPassageWidth = (u, v) -> style == RenderingStyle.PEARLS ? 1
				: app.getCellSize() * app.getPassageWidthPct() / 100;
		r.fnPassageColor = (cell, dir) -> Color.WHITE;
		return r;
	}

	private MouseListener mouseHandler = new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent mouse) {
			if (mouse.getButton() == MouseEvent.BUTTON1) {
				int cell = app.cellAt(mouse.getX(), mouse.getY());
				app.changeTile(cell, app.getMap().get(cell) == Tile.WALL ? Tile.BLANK : Tile.WALL);
				app.computePath();
				redraw(false);
			}
		}

		@Override
		public void mouseReleased(MouseEvent mouse) {
			if (draggedCell != -1) {
				// dragging ends
				draggedCell = -1;
				app.computePath();
				redraw(false);
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
				app.changeTile(cell, mouse.isShiftDown() ? Tile.BLANK : Tile.WALL);
				app.computePath();
				redraw(false);
			}
		}
	};
	private Action actionSetSource = new AbstractAction("Set Source Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.setSource(popupCell);
			popupCell = -1;
			app.computePath();
			redraw(false);
		}
	};

	private Action actionSetTarget = new AbstractAction("Set Target Here") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.setTarget(popupCell);
			popupCell = -1;
			app.computePath();
			redraw(false);
		}
	};

	private Action actionResetScene = new AbstractAction("Reset Scene") {

		@Override
		public void actionPerformed(ActionEvent e) {
			app.resetScene();
			app.computePath();
			redraw(true);
		}
	};
	private JTextArea textLog;

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