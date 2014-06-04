package net.sevenscales.editor.gfx.domain;

import java.util.List;

public interface IPolyline extends IShape {
  void setShape(int[] points);
  void setShape(double[] points);
  void setShape(List<Integer> points);
  List<Integer> getShape();
  int getArrayValue(int index);
}