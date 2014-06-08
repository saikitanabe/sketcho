package net.sevenscales.editor.diagram.utils;

import java.util.List;

import com.google.gwt.core.client.JsArrayInteger;

public class PathFitter {
  public static String fitRelative(List<Integer> points) {
    return fit(points, false);
  }
  public static String fitAbsolute(List<Integer> points) {
    return fit(points, true);
  }

  private static String fit(List<Integer> points, boolean absolute) {
    JsArrayInteger dest = JsArrayInteger.createArray().cast();
    for (Integer i : points) {
      dest.push(i.intValue());
    }
    // JsArray<PaperSegment> segments = fit(dest);
    // for (int i = 0; i < segments.length(); ++i) {
    //   PaperPoint point = segments.get(i).getPoint();
    //   logger.debug("seg {}, {}", point.getX(), point.getY());
    // }
    return PathFitter.fit(dest, absolute);
  }

  private static native String /*JsArray<PaperSegment>*/ fit(JsArrayInteger points, boolean absolute)/*-{
    var segs = []
    for (var i = 0; i < points.length; i += 2) {
      segs.push(new $wnd.paper.Segment(points[i], points[i + 1]))
    }
    // var myPath = {_segments: [new $wnd.paper.Segment(1,2), new $wnd.paper.Segment(3,4), new $wnd.paper.Segment(5,6)]};
    var myPath = {_segments: segs}
    // console.log(myPath)
    var fitter = new $wnd.paper.PathFitter(myPath, 2.5);

    var getPathData = function(_segments, precision) {
      var segments = _segments,
        f = $wnd.paper.Formatter.instance,
        parts = [];

      // TODO: Add support for H/V and/or relative commands, where appropriate
      // and resulting in shorter strings
      function addCurve(seg1, seg2, skipLine) {
        var point1 = seg1._point,
          point2 = seg2._point,
          handle1 = seg1._handleOut,
          handle2 = seg2._handleIn;
        if (handle1.isZero() && handle2.isZero()) {
          if (!skipLine) {
            // L = absolute lineto: moving to a point with drawing
            parts.push((absolute ? 'L' : 'l') + f.point(point2, precision));
          }
        } else {
          // c = relative curveto: handle1, handle2 + end - start,
          // end - start
          var end = point2.subtract(point1);
          parts.push((absolute ? 'C' : 'c') + f.point(handle1, precision)
              + ' ' + f.point(end.add(handle2), precision)
              + ' ' + f.point(end, precision));
        }
      }

      if (segments.length === 0)
        return '';
      parts.push((absolute ? 'M' : 'm') + f.point(segments[0]._point));
      for (var i = 0, l = segments.length  - 1; i < l; i++)
        addCurve(segments[i], segments[i + 1], false);
      if (this._closed) {
        addCurve(segments[segments.length - 1], segments[0], true);
        parts.push((absolute ? 'Z' : 'z'));
      }
      return parts.join('');
    }

    // console.log(getPathData(fitter.fit()));
    // return fitter.fit();
    return getPathData(fitter.fit());
  }-*/;
}