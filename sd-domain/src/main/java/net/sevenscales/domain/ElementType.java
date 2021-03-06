package net.sevenscales.domain;

public enum ElementType {
	ELLIPSE("ellipseitem"),
	USE_CASE("usecase"),
	SEQUENCE("sequenceitem"),
	COMPONENT("comp"),
	SERVER("server"),
	CLASS("classitem"),
	NOTE("noteitem"),
	CHOICE("choice"),
	ACTIVITY_START("activitystart"),
	ACTIVITY_START2("astart"),
	ACTIVITY_END("activityend"),
	ACTIVITY_END2("aend"),
	ACTIVITY("activity"),
	MIND_CENTRAL("centtop"),
	STORAGE("storage"),
	COMMENT("comment"),
	TEXT_ITEM("textitem"),
	CHILD_TEXT("childtext"),
	ACTOR("actoritem"),
	RELATIONSHIP("relationship"),
	FREEHAND("freehand"), /* deprecated legary freehand */
	PACKAGE("package"),
	VERTICAL_PARTITION("rectcont"),
	COMMENT_THREAD("comments"),
	HORIZONTAL_PARTITION("hpart"),
	FORK("fork"),
	FORK_HORIZONTAL("hfork"),
	FORK_VERTICAL("vfork"),
	IMAGE("img"),
	// NOTE url image needs to be a new type!!! G_IMAGE("gimg") Makes aws url and name parsing more robust.
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
	FREEHAND2("freehand2"),
	SWITCH("switch"),
	ROUTER("router"),
	DESKTOP("desktop"),
	LAPTOP("laptop"),
	SERVER2("server2"),
	TABLET_UP("tablet_u"),
	TABLET_HORIZONTAL("tablet_h"),
	OLD_PHONE("phone"),
	ANDROID("android"),
	LIGHTBULB("lightbulb"),
	SLIDE("o_slide");

	private String value;

	private ElementType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	// public static ElementType getEnum(String value) {
	// 	if (value == null) {
 //      throw new IllegalArgumentException();
	// 	}
		
	// 	for (ElementType v : values()) {
 //      if (value.equalsIgnoreCase(v.getValue())) return v;
	// 	}
	// 	throw new IllegalArgumentException();
	// }
	
}