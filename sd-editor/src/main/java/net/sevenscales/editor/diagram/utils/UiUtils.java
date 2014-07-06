package net.sevenscales.editor.diagram.utils;

public class UiUtils {
	public static native String getUserAgent() /*-{
		return navigator.userAgent.toLowerCase();
	}-*/;
	
	public static boolean isIE() {
		if (getUserAgent().contains("msie") || getUserAgent().contains("trident")) {
	    return true;
    }
    return false;
	}

  public native static boolean isSafari()/*-{
    var n = $wnd.navigator,
        dav = n.appVersion;
    var index = Math.max(dav.indexOf('WebKit'), dav.indexOf('Safari'), 0);
    return index > 0 ? true : false;
  }-*/;
  
  public native static boolean isChrome()/*-{
  	return $wnd.navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
  }-*/;
  
  public native static boolean isFirefox()/*-{
		return $wnd.navigator.userAgent.toLowerCase().indexOf('firefox') > -1;
	}-*/;

  public native static boolean isOpera()/*-{
		return $wnd.navigator.userAgent.toLowerCase().indexOf('opera') > -1;
	}-*/;
  
}

