package net.sevenscales.editor.diagram.utils;

import java.util.List;

import com.google.gwt.core.client.JsArrayInteger;


public class BezierHelpers {
	public static String smooth(List<Integer> points) {
		JsArrayInteger jspoints = JsArrayUtils.readOnlyJsArray(points);
		return _smooth(jspoints);
	}

	private static native String _smooth(JsArrayInteger points)/*-{
		var ps = $wnd.sketchboard.toPoints(points)
		var segments = $wnd.sketchboard.smoothPointsToSegments(ps)
		return $wnd.sketchboard.segmentsToPath(segments)
	}-*/;
}