package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.api.event.pointer.Events;
import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;

public class GraphicsPointerUpEvent implements GraphicsEventType<GraphicsPointerUpHandler> {
  private static final Type<GraphicsPointerUpHandler> TYPE = new Type<GraphicsPointerUpHandler>(
      Events.PointerUp.getNativeEventName(), new GraphicsPointerUpEvent());

  public static Type<GraphicsPointerUpHandler> getType() {
    return TYPE;
  }
}

