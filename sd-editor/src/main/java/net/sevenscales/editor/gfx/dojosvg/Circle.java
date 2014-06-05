package net.sevenscales.editor.gfx.dojosvg;

import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.IContainer;

import com.google.gwt.core.client.JavaScriptObject;

class Circle extends Shape implements ICircle {
  Circle(IContainer container) {
    rawNode = nativeCreateCircle(container.getContainer());
  }
  
  Circle(Surface surface) {
    rawNode = nativeCreateCircle(surface.getContainer());
  }
  
	Circle(Surface surface, int rx, int ry, int radius) {
		rawNode = nativeCreateCircle(surface.getContainer(), rx, ry, radius);
	}
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ICircle#setShape(int, int, int)
   */
	public void setShape(int cx, int cy, int radius) {
		nativeSetShape(rawNode, cx, cy, radius);
	}
	public void setShape(double cx, double cy, int radius) {
		nativeSetShape(rawNode, cx, cy, radius);
	}
	
	private native void nativeSetShape(JavaScriptObject circle, double cx, double cy, int r)/*-{
		circle.setShape( {cx:cx, cy:cy, r:r} );
	}-*/;
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ICircle#getX()
   */
	public int getX() {
		return nativeGetX(rawNode);
	}
	
	public native int nativeGetX(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().cx);
	}-*/;
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ICircle#getY()
   */
	public int getY() {
		return nativeGetY(rawNode);
	}
	public native int nativeGetY(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().cy);
	}-*/;
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ICircle#getRadius()
   */
	public int getRadius() {
		return nativeGetRadius(rawNode);
	}
	public native int nativeGetRadius(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().r);
	}-*/;
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ICircle#setRadius(int)
   */
	public void setRadius(int radius) {
	  setShape(getX(), getY(), radius);
//	  nativeSetRadius(rawNode, radius);
	}
	private native void nativeSetRadius(JavaScriptObject rawNode, int radius)/*-{
	  s = rawNode.getShape();
    rawNode.setShape( {cx:s.cx, cy:s.cy, r:radius} );
	}-*/;
	
//	@Override
//	public void applyTransform(int dx, int dy) {
//		int cx = getX();
//		int cy = getY();
//		int radius = getRadius();
//		setShape(cx + dx, cy + dy, radius);
//	}

  private static native JavaScriptObject nativeCreateCircle
    (JavaScriptObject surface)/*-{
//    var circleInfo = { };
    return surface.createCircle();
  }-*/;

  private static native JavaScriptObject nativeCreateCircle
    (JavaScriptObject surface, int circleX, int circleY, int radius)/*-{
    var circleInfo = { cx: circleX, cy: circleY, r: radius };
    return surface.createCircle(circleInfo);
  }-*/;
	
	@Override
	public void applyTransformToShape(int dx, int dy) {
		setShape(getX() + dx, getY() + dy, getRadius());
	}

}
