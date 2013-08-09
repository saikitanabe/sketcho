package net.sevenscales.editor.uicomponents.helpers;

import net.sevenscales.editor.uicomponents.AbstractDiagramItem;

public interface IGlobalElement {
	AbstractDiagramItem getParent();
	void hideGlobalElement();
	void release();
}