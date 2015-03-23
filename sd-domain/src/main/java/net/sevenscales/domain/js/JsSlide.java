package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONNumber;

public class JsSlide extends JavaScriptObject {
	protected JsSlide() {
	}

	public final native int getLeft()/*-{
		return this.left
	}-*/;
	public final native int getTop()/*-{
		return this.top
	}-*/;
	public final native int getWidth()/*-{
		return this.width
	}-*/;
	public final native int getHeight()/*-{
		return this.height
	}-*/;
	public final native String getSvg()/*-{
		return this.svg
	}-*/;

	public static final JsSlide newSlide(String clientId, int left, int top, int width, int height, String svg) {
		JSONObject result = new JSONObject();

		result.put("clientId", new JSONString(clientId));
		result.put("left", new JSONNumber(left));
		result.put("top", new JSONNumber(top));
		result.put("width", new JSONNumber(width));
		result.put("height", new JSONNumber(height));
		result.put("svg", new JSONString(svg));
		return result.getJavaScriptObject().cast();
	}

}
