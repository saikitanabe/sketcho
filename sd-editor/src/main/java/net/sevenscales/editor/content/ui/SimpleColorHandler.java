package net.sevenscales.editor.content.ui;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;
import net.sevenscales.editor.gfx.domain.ElementColor;

public class SimpleColorHandler {
	private ElementColor color = new ElementColor();

	public SimpleColorHandler(EditorContext editorContext) {
		editorContext.set(EditorProperty.CURRENT_COLOR, color);
	}
}
