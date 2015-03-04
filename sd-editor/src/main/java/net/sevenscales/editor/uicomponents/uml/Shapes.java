package net.sevenscales.editor.uicomponents.uml;

import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.utils.SLogger;


public class Shapes {
	private static final SLogger logger = SLogger.createLogger(Shapes.class);

	static final String STROKE_LINE_ROUND = "stroke-linejoin:round;";
	static final String STROKE_LINECAP_ROUND = "stroke-linecap:round;";
	static final String FILL_BACKGROUND = "fill:bgcolor;";
	static final String FILL_BORDER = "fill:bordercolor;";
	static final String STROKE_NONE = "stroke:none;";

	static {
		SLogger.addFilter(Shapes.class);
	}


	public static class Matrix {
		public double a;
		public double b;
		public double c;
		public double d;
		public double e;
		public double f;

		public Matrix(double a, double b, double c, double d, double e, double f) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
			this.e = e;
			this.f = f;
		}
	}

	public static class JsPathData extends JavaScriptObject {
		protected JsPathData() {
		}

	  public final native String getCode()/*-{
	  	return this.code;
	  }-*/;
	  public final native String getCommand()/*-{
	  	return this.command;
	  }-*/;
	  public final native double getX()/*-{
	  	return this.x;
	  }-*/;
	  public final native double getY()/*-{
	  	return this.y;
	  }-*/;
	  public final native boolean isRelative()/*-{
	  	return this.relative;
	  }-*/;
	  public final native double getX1()/*-{
	  	return this.x1;
	  }-*/;
	  public final native double getY1()/*-{
	  	return this.y1;
	  }-*/;
	  public final native double getX2()/*-{
	  	return this.x2;
	  }-*/;
	  public final native double getY2()/*-{
	  	return this.y2;
	  }-*/;

	  // A command support
	  public final native double getRX()/*-{
	  	return this.rx;
	  }-*/;
	  public final native double getRY()/*-{
	  	return this.ry;
	  }-*/;
	  public final native int getXRotation()/*-{
	  	return this.xAxisRotation;
	  }-*/;
	  public final native int getLargeArc()/*-{
	  	return this.largeArc ? 1 : 0;
	  }-*/;
	  public final native int getSweep()/*-{
	  	return this.sweep ? 1 : 0;
	  }-*/;

	  public final String toPath(double factorX, double factorY) {
	  	String result = "";
	  	String code = getCode().toLowerCase();
	  	if (isRelative()) {
	  		result = code;
	  	} else {
	  		// some bug in parser and this is uppercase...
	  		// at least chrome doesn't seem to care about that
	  		// still to lower case as in spec if some browser doesn't work like that...
	  		result = code;
	  	}

	  	if ("m".equals(code)) {
				result += (getX() * factorX) + "," + (getY() * factorY);
	  	} else if ("l".equals(code)) {
				result += (getX() * factorX) + "," + (getY() * factorY);
	  	} else if ("c".equals(code)) {
		  	result += (getX1() * factorX) + "," + (getY1() * factorY) + " ";
		  	result += (getX2() * factorX) + "," + (getY2() * factorY) + " ";
				result += (getX() * factorX) + "," + (getY() * factorY);
	  	} else if ("s".equals(code)) {
		  	result += (getX2() * factorX) + "," + (getY2() * factorY) + " ";
				result += (getX() * factorX) + "," + (getY() * factorY);
	  	} else if ("q".equals(code)) {
		  	result += (getX1() * factorX) + "," + (getY1() * factorY) + " ";
				result += (getX() * factorX) + "," + (getY() * factorY);
	  	} else if ("a".equals(code)) {
		  	result += (getRX() * factorX) + "," + (getRY() * factorY) + " " +
		  						getXRotation() + " " + getLargeArc() + " " + getSweep() + " ";
				result += (getX() * factorX) + "," + (getY() * factorY);
	  	} else if ("v".equals(code)) {
		  	result += (getY() * factorY);
	  	} else if ("h".equals(code)) {
		  	result += (getX() * factorY);
	  	}
	  	// else if (!"z".equals(code)) {
		  // 	result += (getX() * factorX) + "," + (getY() * factorY);
	  	// }
	  	// if ("m".equals(getCode())) {
		  // 	result += getX() + "," + getY();
	  	// } else if ("l".equals(getCode())) {
	  	// 	result += getX() + "," + getY();
	  	// } else if ("c".equals(getCode())) {
	  	// 	result += getX1() + "," + getY1() + " " + getX2() + "," + getY2() + " " + getX() + "," + getY();
	  	// }
	  	return result;
	  }

	  public final String toPathMove(int moveX, int moveY) {
	  	String code = getCode().toLowerCase();
	  	String result = code;
	  	if ("c".equals(code)) {
		  	result += (getX1()) + "," + (getY1()) + " ";
		  	result += (getX2()) + "," + (getY2()) + " ";
	  	}

	  	if ("a".equals(code)) {
		  	result += (getRX()) + "," + (getRY()) + " " +
		  						getXRotation() + " " + getLargeArc() + " " + getSweep() + " ";
	  	}

	  	if ("v".equals(code)) {
		  	result += (getY());
	  	}
	  	if ("h".equals(code)) {
		  	result += (getX());
	  	}
	  	else if (!"z".equals(code) && !"m".equals(code)) {
		  	result += (getX()) + "," + (getY());
	  	}

	  	if ("m".equals(code)) {
		  	result += (getX() + moveX) + "," + (getY() + moveY);
	  	}
	  	return result;
	  }

	}

 // <g transform="translate(7.7738567e-7,-308.27059)">
 //  <path opacity="0.85500004" d="m83.703402,82.745628c0,9.906024-10.913134,17.936442-24.375166,17.936442-13.462033,0-24.375166-8.030418-24.375166-17.936442s10.913133-17.936443,24.375166-17.936443c13.462032,0,24.375166,8.030419,24.375166,17.936443z" transform="matrix(0.98649777,0,0,0.92248204,-33.527173,249.43923)" stroke="#000" stroke-miterlimit="4" stroke-dasharray="none" stroke-width="2" fill="none"/>
 // </g>


	public static ShapeGroup get(String elementType, boolean sketch) {
		return ShapeCache.get(elementType, sketch);
	}

	// public static ShapeGroup get(ElementType type, boolean sketch) {
	// 	return ShapeCache.get(type, sketch);
	// }

	public static ShapeGroup getSketch(String type) {
		return ShapeCache.getSketch(type);
	}
}