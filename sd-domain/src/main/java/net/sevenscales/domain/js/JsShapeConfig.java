package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;


public class JsShapeConfig extends JavaScriptObject {

	protected JsShapeConfig() {

	}

	public final native double getTargetWidth()/*-{
		return this.tw
	}-*/;
	public final native double getTargetHeight()/*-{
		return this.th
	}-*/;
}