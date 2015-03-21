package net.sevenscales.editor.gfx.svg.converter;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.domain.js.JsPresentation;
import net.sevenscales.domain.js.JsSlide;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.domain.ElementType;
import net.sevenscales.editor.gfx.svg.converter.SvgConverter;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.editor.api.BoardDimensions;

public class SlidesHelper {

	public static JsPresentation getPresentation(ISurfaceHandler surface) {

		JsArray<JsSlide> jsSlides = JavaScriptObject.createArray().cast();

		List<Diagram> slides = _getSlides(surface);

		SvgConverter sc = new SvgConverter(false);

		for (Diagram slide : slides) {
			Diagram[] diagrams = getDiagramsOnArea(surface, slide.getLeft(), slide.getTop(), slide.getWidth(), slide.getHeight());
			// SvgData svgdata = sc.diagramsToSvg(surface, diagrams, false, false);

			String svg = sc.diagramsToSvg(surface, diagrams, false, false, -1 * slide.getLeft(), -1 * slide.getTop());

			jsSlides.push(JsSlide.newSlide(slide.getLeft(), slide.getTop(), slide.getWidth(), slide.getHeight(), formatSvg(slide.getWidth(), slide.getHeight(), svg)));
		}

		BoardDimensions.resolveDimensions(slides);

		int width = BoardDimensions.getRightmost() - BoardDimensions.getLeftmost();
		int height = BoardDimensions.getBottommost() - BoardDimensions.getTopmost();

		return JsPresentation.newPresentation(width, height, jsSlides);
	}

	private static String formatSvg(int width, int height, String svg) {
		String svgStart = "<svg width='" + width + "' height='" + height + "'>";
		return svgStart + svg + "</svg>";
	}

	private static List<Diagram> _getSlides(ISurfaceHandler surface) {
		List<Diagram> result = new ArrayList<Diagram>();
		for (Diagram d : surface.getDiagrams()) {
			IDiagramItem di = d.getDiagramItem();
			if (di != null && ElementType.SLIDE.getValue().equals(di.getType())) {
				result.add(d);
			}
		}
		return result;
	}

	private static Diagram[] getDiagramsOnArea(ISurfaceHandler surface, int x, int y, int width, int height) {
		List<Diagram> result = new ArrayList<Diagram>();
		for (Diagram d : surface.getDiagrams()) {
			IDiagramItem di = d.getDiagramItem();
			if (di != null && !ElementType.SLIDE.getValue().equals(di.getType()) && d.onArea(x, y, x + width, y + height)) {
				result.add(d);
			}
		}

		Diagram[] r = new Diagram[result.size()];
		result.toArray(r);
		return r;
	}

}

