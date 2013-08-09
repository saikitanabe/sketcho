package net.sevenscales.appFrame.impl;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class TilesEngineComponent implements ITilesEngine {
	private Map<String, Panel> tiles = new HashMap<String, Panel>();
	private Widget container;
  private List<String> dynamicAreas;

	public TilesEngineComponent() {
	}

	public void setContainer(Widget container) {
		this.container = container;
	}

	public void setTile(String id, Widget widget) {
		Panel p = (Panel) tiles.get(id);
		if (p != null && p.isAttached() && widget != null) {
	    p.clear();
			p.add(widget);
			p.setVisible(true);
		}
	}

	public void setTile(String tileId, Widget widget, String styleSheet) {
		setTile(tileId, widget);
		setStyleSheet(tileId, styleSheet);
	}
	
	public Panel getTile(String id) {
	  return tiles.get(id);
	}

	public Widget createTile(String id, Panel panel) {
		tiles.put(id, panel);
		panel.setVisible(false);
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
	
	public void setDynamicAreas(List<String> dynamicAreas) {
	  this.dynamicAreas = dynamicAreas;
	}
	
	public void clearDynamicAreas() {
	  for (String id : dynamicAreas) {
	    clear(id);
	  }
	}
	
  public void clear(String tileId) {
    Panel p = (Panel) tiles.get(tileId);
    if (p != null) {
      p.clear();
      p.setVisible(false);
    }
  }

}
