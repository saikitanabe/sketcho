package net.sevenscales.editor.utils;

import com.google.gwt.core.client.JavaScriptObject;


public class WebStorage {
	public static native void listen(String key, IWebStorageListener listener)/*-{
		$wnd.webStorage.listen(key, function(e) {
			listener.@net.sevenscales.editor.utils.IWebStorageListener::onStorageEvent(Ljava/lang/String;Ljava/lang/String;)(e.key, e.newValue)
		})
	}-*/;

	public static native void setJson(String key, JavaScriptObject json)/*-{
		if (typeof $wnd.webStorage !== 'undefined') {
			$wnd.webStorage.set(key, JSON.stringify(json));
		}
	}-*/;

	public static native void setString(String key, String value)/*-{
		if (typeof $wnd.webStorage !== 'undefined') {
			$wnd.webStorage.set(key, value);
		}
	}-*/;

	public static native String get(String key)/*-{
		if (typeof $wnd.webStorage !== 'undefined') {
			var result = $wnd.webStorage.get(key);
			if (result) {
				return result
			} else {
				return ""
			}
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