package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;
import net.sevenscales.editor.gfx.domain.IGraphics;

public class GraphicsDoubleClickEvent implements GraphicsEventType<GraphicsDoubleClickHandler> {
  private static final Type<GraphicsDoubleClickHandler> TYPE = new Type<GraphicsDoubleClickHandler>(
      IGraphics.ON_DOUBLE_CLICK, new GraphicsDoubleClickEvent());

  public static Type<GraphicsDoubleClickHandler> getType() {
    return TYPE;
  }
}

