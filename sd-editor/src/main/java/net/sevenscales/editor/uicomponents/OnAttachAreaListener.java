package net.sevenscales.editor.uicomponents;

import net.sevenscales.editor.diagram.Diagram;

public interface OnAttachAreaListener {
	void onAttachArea(Diagram me, int x, int y);
	void notOnArea(Diagram me);
}
