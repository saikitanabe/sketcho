package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class JsSvg extends JavaScriptObject {

	protected JsSvg() {
	}

	public final native String getSvg()/*-{
		return this.svg
  }-*/;
  
	public final native int getWidth()/*-{
		return this.width
  }-*/;
  
	public final native int getHeight()/*-{
		return this.height
	}-*/;

	public static JsSvg create(String svg) {
		JSONObject result = new JSONObject();

		result.put("svg", new JSONString(svg));
		
		return result.getJavaScriptObject().cast();
	}

}
