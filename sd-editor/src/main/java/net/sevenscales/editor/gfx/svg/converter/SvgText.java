package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import net.sevenscales.domain.utils.StringUtil;
import net.sevenscales.editor.gfx.domain.IText;
import net.sevenscales.domain.utils.SLogger;

public class SvgText extends StringUtil {
  private static final SLogger logger = SLogger.createLogger(SvgText.class);

  private static final RegExp AMP_RE = RegExp.compile("&", "g");
  private static final RegExp DOLLAR_RE = RegExp.compile("\\$", "g");

	public static String escapeAmp(String s) {
		// this is already escaped in Text.java
//    if (s.indexOf("&") != -1) {
//      s = AMP_RE.replace(s, "&amp;");
//    }
    
    if (s.indexOf("$") != -1) {
      s = DOLLAR_RE.replace(s, "\\$");
    }

    return s;
	}
  
  public static String svg(IText t, int transformX, int transformY) {
    Map<String,String> params = new HashMap<String, String>();
    params.put("%x", String.valueOf(t.getX() + transformX));
    params.put("%y", String.valueOf(t.getY() + transformY));
    params.put("%weight", t.getFontWeight());
    params.put("%size", t.getFontSize().replaceFirst("px", ""));
//    params.put("%size", "11");
    params.put("%anchor", t.getAlignment());
//    params.put("%font-family", t.getFontFamily());
    
    // sans-serif doesn't work on batik 1.6, need to test again with 1.7
    // SansSerif, 11 has not been tried out yet, quality is pretty fine, but doesn't fit
    // Arial hack
    params.put("%font-family", "Arial2Change");
    // yeps, this is needed when working with client side svg => canvas => png
    // with confluence, not working at the moment
//    params.put("%font-family", "sans-serif, Arial");
    params.put("%fill", "#" + t.getFillColor().toHexString());
    
    String encoded = SafeHtmlUtils.htmlEscape(t.getText());
    params.put("%text", encoded);
    
//    String text = t.getText();
    if (encoded == null || encoded.length() == 0) {
    	encoded = escapeAmp(t.getChildElements(transformX));
    }
    params.put("%text", encoded);
    String template = "<text x='%x' y='%y' style='font-weight:%weight; font-size: %sizepx; text-anchor: %anchor; font-family: %font-family; fill: %fill;'>%text</text>";
//    String result = "<text x='"++"' y='"+t.getY()+"' style='font-weight:bold; font-size: 12px; text-anchor: middle; font-family: Arial;'>"+t.getText()+"</text>";
    
    return parse(template, params);
  }

}
