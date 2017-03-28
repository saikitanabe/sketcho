package net.sevenscales.editor.uicomponents.uml;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;


interface IGenericElement {
	AbstractDiagramItem getDiagram();
	IDiagramItem getDiagramItem();
	int getRelativeLeft();
	int getRelativeTop();
	int getLeft();
	int getTop();
	int getWidth();
	int getHeight();
	double getTextHeight();
	boolean resize(int left, int top, int width, int height);
	void fireSizeChanged();
}