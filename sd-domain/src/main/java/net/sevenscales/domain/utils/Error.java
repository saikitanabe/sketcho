package net.sevenscales.domain.utils;

import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Window;
import java.util.logging.Level;

public class Error {
	public static void reload(Exception e) {
		Error.reload(e.getMessage());
	}

	public static void reload(String msg) {
    // TODO in future report to server so problem can be fixed!
    Debug.log("Error", msg);

    if (LogConfiguration.loggingIsEnabled(Level.FINEST)) {
      Window.alert(msg);
    }

    Window.Location.reload();
	}
}