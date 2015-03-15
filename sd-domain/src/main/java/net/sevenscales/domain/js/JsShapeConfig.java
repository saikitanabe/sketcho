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
	private final native String _getDefaultText()/*-{
		return this.dt
	}-*/;

	public final String getDefaultText() {
		if (isDefaultTextDefined()) {
			return _getDefaultText();
		}
		return "";
	}

	public final native boolean isDefaultTextDefined()/*-{
		return this.dt
	}-*/;


	public final native boolean isTargetSizeDefined()/*-{
		return this.tw && this.th
	}-*/;
}