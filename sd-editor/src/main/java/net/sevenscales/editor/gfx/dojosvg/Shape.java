package net.sevenscales.editor.gfx.dojosvg;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IShape;

import com.google.gwt.core.client.JavaScriptObject;

abstract class Shape extends Graphics implements IShape {
	private static final SLogger logger = SLogger.createLogger(Shape.class);
	
	private JavaScriptObject stroke;
	private JavaScriptObject fill;
	private boolean visibility = true;
	private int rotateDegree;
	private int svgdx;
	private int svgdy;
	private boolean themeSupported = true;
	
	final public void setStroke(String color) {
		if (visibility) {
			if (!"transparent".equals(color)) {
	//			nativeSetStrokeColor(color, rawNode);
				color = color.length() > 6 ? color.substring(1, 7) : color; 
				int red = Integer.valueOf(color.substring(0, 2), 16);
				int green = Integer.valueOf(color.substring(2, 4), 16);
				int blue = Integer.valueOf(color.substring(4, 6), 16);
				nativeSetStroke(rawNode, red, green, blue, 1);
			} else {
				nativeSetStroke(rawNode, 0xff, 0xff, 0xff, 0);
			}
		}
	}
	
	final public void setStroke(int red, int green, int blue, double opacity) {
//		nativeSetStroke(rawNode, red, green, blue, opacity);
		nativeSetStroke(rawNode, red, green, blue, opacity, 3);
	}
	
	final public void setStroke(int red, int green, int blue, double opacity, double width) {
		nativeSetStroke(rawNode, red, green, blue, opacity, width);
	}
	
  private native void nativeSetStrokeColor(String color, JavaScriptObject rawNode)/*-{
	  var s = rawNode.getStroke();
	  if (s == null)
	    s = {};
	  s.color = color;
	  rawNode.setStroke(s);
	}-*/;
	
	
	private native void nativeSetStroke(
			JavaScriptObject rawNode, int red, int green, int blue, double opacity)/*-{
		var color = new $wnd.dojox.color.Color( {r:red, g:green, b:blue, a: opacity} );
//		var stroke = rawNode.getStroke();
//		stroke.color = color;
//		if (stroke != null) {
//			color.width = stroke.width;
//		}
//		rawNode.setStroke(color);
	  var s = rawNode.getStroke();
	  if (s == null)
	    s = {};
	  s.color = color;
	  rawNode.setStroke(s);

				
//		var stroke = rawNode.getStroke();
//		if (stroke != null) {
//			var color = new $wnd.dojox.color.Color( {r:red,g:green,b:blue, a: opacity} );
//			stroke.r = red;
//			stroke.g = green;
//			stroke.b = blue;
//			stroke.a = opacity;
//			rawNode.setStroke(stroke);
//			console.log("color set");
//		} else {
//			var color = new $wnd.dojox.color.Color( {r:red,g:green,b:blue} );
//			rawNode.setStroke(color);
//		}
	}-*/;
	
	private native void nativeSetStroke(
			JavaScriptObject rawNode, int red, int green, int blue, double opacity, double width)/*-{
		var color = new $wnd.dojox.color.Color( {r:red,g:green,b:blue} );
		color.a = opacity;
		rawNode.setStroke(color);
	}-*/;

  final public void setStrokeWidth(double width) {
	  nativeSetStrokeWidth(rawNode, Double.toString(width));
	}
  private native void nativeSetStrokeWidth(JavaScriptObject rawNode, String width)/*-{
//  	node.getStroke().width = width;
	  var s = rawNode.getStroke();
	  if (s == null)
	    s = {};
	  s.width = width;
	  rawNode.setStroke(s);
	}-*/;
  
  @Override
  final public double getStrokeWidth() {
  	return _getStrokeWidth(rawNode);
  }
  private native double _getStrokeWidth(JavaScriptObject rawNode)/*-{
  	if (rawNode.getStroke() != null) { 
  		return parseInt(rawNode.getStroke().width);
  	}
  	return 0;
  }-*/;
	
  final public JavaScriptObject getStroke() {
		return nativeGetStroke(rawNode);
	}

	private native JavaScriptObject nativeGetStroke(JavaScriptObject rawNode)/*-{
		return rawNode.getStroke();
	}-*/;
	
	final public Color getStrokeColor() {
	  JavaScriptObject raw = getStroke();
	  if (raw == null) {
	    return null;
	  }
	  return getColor(raw);
	}

	public void setStrokeCap(String value) {
		_setStrokeCap(rawNode, value);
	}
	private native void _setStrokeCap(JavaScriptObject rawNode, String value)/*-{
		var stroke = rawNode.getStroke()
		if (stroke != null) {
			stroke.cap = value;
		} else {
			rawNode.setStroke({cap:value});
		}
	}-*/;
  public String getStrokeCap() {
		return _getStrokeCap(rawNode);
  }
  private native String _getStrokeCap(JavaScriptObject rawNode)/*-{
  	return rawNode.getStroke().cap;
  }-*/;

	private native double getOpacity(JavaScriptObject rawNode)/*-{
		if (rawNode.color != null) {
  		return rawNode.color.a;
  	}
	  return rawNode.a;
  }-*/;
  private native int getBlue(JavaScriptObject rawNode)/*-{
  	if (rawNode.color != null) {
  		return rawNode.color.b;
  	}
    return rawNode.b;
  }-*/;
  private native int getGreen(JavaScriptObject rawNode)/*-{
  	if (rawNode.color != null) {
  		return rawNode.color.g;
  	}
    return rawNode.g;
  }-*/;
  private native int getRed(JavaScriptObject rawNode)/*-{
  	if (rawNode.color != null) {
  		return rawNode.color.r;
  	}
    return rawNode.r;
  }-*/;
  
  private Color getColor(JavaScriptObject raw) {
	  Color result = new Color();
	  if (raw != null) {
		  result.red = getRed(raw);
		  result.green = getGreen(raw);
		  result.blue = getBlue(raw);
		  result.opacity = getOpacity(raw);
	  }
	  return result;
  };

	final public void setFill(String color) {
		if (visibility)
			nativeSetFill(rawNode, color);
	}

	final public void setFill(int red, int green, int blue, double opacity) {
		nativeSetFill(rawNode, red, green, blue, opacity);
	}
	
	final public Color getFillColor() {
	  JavaScriptObject fill = getFill();
	  return getColor(fill);
	}
  final public JavaScriptObject getFill() {
		return nativeGetFill(rawNode);
	}
	private native JavaScriptObject nativeGetFill(JavaScriptObject rawNode)/*-{
		return rawNode.getFill();
	}-*/;
	
	private native void nativeSetFill(JavaScriptObject rawNode, String color)/*-{
		rawNode.setFill( color );
	}-*/;
	
	private native void nativeSetFill(
			JavaScriptObject rawNode, int red, int green, int blue, double opacity)/*-{
		var color = new $wnd.dojo.Color( {r:red,g:green,b:blue,a:opacity} );
		rawNode.setFill(color);
	}-*/;
	
	final public JavaScriptObject getRawNode() {
		return rawNode;
	}
	
	final public void moveToBack() {
    moveToBack(rawNode);
  }
  	  
  private native void moveToBack(JavaScriptObject rawNode)/*-{
    rawNode.moveToBack();
  }-*/;

  final public void moveToFront() {
    moveToFront(rawNode);
  }
      
  private native void moveToFront(JavaScriptObject rawNode)/*-{
    rawNode.moveToFront();
  }-*/;
	
	final public void applyTransform(int dx, int dy) {
		nativeApplyTransform(rawNode, dx, dy);
	}
	
	final public int getDX() {
		return _getDX(rawNode);
	}
	
	private native int _getDX(JavaScriptObject rawNode)/*-{
		if (rawNode.getTransform() != null) {
			return rawNode.getTransform().dx;
		}
		return 0; 
	}-*/;

	final public int getDY() {
		return _getDY(rawNode);
	}
	private native int _getDY(JavaScriptObject rawNode)/*-{
		if (rawNode.getTransform() != null) {
			return rawNode.getTransform().dy;
		}
		return 0; 
	}-*/;

	public void setTranslate(double x, double y) {
		_setTranslate(rawNode, x, y);
	}
	private native void _setTranslate(JavaScriptObject rawNode, double x, double y)/*-{
		var m = $wnd.dojox.gfx.matrix;
		rawNode.applyTransform(m.translate(x, y));
	}-*/;

	public void setMatrix(double xx, double xy, double yx, double yy, double dx, double dy) {
		_setMatrix(rawNode, xx, xy, yx, yy, dx, dy);
	}
	private native void _setMatrix(JavaScriptObject rawNode, double xx, double xy, double yx, double yy, double dx, double dy)/*-{
		var m = $wnd.dojox.gfx.matrix;
		m.xx = xx;
		m.xy = xy;
		m.yx = yx;
		m.yy = yy;
		m.dx = dx;
		m.dy = dy;
		rawNode.applyTransform(m);
	}-*/;

	public void setScale(double xx, double yy) {
		_setScale(rawNode, xx, yy);
	}
	private native void _setScale(JavaScriptObject rawNode, double xx, double yy)/*-{
		var m = $wnd.dojox.gfx.matrix;
		rawNode.applyTransform(m.scale(xx, yy));
	}-*/;	
	
	final public boolean isVisible() {
	  return visibility;
	}
	
	final public void setVisibility(boolean visibility) {
		this.visibility = visibility;
		String visibilityValue = visibility ? "visible" : "hidden";
		nativeSetVisibility(rawNode, visibilityValue);
	}
	
	private native void nativeSetVisibility(JavaScriptObject rawNode, String visibility)/*-{
//		if ($wnd.dojo._hasResource["dojox.gfx.svg"]) {
//	  	rawNode.rawNode.setAttribute('display', visibility);
		rawNode.rawNode.style.visibility = visibility; 
//		} 
	}-*/;

	
//	public void setVisibility(boolean visibility) {
//	  this.visibility = visibility;
//	  nativeSetVisibility(rawNode, visibility);
//	}
//  public native void nativeSetVisibility(JavaScriptObject rawNode, boolean visibility)/*-{
//    if (visibility) {
//      rawNode.rawNode.visibility = "Visible";
//    } else {
//      rawNode.rawNode.visibility = "Collapsed";
//    }
//  }-*/;
	
  private native void nativeApplyTransform(JavaScriptObject rawNode, 
      int dx, int dy)/*-{
    rawNode.applyTransform( {dx:dx, dy:dy} );
  }-*/;
	
	private native void nativeSetStroke(JavaScriptObject rawNode, JavaScriptObject stroke)/*-{
		rawNode.setStroke(stroke);
	}-*/;
	private native void nativeSetFill(JavaScriptObject rawNode, JavaScriptObject fill)/*-{
		rawNode.setFill(fill);
	}-*/;
	
	@Override
	final public void setAttribute(String name, String value) {
		setAttribute(rawNode, name, value);
	}
	
	private native void setAttribute(JavaScriptObject rawNode, String name, String value)/*-{
		rawNode.rawNode.setAttribute(name, value);
	}-*/;
	
	public String getAttribute(String name) {
		return _getAttribute(rawNode, name);
	}
	private native String _getAttribute(JavaScriptObject rawNode, String name)/*-{
		return rawNode.rawNode.getAttribute(name);
	}-*/;

	@Override
	public void rotatedxdy(int degree, int x, int y, int dx, int dy) {
		this.rotateDegree = degree;
		_rotate(rawNode, degree, x, y, dx, dy);
	}
	
	@Override
	public void rotate(int degree, int x, int y) {
		this.rotateDegree = degree;
		_rotate(rawNode, degree, x, y);
	}
	private native void _rotate(JavaScriptObject rawNode, int degree, int a, int b)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var r = m.rotategAt(degree, a, b);
		rawNode.applyTransform(r);
	}-*/;

	private native void _rotate(JavaScriptObject rawNode, int degree, int centerX, int centerY, int translateX, int translateY)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var r = m.multiply(m.rotategAt(degree, centerX, centerY), m.translate(translateX, translateY));
		rawNode.setTransform(r);
	}-*/;
	
	/**
	* Currently supports only rotation
	*/
	public String getTransformMatrix() {
		if (rotateDegree != 0) {
			return _getTransformMatrix(rawNode);
		} else {
			return null;
		}
	}

	private native String _getTransformMatrix(JavaScriptObject rawNode)/*-{
		var t = rawNode.getTransform();
		if (t) {
			// {xx: 1, xy: 0, yx: 0, yy: 1, dx: 0, dy: 0}
			// currently supports only rotation and transformation; uses parent group xx and yy values
			// therefore 0 (current understanding...)
			return 'matrix(' + 0 + ',' + t.yx +',' + t.xy + ',' + 0 + ',' + t.dx + ',' + t.dy + ')';
		}
		return null;
	}-*/;

	public void rotateg(int degree) {
		_rotateg(rawNode, degree);
	}
	private native void _rotateg(JavaScriptObject rawNode, int degree)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var r = m.rotateg(degree);
		rawNode.applyTransform(r);
	}-*/;
	
	public void skewg(int skew) {
		_skewg(rawNode, skew);
	}
	
	private native void _skewg(JavaScriptObject rawNode, int skew)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var r = m.skewXg(skew);
		rawNode.applyTransform(r);
	}-*/;
	
	@Override
	public int getRotateDegree() {
		return rotateDegree;
	}
	
	@Override
	public void unrotate(int degree, int x, int y) {
		this.rotateDegree = 0;
		_unrotate(rawNode, degree, x, y);
	}
	private native void _unrotate(JavaScriptObject rawNode, int degree, int a, int b)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var r = m.invert(m.rotategAt(degree, a, b));
		rawNode.applyTransform(r);
	}-*/;
	
	@Override
	public void resetAllTransforms() {
		_resetAllTransforms(rawNode);
	}
	private native void _resetAllTransforms(JavaScriptObject rawNode)/*-{
		var m = $wnd.dojox.gfx.matrix;
		rawNode.setTransform(m.identity);
	}-*/;
	
	@Override
	public void remove() {
		_remove(rawNode);
	}
  private native void _remove(JavaScriptObject rawNode)/*-{
		rawNode.removeShape();
	}-*/;
  
  @Override
  public void setSvgFixX(int dx) {
  	this.svgdx = dx;
  }
  public int getSvgFixX() {
		return svgdx;
	}
  
  @Override
  public void setSvgFixY(int dy) {
  	this.svgdy = dy;
  }
  @Override
  public int getSvgFixY() {
  	return svgdy;
  }
  
	public void setStyle(String style) {
		_setStyle(rawNode, style);
	}
	private native void _setStyle(JavaScriptObject rawNode, String style)/*-{
		rawNode.getStroke().style = style;
	}-*/;
	
	public String getStyle() {
	  return _getStyle(rawNode);
	}
  private native String _getStyle(JavaScriptObject rawNode)/*-{
    if (rawNode.getStroke()) {
      return rawNode.getStroke().style;
    }
    return "";
  }-*/;

  public boolean isThemeSupported() {
  	return themeSupported;
  }
  public void setSupportsTheme(boolean themeSupported) {
  	this.themeSupported = themeSupported;
  }
	
}
