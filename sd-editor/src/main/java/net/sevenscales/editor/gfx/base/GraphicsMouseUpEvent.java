package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;
import net.sevenscales.editor.gfx.domain.IGraphics;

public class GraphicsMouseUpEvent implements GraphicsEventType<GraphicsMouseUpHandler> {
  private static final Type<GraphicsMouseUpHandler> TYPE = new Type<GraphicsMouseUpHandler>(
      IGraphics.ON_MOUSE_UP, new GraphicsMouseUpEvent());

  public static Type<GraphicsMouseUpHandler> getType() {
    return TYPE;
  }
}

