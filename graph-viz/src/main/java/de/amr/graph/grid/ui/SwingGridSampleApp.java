package de.amr.graph.grid.ui;

import static de.amr.graph.core.api.TraversalState.COMPLETED;
import static de.amr.graph.core.api.TraversalState.UNVISITED;
import static java.awt.EventQueue.invokeLater;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import de.amr.graph.core.api.TraversalState;
import de.amr.graph.grid.api.GridPosition;
import de.amr.graph.grid.api.GridTopology;
import de.amr.graph.grid.impl.Grid4Topology;
import de.amr.graph.grid.impl.GridFactory;
import de.amr.graph.grid.impl.ObservableGridGraph;
import de.amr.graph.grid.ui.animation.BFSAnimation;
import de.amr.graph.grid.ui.animation.GridCanvasAnimation;
import de.amr.graph.grid.ui.rendering.ConfigurableGridRenderer;
import de.amr.graph.grid.ui.rendering.GridCanvas;
import de.amr.graph.grid.ui.rendering.PearlsGridRenderer;
import de.amr.graph.grid.ui.rendering.WallPassageGridRenderer;
import de.amr.swing.Swing;
import de.amr.util.StopWatch;

/**
 * Base class for grid sample applications.
 * 
 * @author Armin Reichert
 */
public abstract class SwingGridSampleApp implements Runnable {

	public enum Style {
		WALL_PASSAGE, PEARLS
	};

	public static void launch(SwingGridSampleApp app) {
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		invokeLater(() -> new Thread(app).start());
	}

	public static Dimension getScreenSize() {
		DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDisplayMode();
		return new Dimension(displayMode.getWidth(), displayMode.getHeight());
	}

	private JFrame window;
	private Dimension canvasSize;
	private GridCanvas canvas;
	private GridCanvasAnimation<TraversalState, Integer> canvasAnimation;
	private Style style;
	private String appName;
	private boolean fullscreen;
	private ObservableGridGraph<TraversalState, Integer> grid;

	protected final StopWatch watch = new StopWatch();

	public SwingGridSampleApp(int width, int height, int cellSize) {
		fullscreen = false;
		style = Style.WALL_PASSAGE;
		canvasSize = new Dimension(width, height);
		grid = GridFactory.emptyObservableGrid(width / cellSize, height / cellSize, Grid4Topology.get(), UNVISITED, 0);
		createUI(cellSize);
	}

	public SwingGridSampleApp(int cellSize) {
		fullscreen = true;
		style = Style.WALL_PASSAGE;
		canvasSize = getScreenSize();
		grid = GridFactory.emptyObservableGrid(canvasSize.width / cellSize, canvasSize.height / cellSize,
				Grid4Topology.get(), UNVISITED, 0);
		grid.setDefaultVertexLabel(v -> UNVISITED);
		createUI(cellSize);
	}

	protected void addKeyboardAction(String key, Runnable code) {
		AbstractAction action = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				code.run();
			}
		};
		getCanvas().getInputMap().put(KeyStroke.getKeyStroke(key), "action_" + key);
		getCanvas().getActionMap().put("action_" + key, action);
	}

	private void createUI(int cellSize) {
		window = new JFrame();
		createAnimatedCanvas(cellSize);
		window.setContentPane(canvas);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.setTitle(getTitleText());
		window.setBackground(Color.BLACK);
		if (fullscreen) {
			window.setPreferredSize(Swing.getDisplaySize());
			window.setSize(Swing.getDisplaySize());
			window.setUndecorated(true);
			window.setAlwaysOnTop(true);
		}
		else {
			canvas.setPreferredSize(canvasSize);
			window.pack();
		}
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	private void createAnimatedCanvas(int cellSize) {
		canvas = new GridCanvas(grid, cellSize);
		canvas.setBackground(Color.BLACK);
		canvas.setCentered(true);
		canvas.clear();
		canvas.pushRenderer(createRenderer());
		canvas.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "exit");
		canvas.getActionMap().put("exit", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		canvasAnimation = new GridCanvasAnimation<>(canvas);
		grid.addGraphObserver(canvasAnimation);
	}

	protected ConfigurableGridRenderer createRenderer() {
		ConfigurableGridRenderer r;
		if (style == Style.WALL_PASSAGE) {
			r = new WallPassageGridRenderer();
		}
		else if (style == Style.PEARLS) {
			r = new PearlsGridRenderer();
		}
		else {
			throw new IllegalArgumentException();
		}
		r.fnCellSize = () -> canvas.getCellSize();
		r.fnCellBgColor = cell -> {
			switch (grid.get(cell)) {
			case VISITED:
				return Color.BLUE;
			case COMPLETED:
				return Color.WHITE;
			case UNVISITED:
				return r.getGridBgColor();
			default:
				return Color.BLACK;
			}
		};
		r.fnPassageWidth = (u, v) -> Math.max(1, canvas.getCellSize() / 4);
		return r;
	}

	private String getTitleText() {
		String pattern = "%s [%d cols %d rows %d cells @%d px]";
		return String.format(pattern, appName, grid.numCols(), grid.numRows(), grid.numVertices(),
				canvas.getCellSize());
	}

	public void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addEdge(int u, int v) {
		grid.set(u, COMPLETED);
		grid.addEdge(u, v);
		grid.set(v, COMPLETED);
	}

	public GridCanvas getCanvas() {
		return canvas;
	}

	public ObservableGridGraph<TraversalState, Integer> getGrid() {
		return grid;
	}

	public void setGrid(ObservableGridGraph<TraversalState, Integer> grid) {
		this.grid = grid;
		if (canvas != null) {
			canvas.setGrid(grid);
			canvas.drawGrid();
			if (canvasAnimation != null) {
				grid.addGraphObserver(canvasAnimation);
			}
		}
	}

	public Style getStyle() {
		return Style.WALL_PASSAGE;
	}

	public void setStyle(Style style) {
		if (this.style == style) {
			return;
		}
		this.style = style;
		if (canvas != null) {
			canvas.popRenderer();
			canvas.pushRenderer(createRenderer());
		}
	}

	public void setGridTopology(GridTopology topology) {
		int numCols = grid.numCols(), numRows = grid.numRows();
		setGrid(GridFactory.emptyObservableGrid(numCols, numRows, Grid4Topology.get(), UNVISITED, 0));
	}

	public void setCellSize(int cellSize) {
		setGrid(GridFactory.emptyObservableGrid(canvasSize.width / cellSize, canvasSize.height / cellSize,
				Grid4Topology.get(), UNVISITED, 0));
		canvas.setCellSize(cellSize, false);
		canvas.setGrid(grid);
		window.setTitle(getTitleText());
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
		if (window != null) {
			window.setTitle(getTitleText());
		}
	}

	public void setCanvasAnimation(boolean animated) {
		canvasAnimation.setEnabled(animated);
	}

	public void setCanvasAnimationDelay(int millis) {
		canvasAnimation.fnDelay = () -> millis;
	}

	public void floodFill() {
		floodFill(GridPosition.CENTER);
	}

	public void floodFill(GridPosition sourcePosition) {
		floodFill(getGrid().cell(sourcePosition));
	}

	public void floodFill(int source) {
		BFSAnimation.builder().canvas(canvas).distanceVisible(false).build().floodFill(source);
	}
}