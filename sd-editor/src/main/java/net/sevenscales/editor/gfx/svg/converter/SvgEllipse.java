package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.domain.utils.StringUtil;
import net.sevenscales.editor.gfx.domain.IEllipse;

public class SvgEllipse extends StringUtil {

  public static String svg(IEllipse ellipse, int transformX, int transformY) {
    Map<String,String> params = new HashMap<String, String>();
    params.put("%cx", String.valueOf(ellipse.getCx() + transformX));
    params.put("%cy", String.valueOf(ellipse.getCy() + transformY));
    params.put("%rx", String.valueOf(ellipse.getRx()));
    params.put("%ry", String.valueOf(ellipse.getRy()));
    params.put("%fill", ellipse.getFillColor().toRgb());
    params.put("%fill-opacity%", String.valueOf(ellipse.getFillColor().getOpacity()));
    params.put("%stroke%", ellipse.getStrokeColor().toRgb());
    params.put("%stroke-width%", String.valueOf(ellipse.getStrokeWidth()));
    
    String template = "<ellipse cx='%cx' cy='%cy' rx='%rx' ry='%ry' " +
    		               "style='fill:rgb(%fill);fill-opacity: %fill-opacity%;stroke:rgb(%stroke%);stroke-width:%stroke-width%'/>";
    return parse(template, params);
  }

}
