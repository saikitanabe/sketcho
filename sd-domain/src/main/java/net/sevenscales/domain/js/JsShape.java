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

}
