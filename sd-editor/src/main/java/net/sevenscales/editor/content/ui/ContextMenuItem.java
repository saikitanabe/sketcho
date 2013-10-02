package net.sevenscales.editor.content.ui;

public enum ContextMenuItem {
	NO_CUSTOM(0), FREEHAND_MENU(0x0001), REVERSE_CONNECTION_MENU(0x0010), COLOR_MENU(0x0100), CHANGE_CONNECTION(0x1000);
	
	private int value;

	private ContextMenuItem(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
}
