package net.sevenscales.editor.uicomponents;

import java.util.List;
import com.google.gwt.core.client.JsArray;
import net.sevenscales.editor.diagram.utils.BezierHelpers;

public interface IRelationship {
	List<Integer> getPoints();
	JsArray<BezierHelpers.Segment> getSegments();
	boolean isCurved();
}