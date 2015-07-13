package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONNumber;


public class JsTransform extends JavaScriptObject {
	protected JsTransform() {
	}

	public final native int getTransformX()/*-{
		return this.transformX
	}-*/;
	public final native int getTransformY()/*-{
		return this.transformY
	}-*/;
	public final native JavaScriptObject getMatrix()/*-{
		return this.matrix
	}-*/;

	public static JsTransform create(int transformX, int transformY, JavaScriptObject matrix) {
		JSONObject result = new JSONObject();

		result.put("transformX", new JSONNumber(transformX));
		result.put("transformY", new JSONNumber(transformY));
		result.put("matrix", new JSONObject(matrix));

		return result.getJavaScriptObject().cast();
	}

}
