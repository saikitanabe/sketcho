package net.sevenscales.editor.gfx.domain;

import net.sevenscales.editor.diagram.Diagram;

public interface IChildElement {
	Diagram getParent();
	Diagram asDiagram();
	void setTransform(int dx, int dy);
	void snapshotTransformations();
  int getSnaphsotTransformX();
  int getSnaphsotTransformY();
  int getLeft();
  int getTop();
  void saveRelativeDistance(double rleft, double rtop);
  double getRelativeDistanceLeft();
  double getRelativeDistanceTop();
  void setPosition(double left, double top);
  SegmentPoint fixedPointIndex();
	double getFixedLeft();
  double getFixedTop();
}