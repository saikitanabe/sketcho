package net.sevenscales.editor.diagram.utils;

public class UiUtils {
//   var firefox = navigator !== 'undefined' ? navigator.userAgent.toLowerCase().indexOf('firefox') > -1 : false
// var ie11 = navigator !== 'undefined' ? navigator.userAgent.toLowerCase().indexOf('trident') > -1 : false
// var msie = navigator !== 'undefined' ? navigator.userAgent.toLowerCase().indexOf('msie') > -1 : false
  private static Boolean ie = null;
  // private static Boolean msie = null;
  private static Boolean firefox = null;
  private static Boolean chrome = null;
  private static Boolean mobile = null;
  private static Boolean safari = null;
  private static Boolean opera = null;

	public static native String getUserAgent() /*-{
		return navigator.userAgent.toLowerCase();
	}-*/;
	
	public static boolean isIE() {
    if (ie == null) {
      if (getUserAgent().contains("msie") || getUserAgent().contains("trident")) {
        ie = true;
      } else {
        ie = false;
      }
    }
    return ie.booleanValue();
	}

  public static boolean isSafari() {
    if (safari == null) {
      safari = _isSafari();
    }
    return safari.booleanValue();
  }
  public native static boolean _isSafari()/*-{
    var n = $wnd.navigator,
        dav = n.appVersion;
    var index = Math.max(dav.indexOf('WebKit'), dav.indexOf('Safari'), 0);
    return index > 0 ? true : false;
  }-*/;

  public static boolean isMobile() {
    if (mobile == null) {
      mobile = _Mobile();
    }
    return mobile.booleanValue();
  }
  public native static boolean _Mobile()/*-{
    var ua = $wnd.navigator.userAgent
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini|Mobile|mobile/i.test(ua);
  }-*/;
  
  public static boolean isChrome() {
    if (chrome == null) {
      chrome = _isChrome();
    }
    return chrome.booleanValue();
  }
  public native static boolean _isChrome()/*-{
  	return $wnd.navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
  }-*/;
  
  public static boolean isFirefox() {
    if (firefox == null) {
      firefox = _isFirefox();
    }
    return firefox.booleanValue();
  }
  public native static boolean _isFirefox()/*-{
		return $wnd.navigator.userAgent.toLowerCase().indexOf('firefox') > -1;
	}-*/;

  public static boolean isOpera() {
    if (opera == null) {
      opera = _isOpera();
    }
    return opera.booleanValue();
  }
  public native static boolean _isOpera()/*-{
		return $wnd.navigator.userAgent.toLowerCase().indexOf('opera') > -1;
	}-*/;
  
}

