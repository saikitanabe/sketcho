package net.sevenscales.editor.uicomponents.helpers;

import net.sevenscales.editor.diagram.Diagram;

public interface IGlobalElement {
	Diagram getParent();
	void hideGlobalElement();
	void release();
}