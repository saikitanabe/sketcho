package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.editor.gfx.domain.IEllipse;

public class SvgEllipse extends SvgBase {

  public static String svg(IEllipse ellipse, int transformX, int transformY, boolean usesSchemeDefaultColors) {
    Map<String,String> params = new HashMap<String, String>();
    params.put("%cx", String.valueOf(ellipse.getCx() + transformX));
    params.put("%cy", String.valueOf(ellipse.getCy() + transformY));
    params.put("%rx", String.valueOf(ellipse.getRx()));
    params.put("%ry", String.valueOf(ellipse.getRy()));
    params.put("%fill%", rgb(ellipse.getFillColor().toRgb()));
    params.put("%fill-opacity%", String.valueOf(ellipse.getFillColor().getOpacity()));
    params.put("%stroke%", rgb(ellipse.getStrokeColor().toRgb()));
    params.put("%stroke-width%", String.valueOf(ellipse.getStrokeWidth()));
    
    String template = "<ellipse cx='%cx' cy='%cy' rx='%rx' ry='%ry' " +
    		               "style='fill:%fill%;fill-opacity: %fill-opacity%;stroke:%stroke%;stroke-width:%stroke-width%'/>";
    return parse(template, params, usesSchemeDefaultColors);
  }

}
