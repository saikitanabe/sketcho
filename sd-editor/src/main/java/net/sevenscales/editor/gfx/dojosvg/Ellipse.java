package net.sevenscales.editor.gfx.dojosvg;

import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IEllipse;

import com.google.gwt.core.client.JavaScriptObject;

class Ellipse extends Shape implements IEllipse {
	Ellipse(IContainer container) {
		rawNode = createEllipse(container.getContainer());
	}
	
	Ellipse(Surface surface) {
		rawNode = createEllipse(surface.getContainer());
	}

	Ellipse(Surface surface, int cx, int cy, int rx, int ry) {
		rawNode = createEllipse(surface.getContainer());
		setShape(cx, cy, rx, ry);
	}
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IEllipse#setShape(int, int, int, int)
   */
	public void setShape(int cx, int cy, int rx, int ry) {
		setShape(rawNode, cx, cy, rx, ry);
	}
	
	private native void setShape(JavaScriptObject ellipse, int cx, int cy, int rx, int ry)/*-{
		ellipse.setShape( {cx:cx, cy:cy, rx:rx, ry:ry} );
	}-*/;
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IEllipse#getCx()
   */
	public int getCx() {
		return nativeGetCx(rawNode);
	}	
	private native int nativeGetCx(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().cx);
	}-*/;
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IEllipse#getCy()
   */
	public int getCy() {
		return nativeGetCy(rawNode);
	}
	private native int nativeGetCy(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().cy);
	}-*/;
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IEllipse#getRx()
   */
	public int getRx() {
		return nativeGetRx(rawNode);
	}
	public native int nativeGetRx(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().rx);
	}-*/;
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IEllipse#getRy()
   */
	public int getRy() {
		return nativeGetRy(rawNode);
	}
	public native int nativeGetRy(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().ry);
	}-*/;
	
//	public int getWidth() {
//		return getRx() * 2;
//	}		
//	public int getHeight() {
//		return getRy() * 2;
//	}		
	
  private static native JavaScriptObject createEllipse
    (JavaScriptObject surface)/*-{
    return surface.createEllipse();
  }-*/;

	@Override
	public void applyTransformToShape(int dx, int dy) {
		int cx = getCx();
		int cy = getCy();
		int rx = getRx();
		int ry = getRy();
		setShape(cx + dx, cy + dy, rx, ry);
	}
	
}
