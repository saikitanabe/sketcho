package net.sevenscales.domain;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONObject;

public class JSONParserHelpers {
	public static int getInt(JSONValue value) {
		return value != null && value.isNumber() != null ? (int) value.isNumber().doubleValue() : 0;
	}

	public static long getLong(JSONValue value) {
		return value != null && value.isNumber() != null ? (long) value.isNumber().doubleValue() : 0L;
	}

	public static double getDouble(JSONValue value) {
		return value != null && value.isNumber() != null ? value.isNumber().doubleValue() : 0f;
	}

	public static String getString(JSONValue value) {
		return value != null && value.isString() != null ? value.isString().stringValue() : "";
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

}