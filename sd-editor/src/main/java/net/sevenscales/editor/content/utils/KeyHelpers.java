package net.sevenscales.editor.content.utils;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import net.sevenscales.domain.utils.SLogger;


public class KeyHelpers {
	private static SLogger logger = SLogger.createLogger(KeyHelpers.class);

	public static boolean isSave(NativePreviewEvent event) {
		NativeEvent ne = event.getNativeEvent();
		boolean ckey = ne.getCtrlKey();
		boolean mkey = ne.getMetaKey();

		// only control key or command key is down
		if ( (!(ckey && mkey) && (ckey || mkey)) && !ne.getAltKey() && !ne.getShiftKey() && ne.getKeyCode() == 'S') {
			return true;
		}
		return false;
	}

}
