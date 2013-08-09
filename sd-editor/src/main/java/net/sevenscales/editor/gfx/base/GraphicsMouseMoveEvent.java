package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;
import net.sevenscales.editor.gfx.domain.IGraphics;

public class GraphicsMouseMoveEvent implements GraphicsEventType<GraphicsMouseMoveHandler> {
  private static final Type<GraphicsMouseMoveHandler> TYPE = new Type<GraphicsMouseMoveHandler>(
      IGraphics.ON_MOUSE_MOVE, new GraphicsMouseMoveEvent());

  public static Type<GraphicsMouseMoveHandler> getType() {
    return TYPE;
  }
}

