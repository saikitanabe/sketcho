package net.sevenscales.domain.js;

import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONObject;
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

	public static JSONObject copyJSONValue(
    JsGradientStop stop
  ) {
		JSONObject result = new JSONObject();

		result.put("offset", new JSONString(stop.getOffset()));
		result.put("stop_color", new JSONString(stop.getStopColor()));

		return result;
	}
}
