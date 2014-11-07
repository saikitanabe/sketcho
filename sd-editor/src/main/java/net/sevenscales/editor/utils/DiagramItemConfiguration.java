package net.sevenscales.editor.utils;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.editor.api.impl.Theme;


public class DiagramItemConfiguration {
	public static IDiagramItem setDefaultColors(IDiagramItem item) {
		item.setBackgroundColor(Theme.createDefaultBackgroundColor().toRgbWithOpacity() + ":" + Theme.createDefaultBorderColor().toRgbWithOpacity());
		item.setTextColor(Theme.createDefaultTextColor().toRgbWithOpacity());
		return item;
	}
}