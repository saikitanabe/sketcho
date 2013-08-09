package net.sevenscales.appFrame.impl;

import java.util.List;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public interface ITilesEngine {
	public void setTile(String id, Widget widget);
	public void setTile(String tileId, Widget widget, String styleSheet);
	public Panel getTile(String id);
	public Widget createTile(String id, Panel panel);
  public void setContainer(Widget root);
  
  // Areas that are can be cleared on each view switch
  public void setDynamicAreas(List<String> dynamicAreas);
  public void clearDynamicAreas();
  public void clear(String tileId);
}
