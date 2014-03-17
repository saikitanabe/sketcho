package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IPath;
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
    params.put("%stroke-width", String.valueOf(path.getStrokeWidth()));
    params.put("%stroke%", rgb(String.valueOf(path.getStrokeColor().toRgb())));
    
    String fill = "none";
    Color color = path.getFillColor();
    if (color != null && color.opacity > 0) {
//      fill-opacity:0.1
      fill = rgb(color.toRgb());
    	params.put("%fill-opacity", "fill-opacity:"+Double.toString(color.opacity));
    }

    params.put("%fill%", fill);

    String template = "<path d='%d%' style='fill:%fill%;stroke:%stroke%;stroke-width:%stroke-width;'/>";
//    String template = "<polyline points='%points' style='%fill;stroke:rgb(%stroke%);stroke-width:%stroke-width;%fill-opacity;'/>";
//    if (poly.getStyle() != null) {
//      template = "<polyline points='%points' style='%fill;stroke:rgb(%stroke%);stroke-width:%stroke-width;stroke-dasharray:%style;%fill-opacity;'/>";
//      params.put("%style", String.valueOf(map.get(poly.getStyle())));
//    }

    return parse(path, template, params, diagram);
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
