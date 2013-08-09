package net.sevenscales.appFrame.impl;

public class Debug {

	public static native void print(String string)/*-{
		$wnd.debugLog(string);
	}-*/;

}
