package net.sevenscales.domain;

public enum TextPosition {
	CENTER(0), TOP(1), RIGHT(2), BOTTOM(3), LEFT(4);

	private int value;

	private TextPosition(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static TextPosition getEnum(int value) {
		// if (operation == null) {
  //     throw new IllegalArgumentException();
		// }
		
		for (TextPosition v : values()) {
      if (v.getValue() == value) return v;
		}
		throw new IllegalArgumentException();
	}
}