package net.sevenscales.editor.content.utils;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;

public class ContentEventUtils {

	public static void hanleKeyEvent(KeyDownEvent event) {
		handleKeyEvent(event.getNativeKeyCode(), event);
	}
	public static void hanleKeyEvent(KeyPressEvent event) {
		handleKeyEvent(event.getNativeEvent().getKeyCode(), event);
	}
	public static void hanleKeyEvent(KeyUpEvent event) {
		handleKeyEvent(event.getNativeKeyCode(), event);
	}
	
	private static void handleKeyEvent(int nativeKeyCode, KeyEvent event) {
//		System.out.println("handling key event");
		if (nativeKeyCode != 0 && disableEvent(nativeKeyCode, event)) {
			System.out.println("Disabling event: " + nativeKeyCode + " " + event.getAssociatedType());
			// disable Confluence default keys when having focus on
			// sketcho diagram area; otherwise will move somewhere else
			event.stopPropagation();
			event.preventDefault();
		}
	}

	private static boolean disableEvent(int nativeKeyCode, KeyEvent event) {
//		System.out.println("event.getNativeKeyCode(): " + event.getNativeKeyCode() + " " + (int) 'r' + " " + (int) 'R');
//		System.out.println("ctrl: " + isControlKeyDown(event) + " shift: " + event.isShiftKeyDown() + " m:" + event.isMetaKeyDown());
		switch (nativeKeyCode) {
		case 'r':
		case 'R':
			if (isControlKeyDown(nativeKeyCode, event)) {
				return false;
			}
		case 'f':
		case 'F':
			if (isControlKeyDown(nativeKeyCode, event) && event.isShiftKeyDown()) {
				return false;
			}
		default:
			return true;
		}
	}

	public static boolean isControlKeyDown(int nativeKeyCode, KeyEvent event) {
		if (nativeKeyCode == 224 || event.isMetaKeyDown()) {
			return true;
		}
		return false;
	}
	
	public static void disableNavigationAway(KeyDownEvent event) {
		// disable back event => that will happen too easily
		switch (event.getNativeKeyCode()) {
		case KeyCodes.KEY_BACKSPACE:
			event.preventDefault();
			event.stopPropagation();
			System.out.println("disabled backspace");
			break;
		}
	}

}
