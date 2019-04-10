package de.amr.graph.grid.ui.test;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import de.amr.graph.grid.ui.rendering.GridCanvas;
import javax.swing.border.LineBorder;

public class GridCanvasTest extends JFrame {

	public GridCanvasTest() {
		setTitle("Grid Canvas Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GridCanvas gridCanvas = new GridCanvas();
		gridCanvas.setBorder(new LineBorder(Color.MAGENTA, 5));
		gridCanvas.setBackground(Color.BLUE);
		getContentPane().add(gridCanvas, BorderLayout.CENTER);
	}

}
