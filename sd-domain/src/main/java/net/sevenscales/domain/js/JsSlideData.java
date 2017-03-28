package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

public class JsSlideData extends JavaScriptObject {
	protected JsSlideData() {
	}

	// public final native int getLeft()/*-{
	// 	return this.left
	// }-*/;
	// public final native int getTop()/*-{
	// 	return this.top
	// }-*/;
	// public final native int getWidth()/*-{
	// 	return this.width
	// }-*/;
	// public final native int getHeight()/*-{
	// 	return this.height
	// }-*/;
	// public final native String getSvg()/*-{
	// 	return this.svg
	// }-*/;

	public static final JsSlideData newSlideData(double order) {
		JSONObject result = new JSONObject();
		result.put("order", new JSONNumber(order));
		return result.getJavaScriptObject().cast();
	}

}
