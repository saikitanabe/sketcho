package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONNumber;


public class JsDimension extends JavaScriptObject {
	protected JsDimension() {
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

	public static JsDimension create(int left, int top, int width, int height) {
		JSONObject result = new JSONObject();

		result.put("left", new JSONNumber(left));
		result.put("top", new JSONNumber(top));
		result.put("width", new JSONNumber(width));
		result.put("height", new JSONNumber(height));

		return result.getJavaScriptObject().cast();
	}

}
