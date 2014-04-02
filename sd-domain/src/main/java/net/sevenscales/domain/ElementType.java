package net.sevenscales.domain;

public enum ElementType {
	ELLIPSE("ellipseitem"),
	SEQUENCE("sequenceitem"),
	COMPONENT("comp"),
	SERVER("server"),
	CLASS("classitem"),
	NOTE("noteitem"),
	CHOICE("choice"),
	ACTIVITY_START("activitystart"),
	ACTIVITY_END("activityend"),
	ACTIVITY("activity"),
	MIND_CENTRAL("centtop"),
	STORAGE("storage"),
	COMMENT("comment"),
	TEXT_ITEM("textitem"),
	ACTOR("actoritem"),
	RELATIONSHIP("relationship"),
	FREEHAND("freehand"), /* deprecated legary freehand */
	PACKAGE("package"),
	VERTICAL_PARTITION("rectcont"),
	COMMENT_THREAD("comments"),
	HORIZONTAL_PARTITION("hpart"),
	FORK("fork"),
	IMAGE("img"),
	STAR4("star4"),
	STAR5("star5"),
	ENVELOPE("envelope"),
	TRIANGLE("triangle"),
	CLOUD("cloud"),
	FIREWALL("firewall"),
	BUBBLE("bubble-l"),
	BUBBLE_R("bubble-r"),
	CIRCLE("circle"),
	SMILEY("smiley"),
	POLYGON4("polygon4"),
	POLYGON8("polygon8"),
	ARROW_UP("arrow-up"),
	ARROW_DOWN("arrow-d"),
	ARROW_RIGHT("arrow-r"),
	ARROW_LEFT("arrow-l"),
	IPHONE("iphone"),
	WEB_BROWSER("w-browser"),
	RECT("rect"),
	FREEHAND2("freehand2");

	private String value;

	private ElementType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static ElementType getEnum(String operation) {
		if (operation == null) {
      throw new IllegalArgumentException();
		}
		
		for (ElementType v : values()) {
      if (operation.equalsIgnoreCase(v.getValue())) return v;
		}
		throw new IllegalArgumentException();
	}
	
}