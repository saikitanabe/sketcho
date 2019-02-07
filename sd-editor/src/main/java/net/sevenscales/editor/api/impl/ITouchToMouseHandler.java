package net.sevenscales.editor.api.impl;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;

public interface ITouchToMouseHandler {
  void onTouchToMouseMove(MouseMoveEvent event);
  void onTouchToMouseUp(MouseUpEvent e);
  void onTouchToMouseDown(MouseDownEvent e);
}
