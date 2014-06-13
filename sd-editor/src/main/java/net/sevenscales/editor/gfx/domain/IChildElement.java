package net.sevenscales.editor.gfx.domain;

import net.sevenscales.editor.diagram.Diagram;

public interface IChildElement {
	Diagram getParent();
	Diagram asDiagram();
	void setTransform(int dx, int dy);
	void snapshotTransformations();
  int getSnaphsotTransformX();
  int getSnaphsotTransformY();
}