package net.sevenscales.editor.content.ui;

public enum ContextMenuItem {
	NO_MENU										(0),
	FREEHAND_MENU							(0x0001),
	REVERSE_CONNECTION_MENU		(0x0002), 
	COLOR_MENU								(0x0004), 
	CHANGE_CONNECTION					(0x0008),
	DUPLICATE									(0x0010),
	FONT_SIZE									(0x0020),
	LAYERS										(0x0040),
	URL_LINK									(0x0080),
	DELETE										(0x0100),
	LINE_WEIGHT								(0x0200),
	TEXT_ALIGN								(0x0400),
	ROTATE								    (0x0800);

	private int value;

	private ContextMenuItem(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public static boolean supported(int value, ContextMenuItem menu) {
		return (value & menu.value) == menu.value;
	}

	public static boolean supportsFontSize(int value) {
		return supported(value, FONT_SIZE);
	}

	public static boolean supportsRotate(int value) {
		return supported(value, ROTATE);
	}
}
