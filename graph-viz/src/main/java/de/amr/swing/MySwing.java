package de.amr.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;

/**
 * Some useful methods for Swing based UIs.
 * 
 * @author Armin Reichert
 */
public interface MySwing {

	public static final Action NULL_ACTION = action("", e -> {
	});

	public static Dimension getDisplaySize() {
		DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDisplayMode();
		return new Dimension(displayMode.getWidth(), displayMode.getHeight());
	}

	public static Icon icon(String path) {
		return new ImageIcon(MySwing.class.getResource(path));
	}

	public static Action action(String name, ActionListener listener) {
		return action(name, null, listener);
	}

	public static Action action(String name, Icon icon, ActionListener listener) {
		return new AbstractAction(name, icon) {

			@Override
			public void actionPerformed(ActionEvent e) {
				listener.actionPerformed(e);
			}
		};
	}

	public static void setEnabled(boolean b, Component... components) {
		Arrays.stream(components).forEach(comp -> comp.setEnabled(b));
	}

	public static void setEnabled(boolean b, Action... actions) {
		Arrays.stream(actions).forEach(action -> action.setEnabled(b));
	}

	public static void setWaitCursor(Component... components) {
		Arrays.stream(components).forEach(comp -> comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)));
	}

	public static void setNormalCursor(Component... components) {
		Arrays.stream(components).forEach(comp -> comp.setCursor(Cursor.getDefaultCursor()));
	}

	public static <T> T comboSelection(JComboBox<T> combo) {
		return combo.getItemAt(combo.getSelectedIndex());
	}

	public static void selectComboNoAction(JComboBox<?> combo, int index) {
		selectComboNoAction(combo, combo.getModel().getElementAt(index));
	}

	public static void selectComboNoAction(JComboBox<?> combo, Object selection) {
		Action action = combo.getAction();
		combo.setAction(NULL_ACTION);
		combo.setSelectedItem(selection);
		combo.setAction(action);
	}
}