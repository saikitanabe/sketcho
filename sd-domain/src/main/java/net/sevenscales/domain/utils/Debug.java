package net.sevenscales.domain.utils;

// import com.google.gwt.core.client.JavaScriptObject;

public class Debug {

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

}
