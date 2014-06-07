package net.sevenscales.editor.uicomponents.impl;

import net.sevenscales.editor.diagram.shape.Info;

public interface RelationshipParser {	
	String parseArrowLine();
	Info parseShape(boolean curved);
	String parseLeftText();
	String parseRightText();
	String parseLabel();
	String parseConnection();
	void setText(String text);
}
