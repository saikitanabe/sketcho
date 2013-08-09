package net.sevenscales.editor.gfx.domain;

public interface IGroup extends IContainer, IGraphics {

	public void transform(int dx, int dy);
	public void setTransform(int dx, int dy);
  public void applyTransform(int dx, int dy);
  void remove(IShape shape);
  public void resetTransform();
  public int getTransformX();
  public int getTransformY();

  public void setAttribute(String name, String value);
  public void moveToBack();
	public void moveToFront();
  void rotate(int degrees, int x, int y);
  void unrotate(int degree, int x, int y);
  void resetAllTransforms();
  void setVisible(boolean visible);
	void remove();
}