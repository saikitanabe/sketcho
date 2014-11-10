package net.sevenscales.editor.utils;

import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.gfx.domain.Color;


public class DiagramItemConfiguration {
	public static IDiagramItem setDefaultColors(IDiagramItem item) {
		item.setBackgroundColor(Theme.createDefaultBackgroundColor().toRgbWithOpacity() + ":" + Theme.createDefaultBorderColor().toRgbWithOpacity());
		item.setTextColor(Theme.createDefaultTextColor().toRgbWithOpacity());
		return item;
	}

	public static IDiagramItem setColors(IDiagramItem item, Color backgroundColor, Color borderColor, Color textColor) {
		String bgColor = "";
		if (backgroundColor != null) {
			bgColor = backgroundColor.toRgbWithOpacity();
		}
		if (borderColor != null) {
			bgColor += ":" + borderColor.toRgbWithOpacity();
		}
		if (!"".equals(bgColor)) {
			item.setBackgroundColor(bgColor);
		}
		if (textColor != null) {
			item.setTextColor(textColor.toRgbWithOpacity());
		}
		return item;
	}

}