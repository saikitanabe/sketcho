package net.sevenscales.editor.gfx.svg.converter;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.domain.js.JsPresentation;
import net.sevenscales.domain.js.JsSlide;
import net.sevenscales.domain.js.JsSlideData;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.domain.ElementType;
import net.sevenscales.editor.gfx.svg.converter.SvgConverter;
import net.sevenscales.domain.api.IDiagramItem;
import net.sevenscales.editor.api.BoardDimensions;
import net.sevenscales.editor.api.ot.BoardDocumentHelpers;
import net.sevenscales.domain.json.JsonExtraction;

public class SlidesHelper {

	public static JsPresentation getPresentation(ISurfaceHandler surface, int slideWidth, int slideHeight, boolean blackBackground) {

		JsArray<JsSlide> jsSlides = JavaScriptObject.createArray().cast();

		List<Diagram> slides = _getSlides(surface);

		SvgBase.setThemeBlack(blackBackground);
		// SvgConverter sc = new SvgConverter(false);

		for (Diagram slide : slides) {
			List<Diagram> diagrams = getDiagramListOnArea(surface, slide.getLeft(), slide.getTop(), slide.getWidth(), slide.getHeight());
			// Diagram[] diagrams = getDiagramsOnArea(surface, slide.getLeft(), slide.getTop(), slide.getWidth(), slide.getHeight());


			// SvgData svgdata = sc.diagramsToSvg(surface, diagrams, false, false);

			// String svg = sc.diagramsToSvg(surface, diagrams, false, true, -1 * slide.getLeft(), -1 * slide.getTop());

      List<? extends IDiagramItemRO> items = BoardDocumentHelpers.diagramsToItems(diagrams);

			String svg = reactDiagramsToSvg(
        JsonExtraction.decompose(items).getJavaScriptObject(),
        -1 * slide.getLeft(),
        -1 * slide.getTop()
      );

			JsSlideData data = slide.getDiagramItem().getData().cast();
			jsSlides.push(JsSlide.newSlide(slide.getDiagramItem().getClientId(), data, slide.getLeft(), slide.getTop(), slide.getWidth(), slide.getHeight(), formatSvg(slideWidth, slideHeight, slide.getWidth(), slide.getHeight(), svg)));
		}

		SvgBase.setThemeBlack(false);

		BoardDimensions.resolveDimensions(slides);

		int width = BoardDimensions.getRightmost() - BoardDimensions.getLeftmost();
		int height = BoardDimensions.getBottommost() - BoardDimensions.getTopmost();

		return JsPresentation.newPresentation(width, height, jsSlides);
	}

	private static native String reactDiagramsToSvg(
    JavaScriptObject items,
    int zeroLeft,
    int zeroTop
	)/*-{
		return $wnd.reactDiagramsToSvg(items, zeroLeft, zeroTop);
	}-*/;

	private static String formatSvg(int slideWidth, int slideHeight, int width, int height, String svg) {
		if (slideWidth == 0) {
			String svgStart = "<svg width='" + width + "' height='" + height + "'>";
			return svgStart + svg + "</svg>";
		} else {
			String xmlDef = "<?xml version='1.0' encoding='UTF-8'?>";
			String svgStart = "<svg xmlns='http://www.w3.org/2000/svg' version='1.1' xmlns:xlink='http://www.w3.org/1999/xlink' width='" + slideWidth + "' height='" + slideHeight + "'>";
			double sx = slideWidth / (double) width;
			double sy = slideHeight / (double) height;
			double scale = sx < sy ? sx : sy;

			// center slide with transform
			String slideGroup = "<g transform='translate("+(slideWidth / 2 - (scale * width) / 2)+","+(slideHeight / 2 - (scale * height) / 2)+") scale("+scale+")'>" + svg + "</g>";
			return xmlDef + svgStart + slideGroup + "</svg>";
		}
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

	private static List<Diagram> getDiagramListOnArea(ISurfaceHandler surface, int x, int y, int width, int height) {
		List<Diagram> result = new ArrayList<Diagram>();
		for (Diagram d : surface.getDiagrams()) {
			IDiagramItem di = d.getDiagramItem();
			if (di != null && !ElementType.SLIDE.getValue().equals(di.getType()) && d.onArea(x, y, x + width, y + height)) {
				result.add(d);
			}
		}

		return result;
	}

	private static Diagram[] getDiagramsOnArea(ISurfaceHandler surface, int x, int y, int width, int height) {
		List<Diagram> result = getDiagramListOnArea(surface, x, y, width, height);

		Diagram[] r = new Diagram[result.size()];
		result.toArray(r);
		return r;
	}

}

