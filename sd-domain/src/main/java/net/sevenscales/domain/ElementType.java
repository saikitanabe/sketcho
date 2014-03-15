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
	FREEHAND("freehand"),
	PACKAGE("package"),
	VERTICAL_PARTITION("rectcont"),
	COMMENT_THREAD("comments"),
	HORIZONTAL_PARTITION("hpart"),
	FORK("fork"),
	STAR4("star4"),
	STAR5("star5"),
	ENVELOPE("envelope"),
	TRIANGLE("triangle");

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