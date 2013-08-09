package net.sevenscales.editor.api.impl;

import net.sevenscales.domain.utils.SLogger;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.TouchEvent;

public class TouchHelpers {
	private static final SLogger logger = SLogger.createLogger(TouchHelpers.class);

	private static class MouseMoveEventImpl extends MouseMoveEvent {
	}

	private static class MouseDownEventImpl extends MouseDownEvent {
	}
	
	private static class MouseUpEventImpl extends MouseUpEvent {
	}
	
	private static boolean supportsTouch;
	
	static {
		supportsTouch = _isSupportsTouch();
	}

	public static MouseDownEvent createMouseDownEvent(Touch touch) {
	  NativeEvent evt = Document.get().createMouseDownEvent(1, touch.getScreenX(), touch.getScreenY(), touch.getClientX(), touch.getClientY(), false, false, false, false, 0);
	  MouseDownEvent result = new MouseDownEventImpl();
	  result.setNativeEvent(evt);
	  return result;
	}
	
	public static MouseMoveEvent createMouseMoveEvent(Touch touch) {
	  NativeEvent evt = Document.get().createMouseMoveEvent(1, touch.getScreenX(), touch.getScreenY(), touch.getClientX(), touch.getClientY(), false, false, false, false, 0);
	  MouseMoveEventImpl result = new MouseMoveEventImpl();
	  result.setNativeEvent(evt);
	  return result;
	}

	public static MouseUpEvent createMouseUpEvent(TouchContext context) {
	  NativeEvent evt = Document.get().createMouseUpEvent(1, context.screenX, context.screenY, 
	  																											 context.clientX, context.clientY, 
	  																											 false, false, false, false, 0);
	  MouseUpEventImpl result = new MouseUpEventImpl();
	  result.setNativeEvent(evt);
	  return result;
	}

	public static <T extends TouchEvent> Touch firstTouch(T touch) {
		if (touch.getTouches().length() == 1) {
			// handle only one finger touches
			return (Touch) touch.getTouches().get(0);
		}
		return null;
	}
	
	public static void fillContext(Touch touch, TouchContext context) {
		context.clientX = touch.getClientX();
		context.clientY = touch.getClientY();
		context.screenX = touch.getScreenX();
		context.screenY = touch.getScreenY();
	}
	
	public static double distance(Touch first, Touch second) {
		int a = Math.abs(first.getClientX() - second.getClientX());
		int b = Math.abs(first.getClientY() - second.getClientY());
		
		return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
	}
	
	public static boolean isSupportsTouch() {
		return supportsTouch;
	}
	public native static boolean _isSupportsTouch()/*-{
		return 'ontouchstart' in $doc.documentElement;
	}-*/;


}
