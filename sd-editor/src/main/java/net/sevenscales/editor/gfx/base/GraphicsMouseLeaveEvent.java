package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;
import net.sevenscales.editor.gfx.domain.IGraphics;

public class GraphicsMouseLeaveEvent implements GraphicsEventType<GraphicsMouseLeaveHandler> {
  private static final Type<GraphicsMouseLeaveHandler> TYPE = new Type<GraphicsMouseLeaveHandler>(
      IGraphics.ON_MOUSE_LEAVE, new GraphicsMouseLeaveEvent());

  public static Type<GraphicsMouseLeaveHandler> getType() {
    return TYPE;
  }
}

