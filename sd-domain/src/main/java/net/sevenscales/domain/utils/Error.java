package net.sevenscales.domain.utils;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;

public class Error {

  public static native void error(String msg, Object obj)/*-{
    if (typeof $wnd.console != "undefined") $wnd.console.error(msg, obj);
  }-*/;

	public static void reload(Exception e) {
		Error._reload("Exception: " + e, e);
	}
	public static void reload(Throwable e) {
		Error._reload("Exception: " + e, e);
	}

  public static void reload(String msg, Exception e) {
    Error._reload(msg + " Exception: " + e, e);
  }

  public static void reload(String msg) {
    Error._reload(msg, null);
  }

	private static void _reload(String msg, Throwable e) {
    // TODO in future report to server so problem can be fixed!

    String stack = "";

    try {
      // make sure that doesn't break
      for (StackTraceElement ste : e.getStackTrace()) {
        stack += ste.getMethodName() + ": " + ste.getFileName() + " " + ste.getLineNumber() + "\n";
      }
    } catch(Exception ex) {
      // make sure execution continues
    }

    Debug.log("Error msg: " + msg + "\nException: " + e, stack);

    try {
      Error.report(e, msg, stack);
    } catch (Exception ex) {
      // make sure execution continues and Sketchboard client is not in invalid state
    }

    if (LogConfiguration.loggingIsEnabled()) {
      GWT.debugger();
      if (Window.confirm(msg)) {
        // possible to reload page as in production
        Window.Location.reload();  
      }
    } else {
      // production reloads page
      Window.Location.reload();
    }

  }
  
  private native static void report(Throwable e, String msg, String stack)/*-{
    if (typeof $wnd.__reportException === 'function') {
      $wnd.__reportException(e, msg, stack)
    }
  }-*/;
}