package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.Tools;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.uicomponents.uml.GenericElement;
import net.sevenscales.domain.utils.SLogger;

public class SvgPath extends SvgLine {
  private static SLogger logger = SLogger.createLogger(SvgPath.class);

  public static String svg(IPath path, int transformX, int transformY, Diagram diagram) {
    Map<String,String> params = new HashMap<String, String>();
    
//    List<Integer> points = poly.getShape();
//    String pairs = convertPoints2Pairs(points);
    String d = path.getShapeStr(transformX, transformY);
    if (d == null) {
      d = path.getRawShape();
    }
    // logger.debug("path: {}", d);
    params.put("%d%", d);

    /* 
    TODO IE hack for vector-effect:nonscaling-stroke!!!
    - can be enabled on IE11
    - hack needs to be changed if there are different stroke widths
    */
    // params.put("%stroke-width%", String.valueOf(path.getStrokeWidth()));
    params.put("%stroke-width%", strokeWidth(diagram, path));
    /* TODO IE hack ENDS */

    params.put("%stroke%", rgb(String.valueOf(path.getStrokeColor().toRgb())));
    
    // default style that has been defined in inscape svg file data-hint-style
    params.put("%default-style%", path.getStyle());

    
    String fill = "none";
    Color color = path.getFillColor();
    if (color != null && color.opacity > 0) {
//      fill-opacity:0.1
      fill = rgb(color.toRgb());
    	params.put("%fill-opacity", "fill-opacity:"+Double.toString(color.opacity));
    }
    params.put("%fill%", fill);

    // styles can override other settings
    String[] pairs = path.getStyle().split(";");
    for (String pairstr : pairs) {
      String[] pair = pairstr.split(":");
      if (pair.length == 2) {
        params.put("%" + pair[0] + "%", pair[1].trim());
      }
    }


    String template = "<path d='%d%' style='fill:%fill%;stroke:%stroke%;stroke-width:%stroke-width%;%vectoreffect%;%default-style%' />";

    if (path.getStrokeStyle() != null && path.getStrokeStyle().equals(ILine.DASH)) {
      template = "<path d='%d%' style='fill:%fill%;stroke:%stroke%;stroke-width:%stroke-width%;stroke-dasharray:%stroke-dasharray%;%vectoreffect%;%default-style%' />";
      params.put("%stroke-dasharray%", "4,3");
    }

    // for now vector-effect is disabled
    params.put("%vectoreffect%", "");

    return parse(path, template, params, diagram);
  }

  private static String strokeWidth(Diagram diagram, IPath path) {
    if (!Tools.isSketchMode() && /*isConfluence(diagram.getSurfaceHandler()) && */(diagram instanceof GenericElement)) {
      GenericElement ge = (GenericElement) diagram;
      return String.valueOf(ge.scaledStrokeWidth(ge.getShapeFactorX(), ge.getShapeFactorY()));
    } else {
      return String.valueOf(path.getStrokeWidth());
    }
  }

  private static boolean isConfluence(ISurfaceHandler surface) {
    return surface.getEditorContext().isTrue(EditorProperty.CONFLUENCE_MODE);
  }  

  public static String convertPoints2Pairs(int[] points) {
    String result = "";
    for (int i = 0; i < points.length; i +=2) {
      result += points[i] + "," + points[i+1] + " ";
    }
    result = result.trim();
    return result;
  }

  public static String convertPoints2Pairs(List<Integer> points) {
    String result = "";
    for (int i = 0; i < points.size(); i +=2) {
      result += points.get(i) + "," + points.get(i+1) + " ";
    }
    result = result.trim();
    return result;
  }

  private static boolean closure(List<Integer> points) {
    if (points.get(0).equals(points.get(points.size()-2)) &&
        points.get(1).equals(points.get(points.size()-1))) {
      // if first last last point is same then polyline closes
      return true;
    }
    return false;
  }

}
