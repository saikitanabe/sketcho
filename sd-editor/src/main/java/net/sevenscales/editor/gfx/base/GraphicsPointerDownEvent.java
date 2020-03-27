package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.api.event.pointer.Events;
import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;

public class GraphicsPointerDownEvent implements GraphicsEventType<GraphicsPointerDownHandler> {
  private static final Type<GraphicsPointerDownHandler> TYPE = new Type<GraphicsPointerDownHandler>(
      Events.PointerDown.getNativeEventName(), new GraphicsPointerDownEvent());

  public static Type<GraphicsPointerDownHandler> getType() {
    return TYPE;
  }
}

