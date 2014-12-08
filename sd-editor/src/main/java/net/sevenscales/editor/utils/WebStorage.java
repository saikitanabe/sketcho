package net.sevenscales.editor.utils;


public class WebStorage {
	public static native void set(String key, String jsonStr)/*-{
		if (typeof $wnd.webStorage !== 'undefined') {
			$wnd.webStorage.set(key, jsonStr);
		}
	}-*/;

	public static native String get(String key)/*-{
		if (typeof $wnd.webStorage !== 'undefined') {
			var result = $wnd.webStorage.get(key);
			return result
		}
		return ""
	}-*/;

	public static native boolean remove(String key)/*-{
		if (typeof $wnd.webStorage !== 'undefined') {
			return $wnd.webStorage.remove(key)
		}
		return false
	}-*/;

	public static native boolean isEmpty(String key)/*-{
		if (typeof $wnd.webStorage !== 'undefined') {
			return $wnd.webStorage.isEmpty(key)
		}
		return true
	}-*/;

}