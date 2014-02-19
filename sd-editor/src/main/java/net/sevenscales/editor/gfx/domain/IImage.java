package net.sevenscales.editor.gfx.domain;


public interface IImage extends IShape {
  void setShape(int x, int y, int width, int height);
  int getX();
  void setX(int x);
  int getY();
  void setY(int y);
  int getWidth();
  void setWidth(int width);
  int getHeight();
  void setHeight(int height);
  void setSrc(String src);
  String getSrc();
  void setShape(int x, int y, int width, int height, String src);
  void setXY(int x, int y);
  void setClipCircle(int x, int y, int r);
}