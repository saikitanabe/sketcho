package net.sevenscales.editor.content.utils;

import com.google.gwt.dom.client.Element;

public class JQuery {
	public static native void click(Element element)/*-{
		if (typeof $wnd.jq172 == "function") $wnd.jq172(element).click();
	  else if (typeof $wnd.jQuery == "function") $wnd.jQuery(element).click();
	}-*/;

	public static native void flashStyleClass(String search, String clazz)/*-{
		if (typeof $wnd.jq172 == "function") $wnd.jq172(search).addClass(clazz).delay(1000).removeClass(clazz);
	  else if (typeof $wnd.jQuery == "function") {
	  	$wnd.jQuery(search).addClass(clazz)
	  	setTimeout(function() {
	  		$wnd.jQuery(search).removeClass(clazz)
      }, 150);
	  }
	}-*/;

	public static native void focus(String search)/*-{
		if (typeof $wnd.jq172 == "function") $wnd.jq172(search).focus();
	  else if (typeof $wnd.jQuery == "function") $wnd.jQuery(search).focus();
	}-*/;

	public static native void twitterButtonToggle(String search)/*-{
		if (typeof $wnd.jq172 == "function") $wnd.jq172(search).button('toggle')
	  else if (typeof $wnd.jQuery == "function") $wnd.jQuery(search).button('toggle')
	}-*/;

	public static native void attr(String selector, String name, String value)/*-{
		if (typeof $wnd.jq172 == "function") $wnd.jq172(selector).attr(name, value);
	  else if (typeof $wnd.jQuery == "function") $wnd.jQuery(selector).attr(name, value);
	}-*/;
}
