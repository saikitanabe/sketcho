package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;
import net.sevenscales.editor.gfx.domain.IGraphics;

public class GraphicsMouseOverEvent implements GraphicsEventType<GraphicsMouseEnterHandler> {
  private static final Type<GraphicsMouseEnterHandler> TYPE = new Type<GraphicsMouseEnterHandler>(
      IGraphics.ON_MOUSE_ENTER, new GraphicsMouseOverEvent());

  public static Type<GraphicsMouseEnterHandler> getType() {
    return TYPE;
  }
}

