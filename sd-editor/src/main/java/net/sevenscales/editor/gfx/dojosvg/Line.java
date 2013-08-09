package net.sevenscales.editor.gfx.dojosvg;

import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.ILine;

import com.google.gwt.core.client.JavaScriptObject;

class Line extends Shape implements ILine {
	Line(IContainer container) {
		rawNode = createLine(container.getContainer());
	}

	Line(IContainer container, int x1, int y1, int x2, int y2) {
		rawNode = createLine(container.getContainer());
		setShape(x1, y1, x2, y2);
	}
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ILine#getX1()
   */
	public int getX1() {
		return getX1(rawNode);
	}
	public native int getX1(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().x1);
	}-*/;
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ILine#getY1()
   */
	public int getY1() {
		return getY1(rawNode);
	}
	public native int getY1(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().y1);
	}-*/;

	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ILine#getX2()
   */
	public int getX2() {
		return getX2(rawNode);
	}
	public native int getX2(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().x2);
	}-*/;
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ILine#getY2()
   */
	public int getY2() {
		return getY2(rawNode);
	}
	public native int getY2(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().y2);
	}-*/;

	private native JavaScriptObject createLine(JavaScriptObject surface)/*-{
		return surface.createLine();
	}-*/;

	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ILine#setShape(int, int, int, int)
   */
	public void setShape(int x1, int y1, int x2, int y2) {
		setShape(rawNode, x1, y1, x2, y2);
	}
	private native void setShape(JavaScriptObject rawNode, int x1, int y1, int x2, int y2)/*-{
    	rawNode.setShape( {x1:x1, y1:y1, x2:x2, y2:y2} );
	}-*/;
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ILine#setStyle(java.lang.String)
   */
	public void setStyle(String style) {
		setStyle(rawNode, style);
	}
	private native void setStyle(JavaScriptObject rawNode, String style)/*-{
		rawNode.setStroke({style:style});
	}-*/;
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.ILine#getStyle()
   */
	public String getStyle() {
	  return nativeGetStyle(rawNode);
	}
	private native String nativeGetStyle(JavaScriptObject rawNode)/*-{
    return rawNode.getStroke().style;
	}-*/;
	  
  public void applyTransformToShape(int dx, int dy) {
  	setShape(getX1() + dx, getY1() + dy, getX2() + dx, getY2() + dy);
  }

}
