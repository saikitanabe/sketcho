package net.sevenscales.editor.uicomponents;

import net.sevenscales.editor.uicomponents.impl.BrowserStyleImpl;

import com.google.gwt.core.client.GWT;

public class BrowserStyle {
	public static final int SI_KEY_CTRL = 5;
  private static BrowserStyleImpl impl;
	
	static {
		impl = (BrowserStyleImpl) GWT.create(BrowserStyleImpl.class);
	}
	
	public static String floatStyle() {
		return impl.floatStyle();
	}
}
