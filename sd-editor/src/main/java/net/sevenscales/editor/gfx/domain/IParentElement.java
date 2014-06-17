package net.sevenscales.editor.gfx.domain;

import java.util.List;

import net.sevenscales.editor.diagram.Diagram;


public interface IParentElement {
	void addChild(IChildElement child);
	Diagram asDiagram();
	List<IChildElement> getChildren();
	SegmentPoint findClosestSegmentPointIndex(int x, int y);
	PointDouble getPoint(SegmentPoint segmentPoint);
	void moveChild(IChildElement child);
}