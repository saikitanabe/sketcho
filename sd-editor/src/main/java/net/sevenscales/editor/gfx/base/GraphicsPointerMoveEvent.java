package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.api.event.pointer.Events;
import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;

public class GraphicsPointerMoveEvent implements GraphicsEventType<GraphicsPointerMoveHandler> {
  private static final Type<GraphicsPointerMoveHandler> TYPE = new Type<GraphicsPointerMoveHandler>(
      Events.PointerMove.getNativeEventName(), new GraphicsPointerMoveEvent());

  public static Type<GraphicsPointerMoveHandler> getType() {
    return TYPE;
  }
}

