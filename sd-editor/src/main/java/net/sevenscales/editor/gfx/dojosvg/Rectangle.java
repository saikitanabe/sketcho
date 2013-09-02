package net.sevenscales.editor.gfx.dojosvg;

import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.Rect;

import com.google.gwt.core.client.JavaScriptObject;


public class Rectangle extends Shape implements IRectangle {
	protected Rectangle(IContainer container) {
		rawNode = createRect(container.getContainer());
		// assert(rawNode != null);
	}
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IRectangle#setShape(net.sevenscales.editor.gfx.dojo.Rect)
   */
	public void setShape(Rect rect) {
		setShape(rawNode, rect.x, rect.y, rect.width, rect.height, rect.r);
	}
	
	@Override
	public void setShape(int x, int y, int width, int height, int radius) {
		setShape(rawNode, x, y, width, height, radius);
	}
	
	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IRectangle#getX()
   */
	public int getX() {
		return getX(rawNode);
	}
	public native int getX(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().x);
	}-*/;

	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IRectangle#getY()
   */
	public int getY() {
		return getY(rawNode);
	}
	public native int getY(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().y);
	}-*/;

	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IRectangle#getWidth()
   */
	public int getWidth() {
		return getWidth(rawNode);
	}
	public native int getWidth(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().width);
	}-*/;

	/* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IRectangle#getHeight()
   */
	public int getHeight() {
		return getHeight(rawNode);
	}
	public native int getHeight(JavaScriptObject rawNode)/*-{
		return parseInt(rawNode.getShape().height);
	}-*/;

	private native void setShape(JavaScriptObject rawNode, 
			int x, int y, int width, int height, int r)/*-{ 
		rawNode.setShape( {x:x, y:y, width:width, height:height, r:r} );
	}-*/;
	
	private native JavaScriptObject createRect(JavaScriptObject surface)/*-{
		return surface.createRect();
	}-*/;
	
		@Override
		public void setRadius(int radius) {
			setRadius(rawNode, radius);
		}
		private native void setRadius(JavaScriptObject rawNode, int radius)/*-{
//			rawNode.setShape( {r:radius} );
		}-*/;
		

		@Override
		public int getRadius() {
			return getRadius(rawNode);
		}
		
		private native int getRadius(JavaScriptObject rawNode)/*-{
			return parseInt(rawNode.getShape().r);
		}-*/;
		
		@Override
		public void applyTransformToShape(int dx, int dy) {
			setShape(getX() + dx, getY() + dy, getWidth(), getHeight(), getRadius());
		}

}
