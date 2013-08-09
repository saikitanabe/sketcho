package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.domain.utils.StringUtil;
import net.sevenscales.editor.gfx.domain.ILine;

public class SvgLine extends StringUtil {
  public static Map<String,String> map;
  static {
    map = new HashMap<String, String>();
    map.put("ShortDash", "3, 2");
    map.put("Solid", "none");
    map.put("solid", "none");
  }
  
  public static String svg(ILine line, int transformX, int transformY) {
    Map<String,String> params = new HashMap<String, String>();
    params.put("%x1", String.valueOf(line.getX1() + transformX));
    params.put("%y1", String.valueOf(line.getY1() + transformY));
    params.put("%x2", String.valueOf(line.getX2() + transformX));
    params.put("%y2", String.valueOf(line.getY2() + transformY));
    params.put("%style", String.valueOf(map.get(line.getStyle())));
    params.put("%stroke%", line.getStrokeColor().toRgb());
    String strokeWidth = "1";
    if (line.getStrokeWidth() > 0) {
    	strokeWidth = String.valueOf(line.getStrokeWidth());
    }
    params.put("%width%", strokeWidth);
    
    String template = "<line x1='%x1' y1='%y1' x2='%x2' y2='%y2' style='stroke:rgb(%stroke%);stroke-width:%width%;stroke-dasharray:%style;'/>";
    return parse(template, params);
  }
}
