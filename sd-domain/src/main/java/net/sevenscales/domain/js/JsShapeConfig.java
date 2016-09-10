package net.sevenscales.domain.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;

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

	public final native String getDefaultBgColor()/*-{
		return this.dbc
	}-*/;
	public final native boolean isDefaultBgColor()/*-{
		return this.dbc
	}-*/;

	public final native String getDefaultBorderColor()/*-{
		return this.dboc
	}-*/;
	public final native boolean isDefaultBorderColor()/*-{
		return this.dboc
	}-*/;


	public final native boolean isTargetSizeDefined()/*-{
		return this.tw && this.th
	}-*/;

	/**
	* NOTE THIS IS NOT PART OF SHAPE CONFIGURATION FROM THE APP
	* THIS IS DYNAMIC PROPERTIES AND CAN HAVE LONG NAMES.
	*/
	public final native boolean isOpenEditor()/*-{
		if (typeof this.openEditor !== 'undefined') {
			return this.openEditor
		}
		// default value is true if not defined
		return true
	}-*/;
	public final native void setOpenEditor(boolean open)/*-{
		this.openEditor = open
	}-*/;

	public static final JsShapeConfig create(String text, int width, int height) {
		JSONObject result = new JSONObject();

		result.put("dt", new JSONString(text));

		if (width > 0) {
			result.put("tw", new JSONNumber(width));
		}

		if (height > 0) {
			result.put("th", new JSONNumber(height));
		}

		return result.getJavaScriptObject().cast();
	}

}