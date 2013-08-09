package net.sevenscales.editor.gfx.base;

import net.sevenscales.editor.gfx.base.GraphicsEventBase.Type;
import net.sevenscales.editor.gfx.domain.IGraphics;

public class GraphicsTouchEndEvent implements GraphicsEventType<GraphicsTouchEndHandler> {
  private static final Type<GraphicsTouchEndHandler> TYPE = new Type<GraphicsTouchEndHandler>(
      IGraphics.ON_TOUCH_END, new GraphicsTouchEndEvent());

  public static Type<GraphicsTouchEndHandler> getType() {
    return TYPE;
  }
}

