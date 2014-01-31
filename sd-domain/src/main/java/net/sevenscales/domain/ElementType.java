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
	FORK("fork");

	private String value;

	private ElementType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}