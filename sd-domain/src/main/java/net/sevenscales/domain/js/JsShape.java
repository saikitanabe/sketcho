package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


public class JsShape extends JavaScriptObject {
	protected JsShape() {
	}

	public final native String getElementType()/*-{
		return this.et
	}-*/;
	public final native int getShapeType()/*-{
		return this.st
	}-*/;
	public final native JsArray<JsPath> getShape()/*-{
		return this.s
	}-*/;
	public final native double getWidth()/*-{
		return this.w
	}-*/;
	public final native double getHeight()/*-{
		return this.h
	}-*/;
	public final Integer getProperties() {
		// some GWT bug with new library shape json
		// Integer is int though it should be Integer...
		Integer i = doGetProperties();
		if (i != null) {
			return new Integer(parseInt(i));
		}
		return null;
	}
	private final native Integer doGetProperties()/*-{
		return this.p
	}-*/;
	public final native Integer getFontSize()/*-{
		return this.f
	}-*/;

	public final native JsShapeConfig getConfig()/*-{
		return this.c
	}-*/;

	private static native int parseInt(Object val)/*-{
    return $wnd.parseInt(val);
  }-*/;

}
