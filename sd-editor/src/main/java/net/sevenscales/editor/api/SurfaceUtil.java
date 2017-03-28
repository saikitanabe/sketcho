package net.sevenscales.editor.api;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;

public class SurfaceUtil {
	
	public static int eventGetElementOffsetX(Element element, Event event) {
    int x = DOM.eventGetClientX(event)
      - DOM.getAbsoluteLeft(element)
      + DOM.getElementPropertyInt(element, "scrollLeft")
      + Window.getScrollLeft();
		return x;
	}
	public static int eventGetElementOffsetY(Element element, Event event) {
    int y = DOM.eventGetClientY(event)
      - DOM.getAbsoluteTop(element)
      + DOM.getElementPropertyInt(element, "scrollTop")
      + Window.getScrollTop();
		return y;
	}

}
