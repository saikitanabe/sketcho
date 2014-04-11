package net.sevenscales.editor.content.utils;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.ImageElement;
import net.sevenscales.editor.diagram.shape.ImageShape;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.gfx.domain.Color;


public class DiagramElementFactory {
	public static Diagram createImageElement(ISurfaceHandler surface, String filename, String url, int x, int y, int width, int height) {
		ImageShape shape = new ImageShape(x, y, width, height, url, filename);
		return new ImageElement(surface, 
														shape, 
														Theme.createDefaultBackgroundColor(), 
														new Color(0, 0, 0, 0), // Theme.createDefaultBorderColor(), 
														Theme.createDefaultTextColor(),
														true,
														new DiagramItemDTO());
	}
}
