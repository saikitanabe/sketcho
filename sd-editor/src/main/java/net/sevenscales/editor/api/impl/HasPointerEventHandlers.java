package net.sevenscales.editor.api.impl;

import com.google.gwt.event.shared.HandlerRegistration;

import net.sevenscales.editor.api.event.pointer.PointerDownHandler;

public interface HasPointerEventHandlers {
  HandlerRegistration addPointerDownEventHandler(PointerDownHandler handler);
}