package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.domain.utils.StringUtil;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ICircle;

public class SvgCircle extends StringUtil {

  public static String svg(ICircle circle, int transformX, int transformY) {
    Map<String,String> params = new HashMap<String, String>();
    params.put("%cx", String.valueOf(circle.getX() + transformX));
    params.put("%cy", String.valueOf(circle.getY() + transformY));
    params.put("%r", String.valueOf(circle.getRadius()));
    params.put("%stroke%", circle.getStrokeColor().toRgb());
    Color c = circle.getFillColor();
    params.put("%fill", c.toRgb());
    
    String template = "<circle cx='%cx' cy='%cy' r='%r' stroke='rgb(%stroke%)' stroke-width='1' fill='rgb(%fill)'/>";
    return parse(template, params);
  }

}
