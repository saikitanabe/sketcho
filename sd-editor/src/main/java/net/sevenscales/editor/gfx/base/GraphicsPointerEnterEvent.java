package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.api.event.pointer.Events;
import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;

public class GraphicsPointerEnterEvent implements GraphicsEventType<GraphicsPointerEnterHandler> {
  private static final Type<GraphicsPointerEnterHandler> TYPE = new Type<GraphicsPointerEnterHandler>(
      Events.PointerEnter.getNativeEventName(), new GraphicsPointerEnterEvent());

  public static Type<GraphicsPointerEnterHandler> getType() {
    return TYPE;
  }
}

