package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;

public class JsPath extends JavaScriptObject {
	protected JsPath() {
	}

	public final native String getPath()/*-{
		return this.p
	}-*/;
	public final native String getStyle()/*-{
		return this.s
	}-*/;
	public final native boolean getNoScaling()/*-{
		return this.noscaling
	}-*/;
}
