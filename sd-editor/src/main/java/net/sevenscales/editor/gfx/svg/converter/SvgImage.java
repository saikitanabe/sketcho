package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.gfx.domain.IImage;

import com.google.gwt.core.client.GWT;

public class SvgImage extends SvgBase {

  public static String svg(IImage image, int transformX, int transformY, EditorContext editorContext, boolean usesSchemeDefaultColors) {
    Map<String,String> params = new HashMap<String, String>();
    params.put("%x%", String.valueOf(image.getX() + transformX));
    params.put("%y%", String.valueOf(image.getY() + transformY + 3)); // some magic to put image in correct position :)
    params.put("%width%", String.valueOf(image.getWidth()));
    params.put("%height%", String.valueOf(image.getHeight()));

//    Debug.log("image.getSrc(): " + image.getSrc());

    // some url hacking to get confluence and Sketchboard.Me working
    String url = image.getSrc().replaceFirst(editorContext.get(EditorProperty.RESOURCES_PATH).toString(), "");
    
    params.put("%src%", GWT.getModuleBaseURL().replace("/" + GWT.getModuleName(), "/..") + url);
//    Debug.log("%src%: " + params.get("%src%"));
    
    String template = "<image x='%x%' y='%y%' width='%width%' height='%height%' " +
    		               "xlink:href='%src%' />";
    return parse(template, params, usesSchemeDefaultColors);
  }

}
