package net.sevenscales.editor.utils;

public class GoogleAnalyticsHelper {
	public static native void trackEvent(String category, String action)/*-{
		// check for confluence
		if (typeof $wnd.analyticsTrackEvent === "function")	$wnd.analyticsTrackEvent(category, action)
	}-*/;

	public static native void trackEvent(String category, String action, String label)/*-{
		// check for confluence
		if (typeof $wnd.analyticsTrackEvent === "function")	$wnd.analyticsTrackEvent(category, action, label)
	}-*/;

}