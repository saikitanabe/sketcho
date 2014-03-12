package net.sevenscales.editor.gfx.domain;

import java.util.List;

public interface IPath extends IShape {
	interface PathTransformer {
		// int getLeft(String shape);
		// int getTop(String shape);
		// int getRight(String shape);
		// int getBottom(String shape);
		// String applyTransformToShape(String shape, int dx, int dy);
		String getShapeStr(int dx, int dy);
	}

  void setShape(int[] points);
  void setShape(List<Integer> points);
  List<Integer> getShape();
  String getRawShape();
  int getArrayValue(int index);
  void setShape(String shape);
  String getShapeStr(int dx, int dy);
  void setPathTransformer(PathTransformer transformer);
}