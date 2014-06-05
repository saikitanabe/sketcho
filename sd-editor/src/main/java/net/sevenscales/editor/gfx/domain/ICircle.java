package net.sevenscales.editor.gfx.domain;

public interface ICircle extends IShape {

  public abstract void setShape(int cx, int cy, int radius);
  public abstract void setShape(double cx, double cy, int radius);

  public abstract int getX();

  public abstract int getY();

  public abstract int getRadius();

  public abstract void setRadius(int radius);

}