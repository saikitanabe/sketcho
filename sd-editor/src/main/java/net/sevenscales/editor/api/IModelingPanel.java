package net.sevenscales.editor.api;

import com.google.gwt.user.client.ui.Widget;
import net.sevenscales.editor.diagram.KeyEventListener;

public interface IModelingPanel {
	ISurfaceHandler getSurface();
	ToolFrame getToolFrame();
	void reset();	
	void addKeyEventHandler(KeyEventListener keyEventHandler);
	Widget getWidget();
	void setSize(Integer width, Integer height);
}