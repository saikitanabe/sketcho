package net.sevenscales.editor.gfx.domain;

import java.util.List;
import com.google.gwt.core.client.JsArray;
import net.sevenscales.editor.diagram.utils.BezierHelpers;

public interface IRelationship extends IParentElement {
	List<Integer> getPoints();
	JsArray<BezierHelpers.Segment> getSegments();
	boolean isCurved();
}