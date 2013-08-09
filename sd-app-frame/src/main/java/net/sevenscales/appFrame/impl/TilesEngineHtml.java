package net.sevenscales.appFrame.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;


public class TilesEngineHtml {
	private Map tiles = new HashMap();
	private Widget container;

	public TilesEngineHtml() {
	}

	public void setContainer(Widget container) {
		this.container = container;
	}

	public void setTile(String id, Widget widget) {
		Panel p = (Panel) tiles.get(id);
		p.clear();
		if (widget != null) {
			p.add(widget);
		}
	}

	public void setTile(String tileId, Widget widget, String styleSheet) {
		setTile(tileId, widget);
		setStyleSheet(tileId, styleSheet);
	}

	public Widget createTile(String id, Panel panel) {
		tiles.put(id, panel);
		return panel;
	}
/*
	public void setBackgroundColor(String id, String color) {
	    Element td = ((Widget) tiles.get(id)).getElement();
	    DOM.setStyleAttribute(td, "backgroundColor", color);
	    System.out.println("setting bgcolor " + td);
	}
	*/

	public void setStyleSheet(String id, String styleSheet) {
		((Widget) tiles.get(id)).setStyleName(styleSheet);
	}
}
