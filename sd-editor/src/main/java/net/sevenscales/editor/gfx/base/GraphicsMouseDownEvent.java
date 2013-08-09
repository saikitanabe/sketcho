package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;
import net.sevenscales.editor.gfx.domain.IGraphics;

public class GraphicsMouseDownEvent implements GraphicsEventType<GraphicsMouseDownHandler> {
  private static final Type<GraphicsMouseDownHandler> TYPE = new Type<GraphicsMouseDownHandler>(
      IGraphics.ON_MOUSE_DOWN, new GraphicsMouseDownEvent());

  public static Type<GraphicsMouseDownHandler> getType() {
    return TYPE;
  }
}

