package net.sevenscales.editor.api;

public enum Tool {
	NO_TOOL					(0x0000),
	COMMENT_TOOL		(0x0001),
	FREEHAND_TOOL		(0x0002),
	CURVED_ARROW 		(0x0004),
	QUICK_MODE	 		(0x0008),
	SKETCH_MODE	 		(0x0010);

	private int value;

	private Tool(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

  public static Tool getEnum(int value) {
    for (Tool t : values()) {
      if (value == t.value) return t;
    }
    throw new IllegalArgumentException();
  }

}
