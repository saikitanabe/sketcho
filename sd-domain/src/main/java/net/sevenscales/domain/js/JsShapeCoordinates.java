package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONNumber;


public class JsShapeCoordinates extends JavaScriptObject {
	protected JsShapeCoordinates() {
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
	public final native int getRotationDegrees()/*-{
		return this.rotationDegrees
	}-*/;

	public static JsShapeCoordinates create(
    int left,
    int top,
    double width,
    double height,
    Integer rotationDegrees
  ) {
		JSONObject result = new JSONObject();

    rotationDegrees = rotationDegrees != null ? rotationDegrees : 0;

		result.put("left", new JSONNumber(left));
		result.put("top", new JSONNumber(top));
		result.put("width", new JSONNumber(width));
		result.put("height", new JSONNumber(height));
		result.put("rotationDegrees", new JSONNumber(rotationDegrees));

		return result.getJavaScriptObject().cast();
	}

}
