package de.amr.demos.graph.pathfinding.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

public class MainWindow extends JFrame {

	public MainWindow() {
		getContentPane().setBackground(Color.WHITE);
		setTitle("Path Finder Demo");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setView(MainView view) {
		getContentPane().removeAll();
		getContentPane().add(view, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}
}