package net.sevenscales.domain;

import com.google.gwt.json.client.JSONValue;

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

}