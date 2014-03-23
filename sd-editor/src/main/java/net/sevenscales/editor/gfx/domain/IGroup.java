package net.sevenscales.editor.gfx.domain;

public interface IGroup extends IContainer, IGraphics {

	public void transform(int dx, int dy);
	public void setTransform(int dx, int dy);
  void setTransform(int dx, int dy, float scaleX, float scaleY);
  public void applyTransform(int dx, int dy);
  void remove(IShape shape);
  public void resetTransform();
  public int getTransformX();
  public int getTransformY();
  void setScale(double xx, double yy);
  String getTransformMatrix();
  String getTransformMatrix(int dx, int dy);

  void setAttribute(String name, String value);
  void moveToBack();
	void moveToFront();
  void insertBefore(IGroup group2);
  void rotate(int degrees, int x, int y);
  void unrotate(int degree, int x, int y);
  void resetAllTransforms();
  void setVisible(boolean visible);
	void remove();
}