package net.sevenscales.editor.gfx.domain;


public interface IRectangle extends IShape {
  public void setShape(Rect rect);
  public void setShape(int x, int y, int width, int height, int radius);
  public int getX();
  public int getY();
  public int getWidth();
  public int getHeight();
  public void setRadius(int radius);
  public int getRadius();
}