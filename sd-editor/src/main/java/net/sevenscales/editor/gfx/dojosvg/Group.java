package net.sevenscales.editor.gfx.dojosvg;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.gfx.domain.IContainer;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShape;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

class Group extends Graphics implements IContainer, IGroup {
	private static final SLogger logger = SLogger.createLogger(Group.class);
	
  private JavaScriptObject group;
  private IGroup layer;
	private boolean visible = true;
  private int rotateDegree;

  Group(IContainer container) {
    group = nativeCreateGroup(container.getContainer());
    if (container instanceof IGroup) {
      layer = (IGroup)container;
    }
  }

  Group(Surface surface) {
    group = nativeCreateGroup(surface.getContainer());
  }
  
  public JavaScriptObject getContainer() {
    return group;
  }

  public Element getElement() {
    return _getElement(group);
  }
  private native Element _getElement(JavaScriptObject group)/*-{
    return group.rawNode
  }-*/;

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

  public double getTransformDoubleX() {
    return getTransformDoubleX(group);
  }

  public native double getTransformDoubleX(JavaScriptObject element)/*-{
    if (element.getTransform() == null) {
      return 0;
    }
    return element.getTransform().dx;
  }-*/;

  public double getTransformDoubleY() {
    return getTransformDoubleY(group);
  }
  public native double getTransformDoubleY(JavaScriptObject element)/*-{
    if (element.getTransform() == null) {
      return 0;
    }
    return element.getTransform().dy;
  }-*/;

  public String getTransformMatrix() {
    return _getTransformMatrix(group);
  }

  private native String _getTransformMatrix(JavaScriptObject rawNode)/*-{
    var t = rawNode.getTransform();
    if (t) {
      // identity {xx: 1, xy: 0, dx: 0, yx: 0, yy: 1, dy: 0}
      return 'matrix(' + t.xx + ',' + t.yx +',' + t.xy + ',' + t.yy + ',' + t.dx + ',' + t.dy + ')';
    }
    return null;
  }-*/;

  public String getTransformMatrix(int dx, int dy) {
    return _getTransformMatrix(group, dx, dy);
  }

  public JavaScriptObject getMatrix() {
    return _getMatrix(group);
  }
  private native JavaScriptObject _getMatrix(JavaScriptObject group)/*-{
    return group.getTransform()
  }-*/;

  private native String _getTransformMatrix(JavaScriptObject rawNode, int dx, int dy)/*-{
    var t = rawNode.getTransform();
    if (t) {
      // identity {xx: 1, xy: 0, dx: 0, yx: 0, yy: 1, dy: 0}
      return 'matrix(' + t.xx + ',' + t.yx +',' + t.xy + ',' + t.yy + ',' + (t.dx + dx) + ',' + (t.dy + dy) + ')';
    }
    return null;
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

  @Override
  public void setTransform(double dx, double dy) {
  	_setTransform(group, (int) dx, (int) dy);
  }

  public void setScale(double xx, double yy) {
    _setScale(group, xx, yy);
  }
  private native void _setScale(JavaScriptObject rawNode, double xx, double yy)/*-{
    var m = $wnd.dojox.gfx.matrix;
    rawNode.setTransform(m.scale(xx, yy));
  }-*/; 


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

 //  private native void _setTransform(JavaScriptObject rawNode, double dx, double dy)/*-{
 //  	var t = rawNode.getTransform();
 //  	if (t != null) {
 //  		t.dx = dx;
 //  		t.dy = dy;
 //  		rawNode.setTransform(t);
 //  	} else {
 //  		rawNode.setTransform({dx: dx, dy: dy});
 //  	}
	// }-*/;

  @Override
  public void setTransform(int x, int y, float scaleX, float scaleY) {
    _setTransform(group, x, y, scaleX, scaleY);
  }

  private native void _setTransform(JavaScriptObject rawNode, int x, int y, float scaleX, float scaleY)/*-{
    var m = $wnd.dojox.gfx.matrix;
    // {dx: dx, dy: dy}
    rawNode.setTransform([m.translate(x, y), m.scale(scaleX, scaleY)]);
    // var t = rawNode.getTransform();
    // if (t != null) {
    //   t.dx = dx;
    //   t.dy = dy;
    //   rawNode.setTransform(t);
    // } else {
    //   rawNode.setTransform({dx: dx, dy: dy});
    // }
    
  }-*/;

  public void translate(double dx, double dy) {  
    _translate(group, dx, dy);
  }
  private native void _translate(JavaScriptObject rawNode, double dx, double dy)/*-{
    var m = $wnd.dojox.gfx.matrix;
    rawNode.applyTransform(m.translate(dx, dy));
  }-*/; 

  public void scale(double xx, double yy) {  
    _scale(group, xx, yy);
  }
  private native void _scale(JavaScriptObject rawNode, double xx, double yy)/*-{
    var m = $wnd.dojox.gfx.matrix;
    rawNode.applyTransform(m.scale(xx, yy));
  }-*/; 

  public void scaleAt(double z, double px, double py) {
    _scaleAt(group, z, px, py);
  }
  private native void _scaleAt(JavaScriptObject rawNode, double z, double px, double py)/*-{
    rawNode.setTransform($wnd.dojox.gfx.matrix.scaleAt(z, z, px, py))
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
    this.rotateDegree = degree;
		ShapeUtils._rotate2(group, degree, x, y);
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

  final public boolean isVisible() {
    return visible;
  }
	
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

  public void insertBefore(IGroup group2) {
    if (getLayer() == group2.getLayer()) {
      // can only move within the layer
      insertBefore(this.group, group2.getContainer());
    }
  }

  private native void insertBefore(JavaScriptObject group1, JavaScriptObject group2)/*-{
    // this.rawNode.parentNode.insertBefore(this.rawNode, this.rawNode.parentNode.firstChild);
    group1.rawNode.parentNode.insertBefore(group1.rawNode, group2.rawNode);
  }-*/;

  public IGroup getLayer() {
    return layer;
  }

	
}
