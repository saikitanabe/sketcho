package net.sevenscales.editor.gfx.domain;

import java.util.Collection;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.drag.AnchorElement;

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

  boolean isInitialized();
  void updateFixedDistance();
  void updateFixedSegment();
  SegmentPoint fixedPointIndex();
	double getFixedDistanceLeft();
  double getFixedDistanceTop();

  Collection<AnchorElement> getAnchors();
}