package net.sevenscales.editor.api;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Interpreter extends SimplePanel {
	private SurfaceHandler surface;
	private TextArea textArea;

	public Interpreter(SurfaceHandler surface) {
		this.surface = surface;
		VerticalPanel panel = new VerticalPanel();
		this.textArea = new TextArea();
		panel.add(textArea);
		setWidget(panel);
	}
}
