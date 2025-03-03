package net.sevenscales.domain.utils;

// import com.google.gwt.core.client.JavaScriptObject;
// import com.google.gwt.user.client.Element;
// import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.logging.Level;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.logging.client.LogConfiguration;

// net.sevenscales.domain.utils.Debug.log()
public class Debug {

  // private static Element dbox;
  private static SimplePanel dbox;

	public static native void print(String string)/*-{
		$wnd.debugLog(string);
	}-*/;
	
	public static void println(String text) {
	  System.out.println(text);
  }
  
  public static void callstack(String msg) {
    if (LogConfiguration.loggingIsEnabled(Level.FINER)) {
      try {
        if (true) {
          throw new RuntimeException("test");
        }
      } catch (Exception err) {
        String stack = "";
        for (StackTraceElement ste : err.getStackTrace()) {
          stack += ste.getMethodName() + ": " + ste.getFileName() + " " + ste.getLineNumber() + "\n";
        }
    
        Debug.log(msg + "... stack:\n" + stack);
      }
    }
  }

  public static native void debugConsole(String name, Object... args)/*-{
  	if (typeof $wnd.consoleDebug) {
      $wnd.consoleDebugGwt(name, args)
    }
  }-*/;

  
  public static void log(String msg) {
    if (LogConfiguration.loggingIsEnabled(Level.FINER)) {
      _log(msg);
    }
  }
  private static native void _log(String msg)/*-{
  	if (typeof $wnd.console != "undefined") $wnd.console.log("Sketchboard: " + msg);
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

  public static native final void error(String msg, String error)/*-{
    if (typeof $wnd.console != "undefined") $wnd.console.error(msg + ": " + error)
  }-*/;

  public static native final void error(String msg, Exception error)/*-{
    if (typeof $wnd.console != "undefined") $wnd.console.error(msg + ": " + error)
  }-*/;

  public static class Profile {
    String title;
    Long time;

    Profile(String title, Long time) {
      this.title = title;
      this.time = time;
    }

    public void stopAndLog() {
      if (LogConfiguration.loggingIsEnabled(Level.FINER)) {
        Long diff = System.currentTimeMillis() - this.time;
        Debug.log(this.title + " took: " + diff.toString() + " milliseconds");
      }
    }
  }

  public static Profile startProfile(String title) {
    return new Profile(title, System.currentTimeMillis());
  }

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
