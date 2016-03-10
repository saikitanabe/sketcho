package net.sevenscales.editor.gfx.domain;

import com.google.gwt.core.client.JavaScriptObject;

public interface IGroup extends IContainer, IGraphics {

	public void transform(int dx, int dy);
  public void setTransform(int dx, int dy);
	void setTransform(double dx, double dy);
  void setTransform(int dx, int dy, float scaleX, float scaleY);
  public void applyTransform(int dx, int dy);
  void remove(IShape shape);
  public void resetTransform();
  public int getTransformX();
  public int getTransformY();
  double getTransformDoubleX();
  double getTransformDoubleY();
  void setScale(double xx, double yy);
  String getTransformMatrix();
  String getTransformMatrix(int dx, int dy);

  void translate(double dx, double dy);
  void scale(double xx, double yy);
  void scaleAt(double z, double px, double py);

  void setAttribute(String name, String value);
  void moveToBack();
	void moveToFront();
  void insertBefore(IGroup group2);
  void rotate(int degrees, int x, int y);
  void unrotate(int degree, int x, int y);
  void resetAllTransforms();
  boolean isVisible();
  void setVisible(boolean visible);
	void remove();
  /**
  * Returns null if on root layer.
  */
  IGroup getLayer();
  JavaScriptObject getMatrix();
}