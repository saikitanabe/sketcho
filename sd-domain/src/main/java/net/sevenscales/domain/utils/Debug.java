package net.sevenscales.domain.utils;

// import com.google.gwt.core.client.JavaScriptObject;
// import com.google.gwt.user.client.Element;
// import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;

public class Debug {

  // private static Element dbox;
  private static SimplePanel dbox;

	public static native void print(String string)/*-{
		$wnd.debugLog(string);
	}-*/;
	
	public static void println(String text) {
	  System.out.println(text);
	}
	
  public static native void log(String msg)/*-{
  	if (typeof $wnd.console != "undefined") $wnd.console.log("Sketchboard.Me: " + msg);
  }-*/;

  public static native void log(String msg, Object obj)/*-{
  	if (typeof $wnd.console != "undefined") $wnd.console.log(msg, obj);
  }-*/;

  public static native void log(String msg, Object... objs)/*-{
    if (typeof $wnd.console != "undefined") $wnd.console.log(msg, objs);
  }-*/;

  public static native void logString(String msg, Object... objs)/*-{
  	if (typeof $wnd.console != "undefined") $wnd.console.log(msg, objs.map(function(o) {
      return o + ""
    }));
  }-*/;

  public static void debugBox(double left, double top) {
    if (Debug.dbox == null) {
      Debug.dbox = Debug.createDBox();
    }

    Style s = Debug.dbox.getElement().getStyle();
    s.setLeft(left, Style.Unit.PX);
    s.setTop(top, Style.Unit.PX);
  }

  private static SimplePanel createDBox() {
    SimplePanel result = new SimplePanel();
    Style s = result.getElement().getStyle();
    s.setPosition(Position.FIXED);    
    s.setBorderColor("#FF00FF");
    s.setBorderStyle(Style.BorderStyle.SOLID);
    s.setBorderWidth(2.0, Style.Unit.PX);
    s.setWidth(10, Style.Unit.PX);
    s.setHeight(10, Style.Unit.PX);
    RootPanel.get().add(result);
    return result;
  }
}
