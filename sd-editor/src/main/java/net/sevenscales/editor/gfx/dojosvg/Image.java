package net.sevenscales.editor.gfx.dojosvg;

import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IImage;

public class Image extends Shape implements IImage {

	public Image(IContainer container, int x, int y, int width, int height, String src) {
		rawNode = createImage(container.getContainer(), x, y, width, height, src);
		// assert(rawNode != null);
	}
	
	private native JavaScriptObject createImage(JavaScriptObject surface, int x, int y, int width, int height, String src)/*-{
		var result = surface.createImage({x: x, y: y, width: width, height: height, src: src});
//		var m = $wnd.dojox.gfx.matrix;
		
//		result.setTransform([m.rotateg(45), m.skewXg(30)]);
//		result.applyTransform([m.rotateg(45), m.skewXg(30)]);
		return result;
	}-*/;

	
	@Override
	public void applyTransformToShape(int dx, int dy) {
		setShape(getX() + dx, getY() + dy, getWidth(), getHeight());
	}

	@Override
	public void setShape(int x, int y, int width, int height) {
		_setShape(rawNode, x, y, width, height);
	}
	private native void _setShape(JavaScriptObject rawNode, int x, int y, int width, int height)/*-{
		rawNode.setShape({x: x, y: y, width: width, height: height});
	}-*/;

	@Override
	public void setXY(int x, int y) {
		_setShape(rawNode, x, y, getWidth(), getHeight());
	}

	@Override
	public void setClipCircle(int x, int y, int r) {
		_setClipCircle(rawNode, x, y, r);
	}

	private native void _setClipCircle(JavaScriptObject rawNode, int x, int y, int r)/*-{
		rawNode.setClip({cx:x, cy:y, rx:r, ry:r});
	}-*/;

	@Override
	public int getX() {
		return _getX(rawNode);
	}
	private native int _getX(JavaScriptObject rawNode)/*-{
		return rawNode.getShape().x;
	}-*/;
	@Override
	public void setX(int x) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getY() {
		return _getY(rawNode);
	}
	private native int _getY(JavaScriptObject rawNode)/*-{
		return rawNode.getShape().y;
	}-*/;
	@Override
	public void setY(int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getWidth() {
		return _getWidth(rawNode);
	}
	private native int _getWidth(JavaScriptObject rawNode)/*-{
		return rawNode.getShape().width;
	}-*/;

	@Override
	public void setWidth(int width) {
		_setWidth(rawNode, width);
	}
	private native void _setWidth(JavaScriptObject rawNode, int width)/*-{
		rawNode.getShape().width = width;
	}-*/;

	@Override
	public int getHeight() {
		return _getHeight(rawNode);
	}
	private native int _getHeight(JavaScriptObject rawNode)/*-{
		return rawNode.getShape().height;
	}-*/;

	@Override
	public void setHeight(int height) {
		_setHeight(rawNode, height);
	}
	private native void _setHeight(JavaScriptObject rawNode, int height)/*-{
		rawNode.getShape().height = height;
	}-*/;

	@Override
	public void setSrc(String src) {
		_setSrc(rawNode, src);
	}
	private native void _setSrc(JavaScriptObject rawNode, String src)/*-{
		rawNode.getShape().src = src;
	}-*/;

	@Override
	public String getSrc() {
		return _getSrc(rawNode);
	}
	private native String _getSrc(JavaScriptObject rawNode)/*-{
		return rawNode.getShape().src;
	}-*/;
	
	@Override
	public void setShape(int x, int y, int width, int height, String src) {
		_setShape(rawNode, x, y, width, height, src);
	}
	private native void _setShape(JavaScriptObject rawNode, int x, int y, int width, int height, String src)/*-{
		rawNode.setShape({x: x, y: y, width: width, height: height, src: src});
	}-*/;

}
