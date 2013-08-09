package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;
import net.sevenscales.editor.gfx.domain.IGraphics;

public class GraphicsTouchMoveEvent implements GraphicsEventType<GraphicsTouchMoveHandler> {
  private static final Type<GraphicsTouchMoveHandler> TYPE = new Type<GraphicsTouchMoveHandler>(
      IGraphics.ON_TOUCH_MOVE, new GraphicsTouchMoveEvent());

  public static Type<GraphicsTouchMoveHandler> getType() {
    return TYPE;
  }
}

