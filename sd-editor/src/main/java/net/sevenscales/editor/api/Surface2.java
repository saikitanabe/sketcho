package net.sevenscales.editor.api;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;

import net.sevenscales.editor.diagram.ClickDiagramHandler;

public class Surface2 extends SimplePanel {
	private JavaScriptObject surface;
	
	public Surface2(int width, int height, boolean editable) {
		this.surface = createSurface(getElement(), width, height); 
	}
	
	private native JavaScriptObject createSurface(Element element, int width, int height)/*-{
		return $wnd.dojox.gfx.createSurface(element, width, height);
	}-*/;

	public void registerClickListener(ClickDiagramHandler listener) {
//		clickListenerCollection.add(listener);
	}
	
}
	
	