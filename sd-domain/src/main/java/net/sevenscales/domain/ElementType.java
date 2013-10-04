package net.sevenscales.domain;

public enum ElementType {
	COMMENT("comment");

	private String value;

	private ElementType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}