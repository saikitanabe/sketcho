package net.sevenscales.editor.content.utils;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

public class EffectHelpers {
	public static native void fadeOut(JavaScriptObject element)/*-{
		if (typeof $wnd.jq172 == "function") $wnd.jq172(element).fadeOut("fast");
		else if (typeof $wnd.jQuery == "function") $wnd.jQuery(element).fadeOut("fast");
	}-*/;
	public static native void fadeIn(JavaScriptObject element)/*-{
		if (typeof $wnd.jq172 == "function") $wnd.jq172(element).fadeIn("fast");
		else if (typeof $wnd.jQuery == "function") $wnd.jQuery(element).fadeIn("fast");
	}-*/;
	public static native void slideOut(JavaScriptObject element)/*-{
		if (typeof $wnd.jq172 == "function") $wnd.jq172(element).hide("slide", {direction: "right"}, "fast");
		else if (typeof $wnd.jQuery == "function") $wnd.jQuery(element).hide("slide", {direction: "right"}, "fast");
	}-*/;
	
	public static native void slideIn(JavaScriptObject element)/*-{
		if (typeof $wnd.jq172 == "function") $wnd.jq172(element).show("slide", {direction: "right"}, "fast");
		else if (typeof $wnd.jQuery == "function") $wnd.jQuery(element).show("slide", {direction: "right"}, "fast");
	}-*/;
	
	public static native void tooltipper()/*-{
	  if (typeof $wnd.jq172 == "function") $wnd.jq172('.tooltipper').tooltip();
	  else if (typeof $wnd.jQuery == "function") $wnd.jQuery('.tooltipper').tooltip({container:'body'});
	}-*/;

	public static native void tooltipperHide()/*-{
		if (typeof $wnd.jq172 == "function") $wnd.jq172('.tooltip').hide();
		else if (typeof $wnd.jQuery == "function") $wnd.jQuery('.tooltip').hide();
	}-*/;
	
	public native static void tooltip(Element element, String placement)/*-{
	  var jq = function(element) {
  		if (typeof $wnd.jq172 == "function") return $wnd.jq172(element);
  		else if (typeof $wnd.jQuery == "function") return $wnd.jQuery(element);
  		return null;
    }
		
		jq(element).tooltip({placement:placement});
	}-*/;


}
