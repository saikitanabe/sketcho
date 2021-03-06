package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IPolyline;

public class SvgPolyline extends SvgLine {

  public static String svg(IPolyline poly, int transformX, int transformY, Diagram diagram) {
    Map<String,String> params = new HashMap<String, String>();
    
    List<Integer> points = poly.getShape();
    String pairs = convertPoints2Pairs(points, transformX, transformY);
    
    params.put("%points", pairs.trim());
    params.put("%stroke-width", String.valueOf(poly.getStrokeWidth()));
    params.put("%stroke%", rgb(String.valueOf(poly.getStrokeColor().toRgb())));
    
    String fill = "none";
    Color color = poly.getFillColor();
    if (color != null) {
//      fill-opacity:0.1
      fill = hex(color.toHexString());
    	params.put("%fill-opacity", "fill-opacity:"+Double.toString(color.opacity));
    }

    params.put("%fill%", fill);

    String template = "<polyline points='%points' style='fill:%fill%;stroke:%stroke%;stroke-width:%stroke-width;%fill-opacity;'/>";
    if (poly.getStrokeStyle() != null && poly.getStrokeStyle().equals(ILine.DASH)) {
      template = "<polyline points='%points' style='fill:%fill%;stroke:%stroke%;stroke-width:%stroke-width;stroke-dasharray:%style;%fill-opacity;'/>";
//      System.out.println("STYLE: " + String.valueOf(poly.getStrokeStyle()));
      params.put("%style", "4,3");
    }

    return parse(poly, template, params, diagram);
  }

  public static String convertPoints2Pairs(int[] points, int transformX, int transformY) {
    String result = "";
    for (int i = 0; i < points.length; i +=2) {
      result += (points[i] + transformX) + "," + (points[i+1] + transformY) + " ";
    }
    result = result.trim();
    return result;
  }

  public static String convertPoints2Pairs(List<Integer> points, int transformX, int transformY) {
    String result = "";
    for (int i = 0; i < points.size(); i +=2) {
      result += (points.get(i) + transformX) + "," + (points.get(i+1) + transformY) + " ";
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
