package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.api.event.pointer.Events;
import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;

public class GraphicsPointerLeaveEvent implements GraphicsEventType<GraphicsPointerLeaveHandler> {
  private static final Type<GraphicsPointerLeaveHandler> TYPE = new Type<GraphicsPointerLeaveHandler>(
      Events.PointerLeave.getNativeEventName(), new GraphicsPointerLeaveEvent());

  public static Type<GraphicsPointerLeaveHandler> getType() {
    return TYPE;
  }
}

