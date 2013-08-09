package net.sevenscales.editor.gfx.domain;

import com.google.gwt.user.client.ui.UIObject;

public interface ISurface extends IContainer, IGraphics {
  public void init(UIObject uiObject, ILoadObserver loadObserver);
  public void load();
  public boolean isLoaded();
  public void setKeyEventHandler(IKeyEventHandler handler);
  public void setSize(int width, int height);
  public void setAttribute(String name, String value);
  public void setBackground(String color);
  public void suspendRedraw();
  public void unsuspendRedrawAll();
	public void moveToBack();
}