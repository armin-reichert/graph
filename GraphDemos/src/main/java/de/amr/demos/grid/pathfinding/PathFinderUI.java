package de.amr.demos.grid.pathfinding;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.amr.demos.grid.pathfinding.PathFinderDemoApp.PathFinderAlgorithm;
import de.amr.graph.grid.impl.Top4;
import de.amr.graph.grid.impl.Top8;
import net.miginfocom.swing.MigLayout;

public class PathFinderUI extends JFrame {

	private PathFinderDemoApp app;
	private JComboBox<PathFinderAlgorithm> comboAlgorithm;
	private JComboBox<String> comboTopology;

	public void setApp(PathFinderDemoApp app) {
		this.app = app;
		comboAlgorithm.setSelectedItem(app.getAlgorithm());
		comboTopology.setSelectedItem(app.getTopology() == Top4.get() ? "4 Neighbors" : "8 Neighbors");
	}

	public PathFinderUI() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Pathfinder Demo");

		JPanel settingsPanel = new JPanel();
		settingsPanel.setPreferredSize(new Dimension(300, 10));
		settingsPanel.setMinimumSize(new Dimension(300, 10));
		getContentPane().add(settingsPanel, BorderLayout.EAST);
		settingsPanel.setLayout(new MigLayout("", "[][grow]", "[][]"));

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
	}
}