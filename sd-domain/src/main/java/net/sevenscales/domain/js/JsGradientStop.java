package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;

// case class GradientStop(
// 	offset: String,
// 	stop_color: String
// )

public class JsGradientStop extends JavaScriptObject {
	protected JsGradientStop() {
	}

	public final native String getOffset()/*-{
		return this.offset
	}-*/;
	public final native String getStopColor()/*-{
		return this.stop_color
	}-*/;
}
