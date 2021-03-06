package net.sevenscales.editor.gfx.base;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;

import net.sevenscales.editor.api.SurfaceUtil;

public abstract class GraphicsEvent extends Event {
  
  public static boolean type;
  
  protected GraphicsEvent() {
  }

  public final int getElementOffsetX(Element element) {
    if (!type) { 
      return SurfaceUtil.eventGetElementOffsetX(element, this);
    }
    return getClientX();
  }

  public final int getElementOffsetY(Element element) {
    if (!type) { 
      return SurfaceUtil.eventGetElementOffsetY(element, this);
    }
    return getClientY();
  }
  
}
