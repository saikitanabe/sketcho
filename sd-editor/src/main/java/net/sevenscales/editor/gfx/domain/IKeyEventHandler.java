package net.sevenscales.editor.gfx.domain;
public interface IKeyEventHandler {
	public void handleKeyDown(int keyCode, int platformCode, boolean shift, boolean ctrl);
	public void handleKeyUp(int keyCode, boolean shift, boolean ctrl);
}
