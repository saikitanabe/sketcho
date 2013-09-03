package net.sevenscales.editor.content.utils;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.ui.UiContextMenu;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.uicomponents.Point;

import com.google.gwt.core.client.JavaScriptObject;

public class ScaleHelpers {
	public static native int scaleValue(int value, float scalefactor)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var i = m.invert(m.scale(scalefactor));
		return parseInt(i.xx * value);
	}-*/;
	
	public static native int unscaleValue(int value, float scalefactor)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var i = m.scale(scalefactor);
		return parseInt(i.xx * value);
	}-*/;


	public static native JavaScriptObject scaleCoordinate(int x, int y, float scalefactor)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var i = m.invert(m.scale(scalefactor));
		return {
			x : i.xx * x,
			y : i.yy * y
		};
	}-*/;

	public static native JavaScriptObject unscaleCoordinate(int x, int y, float scalefactor)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var i = m.scale(scalefactor);
		return {
			x : i.xx * x,
			y : i.yy * y
		};
	}-*/;
	
	public static class ScaledAndTranslatedPoint {
		public Point scaledAndTranslated;
		public MatrixPointJS scaledPoint;
	}
	public static ScaledAndTranslatedPoint scaleAndTranslateScreenpoint(int screenX, int screenY, ISurfaceHandler surface) {
		ScaledAndTranslatedPoint result = new ScaledAndTranslatedPoint();
		// scale point
		result.scaledAndTranslated = new Point();
		result.scaledPoint = MatrixPointJS.createScaledPoint(screenX, screenY, surface.getScaleFactor());
		
		// translate by root layer location
		result.scaledAndTranslated.x = result.scaledPoint.getX() - ScaleHelpers.scaleValue(surface.getRootLayer().getTransformX(), surface.getScaleFactor()); 
		result.scaledAndTranslated.y = result.scaledPoint.getY() - ScaleHelpers.scaleValue(surface.getRootLayer().getTransformY(), surface.getScaleFactor());
		return result;
	}
	
	public static int scaleAndTranslateY(int scaledY, ISurfaceHandler surface) {
		return unscaleValue(scaledY, surface.getScaleFactor()) + surface.getRootLayer().getTransformY();
	}

	public static int scaleAndTranslateX(int scaledX, ISurfaceHandler surface) {
		return unscaleValue(scaledX, surface.getScaleFactor()) + surface.getRootLayer().getTransformX();
	}


}
