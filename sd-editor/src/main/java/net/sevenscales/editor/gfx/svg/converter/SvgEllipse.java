package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.domain.IEllipse;

public class SvgEllipse extends SvgBase {

  public static String svg(IEllipse ellipse, int transformX, int transformY, Diagram diagram) {
    Map<String,String> params = new HashMap<String, String>();
    params.put("%cx", String.valueOf(ellipse.getCx() + transformX));
    params.put("%cy", String.valueOf(ellipse.getCy() + transformY));
    params.put("%rx", String.valueOf(ellipse.getRx()));
    params.put("%ry", String.valueOf(ellipse.getRy()));
    // Batik doesn't support functions for fill in style
    // params.put("%fill%", rgb(ellipse.getFillColor().toRgb()));
    params.put("%fill%", hex(ellipse.getFillColor().toHexString()));
    params.put("%fill-opacity%", String.valueOf(ellipse.getFillColor().getOpacity()));
    params.put("%stroke%", rgb(ellipse.getStrokeColor().toRgb()));
    params.put("%stroke-width%", String.valueOf(ellipse.getStrokeWidth()));
    
    String template = "<ellipse cx='%cx' cy='%cy' rx='%rx' ry='%ry' " +
    		               "style='fill:%fill%;fill-opacity: %fill-opacity%;stroke:%stroke%;stroke-width:%stroke-width%'/>";
    return parse(ellipse, template, params, diagram);
  }

}
