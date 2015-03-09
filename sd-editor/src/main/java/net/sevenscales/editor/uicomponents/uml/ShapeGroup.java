package net.sevenscales.editor.uicomponents.uml;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.core.client.JsArray;

import net.sevenscales.domain.js.JsShape;
import net.sevenscales.domain.js.JsPath;

public class ShapeGroup {
	public String elementType;
	public ShapeProto[] protos;

		// NOTE: important to keep as float or double; int will be really slow!
	public double width;
	public double height;
	public Integer properties;
	public Integer fontSize;
	public double targetWidth;
	public double targetHeight;

	public ShapeGroup(String elementType, ShapeProto[] protos, double width, double height, Integer properties) {
		this.elementType = elementType;
		this.protos = protos;
		this.width = width;
		this.height = height;
		this.properties = properties;
	}

	public boolean isTargetSizeDefined() {
		if (targetWidth > 0 && targetHeight > 0) {
			return true;
		}
		return false;
	}

	public JsShape scaleToShape(double factorX, double factorY) {
		JSONObject result = new JSONObject();
		result.put("et", new JSONString(elementType));

		JsArray<JsPath> paths = JavaScriptObject.createArray().cast();

		for (ShapeProto sp : protos) {
			JSONObject path = new JSONObject();
			path.put("p", new JSONString(sp.toPath(factorX, factorY, width)));
			path.put("s", new JSONString(sp.style));
			JsPath p = path.getJavaScriptObject().cast();
			paths.push(p);
		}

		result.put("s", new JSONArray(paths));
		result.put("w", new JSONNumber(width));
		result.put("h", new JSONNumber(height));

		return result.getJavaScriptObject().cast();
	}

}
