package net.sevenscales.editor.content.utils;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event.NativePreviewEvent;

public class KeyHelpers {

	public static boolean isSave(NativePreviewEvent event) {
		NativeEvent ne = event.getNativeEvent();
		if ((ne.getCtrlKey() || ne.getMetaKey()) && !ne.getShiftKey() && ne.getKeyCode() == 'S') {
			return true;
		}
		return false;
	}

}
