package de.amr.swing;

import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

/**
 * Builder for menus.
 * 
 * @author Armin Reichert
 */
public class MenuBuilder {

	public static MenuBuilder beginMenu() {
		return new MenuBuilder();
	}

	@SuppressWarnings("unchecked")
	private static <T> Optional<T> getProperty(JComponent comp, String key) {
		T value = (T) comp.getClientProperty(key);
		return Optional.ofNullable(value);
	}

	public static void updateMenuSelection(JMenu menu) {
		for (int i = 0; i < menu.getItemCount(); ++i) {
			JMenuItem item = menu.getItem(i);
			if (item instanceof JCheckBoxMenuItem checkBox) {
				Optional<Supplier<Boolean>> selection = getProperty(checkBox, "selection");
				if (selection.isPresent()) {
					checkBox.setSelected(selection.get().get());
				}
			} else if (item instanceof JRadioButtonMenuItem radioButton) {
				Optional<Supplier<?>> selection = getProperty(radioButton, "selection");
				Optional<?> selectionValue = getProperty(radioButton, "selectionValue");
				if (selection.isPresent() && selectionValue.isPresent() && selection.get().get().equals(selectionValue.get())) {
					radioButton.setSelected(true);
				}
			}
		}
	}

	// Menu button builder

	public class ButtonBuilder {

		private Action action;
		private String text;

		public ButtonBuilder action(Action buttonAction) {
			action = buttonAction;
			return this;
		}

		public ButtonBuilder text(String buttonText) {
			text = buttonText;
			return this;
		}

		public MenuBuilder endButton() {
			Objects.requireNonNull(action);
			JMenuItem item = new JMenuItem(action);
			if (text != null) {
				item.setText(text);
			}
			menu.add(item);
			return MenuBuilder.this;
		}
	}

	// CheckBox builder

	public class CheckBoxBuilder {

		private Consumer<Boolean> onToggle;
		private Supplier<Boolean> selection;
		private String text;

		public CheckBoxBuilder onToggle(Consumer<Boolean> onToggleHandler) {
			onToggle = onToggleHandler;
			return this;
		}

		public CheckBoxBuilder selection(Supplier<Boolean> selectionHandler) {
			selection = selectionHandler;
			return this;
		}

		public CheckBoxBuilder text(String checkBoxText) {
			text = checkBoxText;
			return this;
		}

		public MenuBuilder endCheckBox() {
			JCheckBoxMenuItem checkBox = new JCheckBoxMenuItem();
			if (onToggle != null) {
				checkBox.addItemListener(change -> onToggle.accept(change.getStateChange() == ItemEvent.SELECTED));
			}
			if (selection != null) {
				checkBox.putClientProperty("selection", selection);
			}
			if (text != null) {
				checkBox.setText(text);
			}
			menu.add(checkBox);
			return MenuBuilder.this;
		}
	}

	// Radio button group builder

	public class RadioButtonGroupBuilder<T> {

		private final ButtonGroup radio;
		private Supplier<T> selection;
		private Consumer<T> onSelect;

		public class RadioButtonBuilder {

			private T selectionValue;
			private String text;

			public RadioButtonBuilder selectionValue(T radionButtonSelectionValue) {
				selectionValue = radionButtonSelectionValue;
				return this;
			}

			public RadioButtonBuilder text(String radioButtonText) {
				text = radioButtonText;
				return this;
			}

			public RadioButtonGroupBuilder<T> endRadioButton() {
				Objects.requireNonNull(selectionValue, "selection value is required");
				Objects.requireNonNull(selection, "selection function is required");
				JRadioButtonMenuItem radioButton = new JRadioButtonMenuItem();
				radioButton.putClientProperty("selectionValue", selectionValue);
				radioButton.putClientProperty("selection", selection);
				if (text != null) {
					radioButton.setText(text);
				}
				radioButton.addItemListener(e -> {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						onSelect.accept(selectionValue);
					}
				});
				radioButton.setSelected(selection.get().equals(selectionValue));
				radio.add(radioButton);
				menu.add(radioButton);
				return RadioButtonGroupBuilder.this;
			}
		}

		public RadioButtonGroupBuilder(Class<T> selectionType) {
			radio = new ButtonGroup();
		}

		public RadioButtonGroupBuilder<T> selection(Supplier<T> selectionSupplier) {
			selection = selectionSupplier;
			return this;
		}

		public RadioButtonGroupBuilder<T> onSelect(Consumer<T> selectionHandler) {
			onSelect = selectionHandler;
			return this;
		}

		public RadioButtonBuilder radioButton() {
			return new RadioButtonBuilder();
		}

		public MenuBuilder endRadioButtonGroup() {
			return MenuBuilder.this;
		}
	}

	// Menu builder

	private final JMenu menu;

	private MenuBuilder() {
		menu = new JMenu();
	}

	public JMenu endMenu() {
		return menu;
	}

	public MenuBuilder property(String key, Object value) {
		menu.putClientProperty(key, value);
		return this;
	}

	public MenuBuilder title(String text) {
		menu.setText(text);
		return this;
	}

	public MenuBuilder action(Action action) {
		JMenuItem item = new JMenuItem(action);
		menu.add(item);
		return this;
	}

	public MenuBuilder caption(String text) {
		JMenuItem caption = new JMenuItem(text);
		caption.setEnabled(false);
		menu.add(caption);
		return this;
	}

	public MenuBuilder separator() {
		menu.addSeparator();
		return this;
	}

	public ButtonBuilder button() {
		return new ButtonBuilder();
	}

	public CheckBoxBuilder checkBox() {
		return new CheckBoxBuilder();
	}

	public <T> RadioButtonGroupBuilder<T> radioButtonGroup(Class<T> selectionType) {
		return new RadioButtonGroupBuilder<>(selectionType);
	}

	public MenuBuilder items(Stream<JMenuItem> items) {
		items.forEach(menu::add);
		return this;
	}

	public MenuBuilder items(JMenuItem... items) {
		return items(Arrays.stream(items));
	}

	public MenuBuilder menu(JMenu subMenu) {
		Objects.requireNonNull(subMenu);
		menu.add(subMenu);
		return this;
	}
}