package net.sevenscales.domain;

public enum DiagramItemField {
	ID("id"),
	TEXT("text"),
	TYPE("elementType"),
	SHAPE("shape"),
	EXTENSION("ext"),
		SVG_DATA("svg"),
			PATHS("paths"),
				PATH("path"),
				STYLE("style"),
			SVG_WIDTH("width"),
			SVG_HEIGHT("height"),
		LINE_WEIGHT("lweight"), // relationship or freehand specific line weight
	BACKGROUND_COLOR("backgroundColor"),
	TEXT_COLOR("textColor"),
	FONT_SIZE("fsize"),
	SHAPE_PROPERTIES("props"),
	DISPLAY_ORDER("dord"),
	CLIENT_ID("clientId"),
	CUSTOM_DATA("cd"),
	VERSION("version"),
	CRC("crc"),
	GROUP("group"),
	DATA("data"),
	ROTATE_DEGREES("rdeg"),
	ANNOTATION("a"),
	RESOLVED("r"),
	LINKS("links"),
	CREATED_BY("cby"),
	CREATED_BY_DISPLAY_NAME("cbyd"),
	CREATED_AT("cat"),
	UPDATED_BY("uby"),
	UPDATED_BY_DISPLAY_NAME("ubyd"),
	UPDATED_AT("uat"),
	PARENT("p");

	private String value;
	private DiagramItemField(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static DiagramItemField getEnum(String value) {
		if (value == null) {
      throw new IllegalArgumentException();
		}
		
		for (DiagramItemField v : values()) {
      if (value.equalsIgnoreCase(v.getValue())) return v;
		}
		throw new IllegalArgumentException();
	}

}