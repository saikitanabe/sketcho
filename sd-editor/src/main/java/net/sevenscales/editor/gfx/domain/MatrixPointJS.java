package net.sevenscales.editor.gfx.domain;

import net.sevenscales.editor.content.utils.ScaleHelpers;

import com.google.gwt.core.client.JavaScriptObject;

public class MatrixPointJS extends JavaScriptObject {
	  // Overlay types always have protected, zero argument constructors.
	  protected MatrixPointJS() {}

	  // JSNI methods to get data
	  public final native int getX() /*-{ return this.x; }-*/;
	  public final native int getY() /*-{ return this.y; }-*/;
	  // screen points
	  public final native int getScreenX() /*-{ return this.sx; }-*/;
	  public final native int getScreenY() /*-{ return this.sy; }-*/;
	  
	  public final native float getScaleFactor() /*-{ return this.scaleFactor; }-*/;
	  
	  
	  public final native int getDX() /*-{ return this.dx; }-*/;
	  public final native int getDY() /*-{ return this.dy; }-*/;
	  public final native int getScreenDX() /*-{ return this.sdx; }-*/;
	  public final native int getScreenDY() /*-{ return this.sdy; }-*/;
	  
	  // guarantees that using only scaled points...
	  public static MatrixPointJS createScaledPoint(int x, int y, float scaleFactor) {
			JavaScriptObject norm = ScaleHelpers.scaleCoordinate(x, y, scaleFactor);
	  	MatrixPointJS result = _create(x(norm), y(norm), x, y, 0, 0, 0, 0, scaleFactor);
	  	return result;
	  }
	  
	  public static MatrixPointJS createUnscaledPoint(int x, int y, float scaleFactor) {
			JavaScriptObject norm = ScaleHelpers.unscaleCoordinate(x, y, scaleFactor);
	  	MatrixPointJS result = _create(x(norm), y(norm), x, y, 0, 0, 0, 0, scaleFactor);
	  	return result;
	  }

	  /**
	   * 
	   * @param dx screen dx
	   * @param dy screen dy
	   * @param scaleFactor
	   * @return
	   */
	  public static MatrixPointJS createScaledTransform(int dx, int dy, float scaleFactor) {
			JavaScriptObject scaled = ScaleHelpers.scaleCoordinate(dx, dy, scaleFactor);
	  	MatrixPointJS result = _create(0, 0, 0, 0, x(scaled), y(scaled), dx, dy, scaleFactor);
	  	return result;
	  }
	  
	  // no difference between dx, dy, sdx, sdy
	  public static MatrixPointJS createScreenTransform(int dx, int dy) {
	  	MatrixPointJS result = _create(0, 0, 0, 0, dx, dy, dx, dy, 1.0f);
	  	return result;
	  }
	  
	  private native static MatrixPointJS _create(int x, int y, int sx, int sy, int dx, int dy, int sdx, int sdy, float scaleFactor)/*-{
	  	return {x: x, y: y, sx: sx, sy: sy, dx: dx, dy: dy, sdx: sdx, sdy: sdy, scaleFactor: scaleFactor};
	  }-*/;
	  
		private static native int x(JavaScriptObject norm)/*-{
			return parseInt(norm.x);
		}-*/;
		
		private static native int y(JavaScriptObject norm)/*-{
			return parseInt(norm.y);
		}-*/;
	  
	}