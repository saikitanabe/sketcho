package net.sevenscales.editor.diagram.utils;

import java.util.List;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


public class BezierHelpers {
	public static class Point extends JavaScriptObject {
		protected Point() {}

		public final native double getX()/*-{
			return this.x
		}-*/;
		public final native double getY()/*-{
			return this.y
		}-*/;
	}

	public static class Segment extends JavaScriptObject {
		protected Segment() {}

		public final native Point getPoint1() /*-{ return this.p1; }-*/;
		public final native Point getPoint2() /*-{ return this.p2; }-*/;
		public final native Point getControlPoint1() /*-{ return this.cp1; }-*/;
		public final native Point getControlPoint2() /*-{ return this.cp2; }-*/;
	}

	public static JsArray<Point> toPoints(List<Integer> points) {
		JsArrayInteger jspoints = JsArrayUtils.readOnlyJsArray(points);
		return toPoints(jspoints);
	}
	public static native JsArray<Point> toPoints(JsArrayInteger points)/*-{
		return $wnd.sketchboard.toPoints(points)
	}-*/;

  public static double bezierInterpolation(double t, double a, double b, double c, double d) {
    double t2 = t * t;
    double t3 = t2 * t;
    return a + (-a * 3 + t * (3 * a - a * t)) * t
    + (3 * b + t * (-6 * b + b * 3 * t)) * t
    + (c * 3 - c * 3 * t) * t2
    + d * t3;
  }

	public static JsArray<Segment> segments(List<Integer> points) {
		return segments(toPoints(points));
	}

	public static native JsArray<Segment> segments(JsArray<Point> points)/*-{
		return $wnd.sketchboard.smoothPointsToSegments(points)
	}-*/;

	public static String smooth(List<Integer> points) {
		JsArrayInteger jspoints = JsArrayUtils.readOnlyJsArray(points);
		return _smooth(jspoints);
	}

	public static String smooth(JsArray<Segment> segments) {
		return _smooth(segments);
	}

	private static native String _smooth(JsArrayInteger points)/*-{
		var ps = $wnd.sketchboard.toPoints(points)
		var segments = $wnd.sketchboard.smoothPointsToSegments(ps)
		return $wnd.sketchboard.segmentsToPath(segments)
	}-*/;

	private static native String _smooth(JsArray<Segment> segments)/*-{
		return $wnd.sketchboard.segmentsToPath(segments)
	}-*/;

	public static Segment lastSegment(JsArray<Segment> segments) {
		Segment result = null;
		int lastIndex = segments.length() - 1;
		if (lastIndex < segments.length()) {
			result = segments.get(lastIndex);
		}
		return result;
	}

}