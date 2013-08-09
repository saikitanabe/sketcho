package net.sevenscales.editor.gfx.domain;

public interface IEllipse extends IShape {

  public abstract void setShape(int cx, int cy, int rx, int ry);

  public abstract int getCx();

  public abstract int getCy();

  public abstract int getRx();

  public abstract int getRy();

}