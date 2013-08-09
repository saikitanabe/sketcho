package net.sevenscales.editor.gfx.dojosvg;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShape;

import com.google.gwt.core.client.JavaScriptObject;

class Group extends Graphics implements IContainer, IGroup {
	private static final SLogger logger = SLogger.createLogger(Group.class);
	
  private JavaScriptObject group;
	private boolean visible = true;

  Group(IContainer container) {
    group = nativeCreateGroup(container.getContainer());
  }

  Group(Surface surface) {
    group = nativeCreateGroup(surface.getContainer());
  }
  
  public JavaScriptObject getContainer() {
    return group;
  }

  private native JavaScriptObject nativeCreateGroup(JavaScriptObject surface)/*-{
    return surface.createGroup();
  }-*/;
  
  public void setClip(int x, int y, int width, int height) {
    nativeSetClip(group, x, y, width, height);
  }
  
  public native void nativeSetClip(JavaScriptObject node, int x, int y, int width, int height)/*-{
//      var r = x + "," + y + "," + width + "," + height;
//      var geometry = node.rawNode.getHost().content.createFromXaml('<RectangleGeometry />');
//      geometry.rect = r;
//      node.rawNode.clip = geometry;
  }-*/;

  /* (non-Javadoc)
   * @see net.sevenscales.editor.gfx.dojo.IGroup#remove(net.sevenscales.editor.gfx.dojo.Shape)
   */
  public void remove(IShape shape) {
    remove(group, shape.getRawNode());
  }
  private native void remove(JavaScriptObject from, JavaScriptObject shape)/*-{
    from.remove(shape);
  }-*/;
  
  @Override
  public void remove() {
  	_remove(group);
  }
  private native void _remove(JavaScriptObject rawNode)/*-{
  	rawNode.removeShape();
  }-*/;
  
  public void applyTransform(int dx, int dy) {
    nativeApplyTransform(group, dx, dy);
  }
  
  public void resetTransform() {
    resetRenderTransform(group);
//    SilverUtils.resetRenderTransform(group.getContainer());
  }
  public static native void resetRenderTransform(JavaScriptObject element)/*-{
    var tf = element.getTransform();
    if (tf != null) {
	  	tf.dx = 0;
	  	tf.dy = 0;
	    element.setTransform(tf);
    }
  }-*/;

  public int getTransformX() {
    return getTransformX(group);
  }

  public native int getTransformX(JavaScriptObject element)/*-{
    if (element.getTransform() == null) {
      return 0;
    }
    return parseInt(element.getTransform().dx);
  }-*/;

  public int getTransformY() {
    return getTransformY(group);
  }
  public native int getTransformY(JavaScriptObject element)/*-{
    if (element.getTransform() == null) {
      return 0;
    }
    return parseInt(element.getTransform().dy);
  }-*/;
  
  public void transform(int dx, int dy) {
  	_transform(group, dx, dy);
  }
  
  private native void _transform(JavaScriptObject rawNode, int dx, int dy)/*-{
  	rawNode.transform(dx, dy);
  }-*/;

  private native void nativeApplyTransform(JavaScriptObject rawNode, 
      int dx, int dy)/*-{
    rawNode.applyTransform( {dx:dx, dy:dy} );
  }-*/;
  
  @Override
  public void setTransform(int dx, int dy) {
  	_setTransform(group, dx, dy);
  }

  private native void _setTransform(JavaScriptObject rawNode, int dx, int dy)/*-{
  	var t = rawNode.getTransform();
  	if (t != null) {
  		t.dx = dx;
  		t.dy = dy;
  		rawNode.setTransform(t);
  	} else {
  		rawNode.setTransform({dx: dx, dy: dy});
  	}
  	
	}-*/;

	@Override
  public void setAttribute(String name, String value) {
  	setAttribute(group, name, value);
  }
  
  private native void setAttribute(JavaScriptObject rawNode, String name, String value)/*-{
  	rawNode.rawNode.setAttribute(name, value);
  }-*/;

	@Override
	public void moveToBack() {
		moveToBack(group);
	}
	private native void moveToBack(JavaScriptObject rawNode)/*-{
		rawNode.moveToBack();
	}-*/;
	
	@Override
	public void moveToFront() {
		_moveToFront(group);
	}
	private native void _moveToFront(JavaScriptObject rawNode)/*-{
		rawNode.moveToFront();
	}-*/;
	
	@Override
	public void rotate(int degree, int x, int y) {
		_rotate(group, degree, x, y);
	}
	private native void _rotate(JavaScriptObject rawNode, int degree, int a, int b)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var r = m.rotategAt(degree, a, b);
		rawNode.applyTransform(r);
	}-*/;
	
	@Override
	public void unrotate(int degree, int x, int y) {
		_unrotate(rawNode, degree, x, y);
	}
	private native void _unrotate(JavaScriptObject rawNode, int degree, int a, int b)/*-{
		var m = $wnd.dojox.gfx.matrix;
		var r = m.invert(m.rotategAt(degree, a, b));
		rawNode.applyTransform(r);
	}-*/;
	
	@Override
	public void resetAllTransforms() {
		_resetAllTransforms(group);
	}
	private native void _resetAllTransforms(JavaScriptObject rawNode)/*-{
		var m = $wnd.dojox.gfx.matrix;
		rawNode.setTransform(m.identity);
	}-*/;
	
	final public void setVisible(boolean visible) {
		this.visible = visible;
		String visibilityValue = visible ? "visible" : "hidden";
		_setVisible(group, visibilityValue);
	}

	private native void _setVisible(JavaScriptObject rawNode, String visibility)/*-{
//		if ($wnd.dojo._hasResource["dojox.gfx.svg"]) {
//	  	rawNode.rawNode.setAttribute('display', visibility);
//		}
		rawNode.rawNode.style.visibility = visibility; 
	}-*/;
	
}
