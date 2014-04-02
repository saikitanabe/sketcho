package net.sevenscales.editor.content.utils;

import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.ImageElement;
import net.sevenscales.editor.diagram.shape.ImageShape;
import net.sevenscales.editor.api.impl.Theme;


public class DiagramElementFactory {
	public static Diagram createImageElement(ISurfaceHandler surface, String filename, String url, int x, int y, int width, int height) {
		ImageShape shape = new ImageShape(x, y, width, height, url, filename);
		return new ImageElement(surface, 
														shape, 
														Theme.createDefaultBackgroundColor(), 
														Theme.createDefaultBorderColor(), 
														Theme.createDefaultTextColor(),
														true,
														new DiagramItemDTO());
	}
}
