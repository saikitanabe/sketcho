package net.sevenscales.editor.content.ui;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.diagram.utils.Color;
import net.sevenscales.editor.uicomponents.AbstractDiagramItem;

public class SimpleColorHandler {
	private Color color = new Color("444444", 0x44, 0x44, 0x44, "ffffff", 0xff, 0xff, 0xff, "333333", 0x33, 0x33, 0x33, AbstractDiagramItem.DEFAULT_FILL_OPACITY);

	public SimpleColorHandler(EditorContext editorContext) {
		editorContext.set(EditorProperty.CURRENT_COLOR, color);
	}
}
