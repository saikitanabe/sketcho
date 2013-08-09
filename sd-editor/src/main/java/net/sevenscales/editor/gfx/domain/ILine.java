package net.sevenscales.editor.gfx.domain;

public interface ILine extends IShape {

  public static final String DASHED = "ShortDash";
  public static final String DASH = "Dash";
  public static final String LONG_DASH = "LongDash";
  public static final String SOLID = "Solid";

  public abstract int getX1();

  public abstract int getY1();

  public abstract int getX2();

  public abstract int getY2();

  public abstract void setShape(int x1, int y1, int x2, int y2);

  public abstract void setStyle(String style);

  public abstract String getStyle();

}