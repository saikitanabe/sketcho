package net.sevenscales.domain;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.domain.api.IExtension;

public class JSONParserHelpers {
	public static int getInt(JSONValue value) {
		return value != null && value.isNumber() != null ? (int) value.isNumber().doubleValue() : 0;
	}

	public static boolean getBoolean(JSONValue value) {
		return value != null && value.isBoolean() != null ? value.isBoolean().booleanValue() : false;
	}

	public static Integer getInteger(JSONValue value) {
		return value != null && value.isNumber() != null ? (int) value.isNumber().doubleValue() : null;
	}

	public static long getLong(JSONValue value) {
		return value != null && value.isNumber() != null ? (long) value.isNumber().doubleValue() : 0L;
	}

	public static Long getLongOrNull(JSONValue value) {
		return value != null && value.isNumber() != null ? (long) value.isNumber().doubleValue() : null;
	}

	public static double getDouble(JSONValue value) {
		return value != null && value.isNumber() != null ? value.isNumber().doubleValue() : 0f;
	}

	public static Double getDoubleOrNull(JSONValue value) {
		return value != null && value.isNumber() != null ? value.isNumber().doubleValue() : null;
	}

	public static String getString(JSONValue value) {
		return value != null && value.isString() != null ? value.isString().stringValue() : "";
	}

	public static String getStringOrNull(JSONValue value) {
		return value != null && value.isString() != null ? value.isString().stringValue() : null;
	}

	public static JavaScriptObject getObjectOrNull(JSONValue value) {
		return value != null && value.isObject() != null ? value.isObject().getJavaScriptObject() : null;
	}

	public static List<String> getListString(JSONValue value) {
		List<String> result = null;
		if (value != null && value.isArray() != null && value.isArray().size() > 0) {
			JSONArray array = value.isArray();
			result = new ArrayList<String>();
			for (int i = 0; i < array.size(); ++i) {
				JSONString link = array.get(i).isString();
				if (link != null) {
					result.add(link.stringValue());	
				}
			}
		}
		return result;
	}

	public static List<UrlLinkDTO> getListUrl(JSONValue value) {
		List<UrlLinkDTO> result = null;
		if (value != null && value.isArray() != null && value.isArray().size() > 0) {
			JSONArray array = value.isArray();
			result = new ArrayList<UrlLinkDTO>();
			for (int i = 0; i < array.size(); ++i) {
				JSONObject jlink = array.get(i).isObject();
				if (jlink != null) {
					JSONValue jurl = jlink.get("url");
					JSONValue jname = jlink.get("name");
					String url = null;
					String name = null;
					if (jurl != null && jurl.isString() != null) {
						url = jurl.isString().stringValue();
					}
					if (jname != null && jname.isString() != null) {
						name = jname.isString().stringValue();
					}
					result.add(new UrlLinkDTO(url, name));
				}
			}
		}
		return result;
	}

	public static IExtension getExtension(JSONValue value) {
		IExtension result = null;
		if (value != null && value.isObject() != null) {
			JSONObject extension = value.isObject();
			result = new ExtensionDTO(getSvgData(extension.get(DiagramItemField.SVG_DATA.getValue())),
																JSONParserHelpers.getInteger(extension.get(DiagramItemField.LINE_WEIGHT.getValue())));
		}
		return result;
	}

	public static ISvgDataRO getSvgData(JSONValue value) {
		ISvgDataRO result = null;
		if (value != null && value.isObject() != null) {
			JSONObject svgdata = value.isObject();
			JSONValue pathsvalue = svgdata.get(DiagramItemField.PATHS.getValue());
			if (pathsvalue != null && pathsvalue.isArray() != null) {
				JSONArray jpaths = pathsvalue.isArray();
				int size = jpaths.size();
				List<PathDTO> pathsdto = new ArrayList<PathDTO>();
				for (int i = 0; i < size; ++i) {
					JSONValue jpath = jpaths.get(i);
					if (jpath != null && jpath.isObject() != null) {
						JSONObject path = jpath.isObject();
						String svgpath = getString(path.get(DiagramItemField.PATH.getValue()));
						String svgstyle = getString(path.get(DiagramItemField.STYLE.getValue()));
						pathsdto.add(new PathDTO(svgpath, svgstyle));
					}
				}
				result = new SvgDataDTO(pathsdto, 
														 		getDouble(svgdata.get(DiagramItemField.SVG_WIDTH.getValue())),
														 		getDouble(svgdata.get(DiagramItemField.SVG_HEIGHT.getValue())));
			}
		}
		return result;
	}

}