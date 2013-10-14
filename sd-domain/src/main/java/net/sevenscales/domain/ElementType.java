package net.sevenscales.domain;

public enum ElementType {
	COMMENT("comment"),
	COMMENT_THREAD("comments");

	private String value;

	private ElementType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}