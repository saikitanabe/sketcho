package net.sevenscales.editor.diagram;

import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

public interface KeyEventListener extends KeyDownHandler, KeyPressHandler, KeyUpHandler {
  public boolean onKeyEventDown(int keyCode, boolean shift, boolean ctrl);
  public boolean onKeyEventUp(int keyCode, boolean shift, boolean ctrl);
  public void onKeyDown(KeyDownEvent event);
  public void onKeyUp(KeyUpEvent event);
  public void onKeyPress(KeyPressEvent event);
}
