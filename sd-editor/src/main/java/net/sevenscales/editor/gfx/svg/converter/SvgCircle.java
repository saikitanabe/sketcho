package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.ICircle;

public class SvgCircle extends SvgBase {

  public static String svg(ICircle circle, int transformX, int transformY, Diagram diagram) {
    Map<String,String> params = new HashMap<String, String>();
    params.put("%cx", String.valueOf(circle.getX() + transformX));
    params.put("%cy", String.valueOf(circle.getY() + transformY));
    params.put("%r", String.valueOf(circle.getRadius()));
    params.put("%stroke%", rgb(circle.getStrokeColor().toRgb()));
    Color c = circle.getFillColor();
    params.put("%fill%", rgb(c.toRgb()));
    params.put("%fill-opacity%", String.valueOf(c.getOpacity()));
    
    String template = "<circle cx='%cx' cy='%cy' r='%r' stroke='%stroke%' stroke-width='1' fill='%fill%' fill-opacity='%fill-opacity%'/>";
    return parse(circle, template, params, diagram);
  }

}
