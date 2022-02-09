package net.sevenscales.editor.content.utils;

import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.dojo.Matrix;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.Point;

public class ScaleHelpers {
	public static native int scaleValue(int value, double scalefactor)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var i = m.invert(m.scale(scalefactor));
		return parseInt(i.xx * value);
	}-*/;
	
	public static native int unscaleValue(int value, double scalefactor)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var i = m.scale(scalefactor);
		return parseInt(i.xx * value);
	}-*/;


	public static native JavaScriptObject scaleCoordinate(int x, int y, double scalefactor)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var i = m.invert(m.scale(scalefactor));
		return {
			x : i.xx * x,
			y : i.yy * y
		};
	}-*/;

	public static native JavaScriptObject unscaleCoordinate(int x, int y, double scalefactor)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var i = m.scale(scalefactor);
		return {
			x : i.xx * x,
			y : i.yy * y
		};
	}-*/;
	
	public static class ScaledAndTranslatedPoint {
		public Point scaledAndTranslatedPoint;
		public MatrixPointJS scaledPoint;
	}
	public static ScaledAndTranslatedPoint scaleAndTranslateScreenpoint(int screenX, int screenY, ISurfaceHandler surface) {
		ScaledAndTranslatedPoint result = new ScaledAndTranslatedPoint();
		// scale point
		result.scaledAndTranslatedPoint = new Point();
		result.scaledPoint = MatrixPointJS.createScaledPoint(screenX, screenY, surface.getScaleFactor());
		
		// translate by root layer location
    Matrix matrix = surface.getMatrix();
		result.scaledAndTranslatedPoint.x = result.scaledPoint.getX() - ScaleHelpers.scaleValue(matrix.getDXInt(), surface.getScaleFactor()); 
		result.scaledAndTranslatedPoint.y = result.scaledPoint.getY() - ScaleHelpers.scaleValue(matrix.getDYInt(), surface.getScaleFactor());
		return result;
	}

	public static Point diagramPositionToScreenPoint(
    Diagram d,
    ISurfaceHandler surface,
    boolean center
  ) {

    int valueX = center ? d.getCenterX() : d.getLeft();
    int valueY = center ? d.getCenterY() : d.getTop();

    Matrix matrix = surface.getMatrix();
		int left = ScaleHelpers.unscaleValue(surface.getAbsoluteLeft() + valueX, surface.getScaleFactor()) + 
				matrix.getDXInt(); 
		int top = ScaleHelpers.unscaleValue(surface.getAbsoluteTop() + valueY, surface.getScaleFactor()) + 
				matrix.getDYInt();
		return new Point(left, top);
	}
	
	public static int scaleAndTranslateY(int scaledY, ISurfaceHandler surface) {
		return unscaleValue(scaledY, surface.getScaleFactor()) + surface.getMatrix().getDXInt();
	}

	public static int scaleAndTranslateX(int scaledX, ISurfaceHandler surface) {
		return unscaleValue(scaledX, surface.getScaleFactor()) + surface.getMatrix().getDYInt();
	}


}
