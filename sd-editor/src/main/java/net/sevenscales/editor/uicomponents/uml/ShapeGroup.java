package net.sevenscales.editor.uicomponents.uml;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.core.client.JsArray;

import net.sevenscales.domain.js.JsShape;
import net.sevenscales.domain.js.JsPath;
import net.sevenscales.domain.js.JsGradient;
import net.sevenscales.domain.js.JsShapeConfig;

public class ShapeGroup {
	public String elementType;
	public int shapeType;
	public ShapeProto[] protos;

		// NOTE: important to keep as float or double; int will be really slow!
	public double width;
	public double height;
	public Integer properties;
	public Integer fontSize;
	private JsShapeConfig config;
	public JsArray<JsGradient> gradients;

	public ShapeGroup(
    String elementType,
    int shapeType,
    ShapeProto[] protos,
    double width,
    double height,
    Integer properties,
    JsArray<JsGradient> gradients
  ) {
		this.elementType = elementType;
		this.shapeType = shapeType;
		this.protos = protos;
		this.width = width;
		this.height = height;
		this.properties = properties;
		this.gradients = copyGradients(gradients);
	}

	private JsArray<JsGradient> copyGradients(JsArray<JsGradient> gradients) {
		JsArray<JsGradient> result = JsArray.createArray().cast();

		for (int i = 0; i < gradients.length(); ++i) {
			JsGradient clone = JsGradient.copy(gradients.get(i));
			result.push(clone);
		}

		return result;
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
  
  public void setStyleGradientId(
    String oldId,
    String newId
  ) {
    for (ShapeProto p : protos) {
      p.style = p.style.replace("#" + oldId, "#" + newId);
    }
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
