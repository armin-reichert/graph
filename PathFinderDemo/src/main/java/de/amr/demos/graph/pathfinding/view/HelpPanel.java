package de.amr.demos.graph.pathfinding.view;

import java.awt.Color;
import java.awt.SystemColor;
import java.io.IOException;

import javax.swing.JTextPane;
import javax.swing.border.LineBorder;
import javax.swing.event.HyperlinkEvent.EventType;

public class HelpPanel extends JTextPane {

	public HelpPanel() {
		setBackground(SystemColor.info);
		setBorder(new LineBorder(Color.LIGHT_GRAY));
		setEditable(false);
		setContentType("text/html");
		try {
			setPage(getClass().getResource("/help.html"));
			addHyperlinkListener(e -> {
				if (e.getEventType() == EventType.ACTIVATED) {
					try {
						Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + e.getURL());
					} catch (IOException x) {
						x.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			setText("COULD NOT READ HELP HTML FILE!");
			e.printStackTrace();
		}
	}
}