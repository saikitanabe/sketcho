package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;
import net.sevenscales.editor.gfx.domain.IGraphics;

public class GraphicsTouchStartEvent implements GraphicsEventType<GraphicsTouchStartHandler> {
  private static final Type<GraphicsTouchStartHandler> TYPE = new Type<GraphicsTouchStartHandler>(
      IGraphics.ON_TOUCH_START, new GraphicsTouchStartEvent());

  public static Type<GraphicsTouchStartHandler> getType() {
    return TYPE;
  }
}

