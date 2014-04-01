package net.sevenscales.editor.diagram;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;

import net.sevenscales.domain.SvgDataDTO;
import net.sevenscales.domain.PathDTO;
import net.sevenscales.domain.DiagramItemDTO;
import net.sevenscales.domain.ElementType;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.event.FreehandModeChangedEvent.FreehandModeType;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.gfx.domain.IPolyline;
import net.sevenscales.editor.uicomponents.uml.GenericElement;
import net.sevenscales.editor.uicomponents.uml.FreehandElement;
import net.sevenscales.editor.diagram.shape.GenericShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers;
import net.sevenscales.editor.content.utils.ScaleHelpers.ScaledAndTranslatedPoint;


class FreehandPath {
  // private static SLogger logger = SLogger.createLogger(FreehandPath.class);
  IGroup group;
  IPolyline polyline;
  List<Integer> points = new ArrayList<Integer>();
  ISurfaceHandler surface;
  boolean curve = true;
  boolean closePath = false;

  FreehandPath(ISurfaceHandler surface) {
    this.surface = surface;
    group = IShapeFactory.Util.factory(true).createGroup(surface.getSurface());
    //  path = IShapeFactory.Util.factory(true).createPath(group);
    polyline = IShapeFactory.Util.factory(true).createPolyline(group);

    polyline.setVisibility(true);
    polyline.setStroke(Theme.getCurrentColorScheme().getBorderColor().toHexString());
    polyline.setStrokeWidth(FreehandElement.FREEHAND_STROKE_WIDTH);
  }

  Diagram plot() {
    if (polyline != null && points.size() > 2) {
      // generic drawing 
      // - deprecated for now since generates bigger shape than own custom simplify algoritm
      return plotOld();
      // return plotGeneric();
    }
    return null;
  }

  private Diagram plotOld() {

  }

  private List<Integer> filterPoints() {
     int modeType = surface.getEditorContext().<FreehandModeType>getAs(EditorProperty.FREEHAND_MODE_TYPE).value();
     logger.debug("filterPoints modeType {}", modeType);
     List<Integer> result = new ArrayList<Integer>();
     for (int i = 0; i < points.size(); i += 2) {
      if (i % modeType == 0) {
        addTranslatedPoint(points.get(i), points.get(i + 1), result);
      }
      // if (i % modeType == 0) {
      addTranslatedPoint(points.get(i), points.get(i + 1), result);
      // }
     }
     
     // add last point just in case if it has been filtered out above
     addTranslatedPoint(points.get(points.size()-2), points.get(points.size()-1), result);

     return result;
   }

  private Diagram plotGeneric() {
    int absleft = DiagramHelpers.getLeftCoordinate(points);
    int abstop = DiagramHelpers.getTopCoordinate(points);
    int width = DiagramHelpers.getWidth(points);
    int height = DiagramHelpers.getHeight(points);

    String svg = null;
    if (curve) {
      svg = fitPointsToSvg(absleft, abstop);
    } else {
      // if staticMovement, do not simplify path! Format path with lines
      svg = formatAllPointsAsLines(absleft, abstop);
    }

    // use absolute values to calculate relative points
    ScaledAndTranslatedPoint pos = ScaleHelpers.scaleAndTranslateScreenpoint(absleft, abstop, surface);
    // need to scale width and height to correspond screen coordinates
    // doe not translate, wince only left, top is scaled and translated!
    int scaledWidth = ScaleHelpers.scaleValue(width, surface.getScaleFactor());
    int scaledHeight = ScaleHelpers.scaleValue(height, surface.getScaleFactor());
    List<PathDTO> paths = new ArrayList<PathDTO>();
    paths.add(new PathDTO(svg, ""));
    SvgDataDTO svgdata = new SvgDataDTO(paths, scaledWidth, scaledHeight);
    GenericElement diagram = new GenericElement(surface, 
      new GenericShape(ElementType.FREEHAND2.getValue(), 
                       pos.scaledAndTranslatedPoint.x, pos.scaledAndTranslatedPoint.y, scaledWidth, scaledHeight, 
                       ShapeProperty.SHAPE_AUTO_RESIZE_FALSE.getValue(), 
                       svgdata),
      "", 
      Theme.createDefaultBackgroundColor(), 
      Theme.createDefaultBorderColor(), 
      Theme.createDefaultTextColor(),
      surface.getEditorContext().isEditable(), 
      DiagramItemDTO.createGenericItem(ElementType.FREEHAND2));
    // surface.add(diagram, true);
    polyline.setVisibility(false);
    points.clear();
    polyline.setShape(points);
    polyline.moveToBack();

    // logger.debug("PLOTTING... done");
    return diagram;
  }

  private String formatAllPointsAsLines(int absx, int absy) {
    String result = "";
    List<Integer> relative = relativePoints(absx, absy);
    for (int i = 0; i < relative.size(); i += 2) {
      if (i == 0) {
        result += "m";
      } else {
        result += " ";
      }
      result += relative.get(i) + "," + relative.get(i + 1);
    }
    if (closePath) {
      result += "z";
    }
    return result;
  }

  private String fitPointsToSvg(int absx, int absy) {
    List<Integer> relative = relativePoints(absx, absy);
    return fit(relative);
  }

  private List<Integer> relativePoints(int absx, int absy) {
    List<Integer> result = new ArrayList<Integer>();
    int prevx = absx;
    int prevy = absy;

    for (int i = 0; i < points.size(); i += 2) {
      // - calculation from absolute values to relative (use unscaled and translated values)
      // - since this is now relative
      // - do not take into account root layer tranlation and only scale point
      // - left, top will take into account root layer scaling and translation
      int ax = points.get(i);
      int ay = points.get(i + 1); 
      int x = 0;
      int y = 0;

      if (curve) {
        // for some reason need minux first abs from all coordinates...
        x = ax - absx;
        y = ay - absy;
      } else {
        x = ax - prevx;
        y = ay - prevy;
      }
      result.add(ScaleHelpers.scaleValue(x, surface.getScaleFactor()));
      result.add(ScaleHelpers.scaleValue(y, surface.getScaleFactor()));
      prevx = ax;
      prevy = ay;
    }

    trimLastExtreaZeroMove(result);
    return result;
  }

  private void trimLastExtreaZeroMove(List<Integer> relativePoints) {
    int size = relativePoints.size();
    if (size > 2 && relativePoints.get(size - 2) == 0 && relativePoints.get(size - 1) == 0) {
      // remove last zero move x, y
      relativePoints.remove(relativePoints.size() - 1);
      relativePoints.remove(relativePoints.size() - 1);
    }
  }

  private String fit(List<Integer> points) {
    JsArrayInteger dest = JsArrayInteger.createArray().cast();
    for (Integer i : points) {
      dest.push(i.intValue());
    }
    // JsArray<PaperSegment> segments = fit(dest);
    // for (int i = 0; i < segments.length(); ++i) {
    //   PaperPoint point = segments.get(i).getPoint();
    //   logger.debug("seg {}, {}", point.getX(), point.getY());
    // }
    return fit(dest);
  }

  private native String /*JsArray<PaperSegment>*/ fit(JsArrayInteger points)/*-{
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
            parts.push('l' + f.point(point2, precision));
          }
        } else {
          // c = relative curveto: handle1, handle2 + end - start,
          // end - start
          var end = point2.subtract(point1);
          parts.push('c' + f.point(handle1, precision)
              + ' ' + f.point(end.add(handle2), precision)
              + ' ' + f.point(end, precision));
        }
      }

      if (segments.length === 0)
        return '';
      parts.push('m' + f.point(segments[0]._point));
      for (var i = 0, l = segments.length  - 1; i < l; i++)
        addCurve(segments[i], segments[i + 1], false);
      if (this._closed) {
        addCurve(segments[segments.length - 1], segments[0], true);
        parts.push('z');
      }
      return parts.join('');
    }

    console.log(getPathData(fitter.fit()));
    // return fitter.fit();
    return getPathData(fitter.fit());
  }-*/;

}
