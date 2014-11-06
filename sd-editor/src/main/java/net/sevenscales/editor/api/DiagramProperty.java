package net.sevenscales.editor.api;

public enum DiagramProperty {
	SKETCH_MODE	 		(0x0001);

	private int value;

	private DiagramProperty(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

  public static DiagramProperty getEnum(int value) {
    for (DiagramProperty t : values()) {
      if (value == t.value) return t;
    }
    throw new IllegalArgumentException();
  }

}
