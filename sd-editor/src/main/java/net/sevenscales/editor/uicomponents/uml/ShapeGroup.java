package net.sevenscales.editor.uicomponents.uml;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.core.client.JsArray;

import net.sevenscales.domain.js.JsShape;
import net.sevenscales.domain.js.JsPath;
import net.sevenscales.domain.js.JsShapeConfig;

public class ShapeGroup {
	public String elementType;
	public ShapeProto[] protos;

		// NOTE: important to keep as float or double; int will be really slow!
	public double width;
	public double height;
	public Integer properties;
	public Integer fontSize;
	private JsShapeConfig config;

	public ShapeGroup(String elementType, ShapeProto[] protos, double width, double height, Integer properties) {
		this.elementType = elementType;
		this.protos = protos;
		this.width = width;
		this.height = height;
		this.properties = properties;
	}

	public void setShapeConfig(JsShapeConfig config) {
		this.config = config;
	}

	public JsShapeConfig getShapeConfig() {
		return config;
	}

	public String getDefaultText() {
		if (config != null && config.getDefaultText() != null) {
			return config.getDefaultText();
		}
		return "";
	}

	public boolean isTargetSizeDefined() {
		if (config != null && config.getTargetWidth() > 0 && config.getTargetHeight() > 0) {
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
			
			if (sp.styleLib != null) {
				path.put("sl", new JSONString(sp.styleLib));
			}

			JsPath p = path.getJavaScriptObject().cast();
			paths.push(p);
		}

		result.put("s", new JSONArray(paths));
		result.put("w", new JSONNumber(width));
		result.put("h", new JSONNumber(height));

		if (config != null) {
			result.put("c", new JSONObject(config));
		}

		return result.getJavaScriptObject().cast();
	}

}
