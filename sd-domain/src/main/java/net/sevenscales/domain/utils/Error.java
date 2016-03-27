package net.sevenscales.domain.utils;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import java.util.logging.Level;
import com.google.gwt.core.shared.GWT;

public class Error {
	public static void reload(Exception e) {
		Error.reload("Exception: " + e);
	}

  public static void reload(String msg, Exception e) {
    Error.reload("Error msg: " + msg + "\nException: " + e);
  }

  public static void reload(String msg) {
    Error.reload("Error: " + msg);
  }

	public static void _reload(String msg) {
    // TODO in future report to server so problem can be fixed!
    Debug.log(msg);

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
}