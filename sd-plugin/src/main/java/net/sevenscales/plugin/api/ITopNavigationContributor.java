package net.sevenscales.plugin.api;

import com.google.gwt.user.client.ui.Widget;

import net.sevenscales.appFrame.api.IContributor;

public interface ITopNavigationContributor extends IContributor {
  
  public interface ITopNaviPanel {
    public void addItem(Widget widget);
    public void insertItem(Widget widget, int index);
  }

  public void addToRight(ITopNaviPanel right);

}
