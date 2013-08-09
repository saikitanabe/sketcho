package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.domain.utils.StringUtil;
import net.sevenscales.editor.gfx.domain.IRectangle;

public class SvgRect extends StringUtil {

  public static String svg(IRectangle rect, int transformX, int transformY) {
    String fill = rect.getFillColor().toRgb();
    
    Map<String,String> params = new HashMap<String, String>();
    params.put("%x%", String.valueOf(rect.getX() + transformX + rect.getSvgFixX()));
    params.put("%y%", String.valueOf(rect.getY() + transformY+ rect.getSvgFixY()));
    params.put("%width%", String.valueOf(rect.getWidth()));
    params.put("%height%", String.valueOf(rect.getHeight()));
    params.put("%r%", String.valueOf(rect.getRadius()));
    params.put("%fill%", fill);
    params.put("%fill-opacity%", String.valueOf(rect.getFillColor().getOpacity()));
    params.put("%stroke-width%", String.valueOf(rect.getStrokeWidth()));
    
    // own map for those or use same map, it doesn't really matter
    String transform = "";
    if (rect.getRotateDegree() != 0) {
      int width = (int) Math.sqrt(Math.pow(rect.getWidth() / 2, 2) + Math.pow(rect.getHeight() / 2, 2));
      params.put("%rotate%", String.valueOf(rect.getRotateDegree()) + "," + 
      						(rect.getX()) + "," + 
      						(rect.getY()));
      params.put("%translate%", (width + 2) + "," + 1); // some magic to look better
      String transtemplate = "transform='translate(%translate%) rotate(%rotate%)' ";
      transform = parse(transtemplate, params);
    }
    params.put("%transform%", transform);
    if (rect.getStrokeColor() != null) {
    	params.put("%stroke%", "stroke: rgb(" + rect.getStrokeColor().toRgb() + ");");
    }

    String template = "<rect x='%x%' y='%y%' width='%width%' height='%height%' rx='%r%' %transform% " +
    		               "style='fill: rgb(%fill%);fill-opacity: %fill-opacity%;stroke-width: %stroke-width%;%stroke%'/>";
    return parse(template, params);
  }

}
