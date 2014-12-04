package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.domain.ILine;

public class SvgLine extends SvgBase {
  public static Map<String,String> map;
  static {
    map = new HashMap<String, String>();
    // map.put("ShortDash", "4,1");
    map.put("Solid", "none");
    map.put("solid", "none");
  }
  
  public static String svg(ILine line, int transformX, int transformY, Diagram diagram) {
    Map<String,String> params = new HashMap<String, String>();
    params.put("%x1", String.valueOf(line.getX1() + transformX));
    params.put("%y1", String.valueOf(line.getY1() + transformY));
    params.put("%x2", String.valueOf(line.getX2() + transformX));
    params.put("%y2", String.valueOf(line.getY2() + transformY));
    params.put("%style", getLineStyle(line));
    params.put("%stroke%", rgb(line.getStrokeColor().toRgb()));
    String strokeWidth = "1";
    if (line.getStrokeWidth() > 0) {
    	strokeWidth = String.valueOf(line.getStrokeWidth());
    }
    params.put("%width%", strokeWidth);
    
    String template = "<line x1='%x1' y1='%y1' x2='%x2' y2='%y2' style='stroke:%stroke%;stroke-width:%width%;stroke-dasharray:%style;'/>";
    return parse(line, template, params, diagram);
  }

  private static String getLineStyle(ILine line) {
    if ("ShortDash".equals(line.getStyle())) {
      int swidth = (int) line.getStrokeWidth();
      return 4 * swidth + "," + 1 * swidth;
    } else {
      return map.get(line.getStyle());
    }
  }
}
