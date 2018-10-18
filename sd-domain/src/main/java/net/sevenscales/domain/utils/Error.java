package net.sevenscales.domain.utils;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;

public class Error {

  public static native void error(String msg, Object obj)/*-{
    if (typeof $wnd.console != "undefined") $wnd.console.error(msg, obj);
  }-*/;

	public static void reload(Exception e) {
		Error._reload("Exception", e);
	}
	public static void reload(Throwable e) {
		Error._reload("Exception", e);
	}

  public static void reload(String msg, Exception e) {
    Error._reload("Error msg: " + msg, e);
  }

  public static void reload(String msg) {
    Error._reload("Error: " + msg, null);
  }

	private static void _reload(String msg, Throwable e) {
    // TODO in future report to server so problem can be fixed!
    Debug.log("Error msg: " + msg + "\nException: " + e);

    Error.report(e);

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
  
  private native static void report(Throwable e)/*-{
    if (typeof $wnd.__reportExeption === 'function') {
      $wnd.__reportExeption(e)
    }
  }-*/;
}