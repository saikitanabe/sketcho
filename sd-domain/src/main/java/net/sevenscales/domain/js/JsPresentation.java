package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

public class JsPresentation extends JavaScriptObject {
	protected JsPresentation() {
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
	public final native JsArray<JsSlide> getSlides()/*-{
		return this.slides
	}-*/;

	public static final JsPresentation newPresentation(int width, int height, JsArray<JsSlide> slides) {
		JSONObject result = new JSONObject();

		// result.put("left", new JSONNumber(left));
		// result.put("top", new JSONNumber(top));
		result.put("width", new JSONNumber(width));
		result.put("height", new JSONNumber(height));
		result.put("slides", new JSONArray(slides));

		return result.getJavaScriptObject().cast();
	}

}
