package net.sevenscales.editor.gfx.svg.converter;

import java.util.HashMap;
import java.util.Map;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.ImageElement;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.gfx.domain.IImage;

import com.google.gwt.core.client.GWT;

public class SvgImage extends SvgBase {

  public static String svg(IImage image, int transformX, int transformY, EditorContext editorContext, Diagram diagram, boolean absoluteUrl) {
    Map<String,String> params = new HashMap<String, String>();
    params.put("%x%", String.valueOf(image.getX() + transformX));
    params.put("%y%", String.valueOf(image.getY() + transformY + 3)); // some magic to put image in correct position :)
    params.put("%width%", String.valueOf(diagram.getWidth()));
    params.put("%height%", String.valueOf(diagram.getHeight()));

    String url = "";
    // printing flag didn't work, and after expired, images are no longer visible on print page
    // if (editorContext.isTrue(EditorProperty.PRINTING)) {
      // just use browser cached images
      // url = image.getSrc();
    // } else
    if (diagram instanceof ImageElement && absoluteUrl) {
      url = ((ImageElement) diagram).getImageAbsoluteUrl();
    } else if (diagram instanceof ImageElement) {
      url = ((ImageElement) diagram).getImageUrl();
    } else {
      // some url hacking to get confluence and Sketchboard.Me working
      url = image.getSrc().replaceFirst(editorContext.get(EditorProperty.RESOURCES_PATH).toString(), "");
      url = GWT.getModuleBaseURL().replace("/" + GWT.getModuleName(), "/..") + url;
    }
    url = url.replaceAll("&", "&amp;");
    params.put("%src%", url);
    
    String template = "<image x='%x%' y='%y%' width='%width%' height='%height%' " +
    		               "xlink:href=\"%src%\" />";
    return parse(image, template, params, diagram);
  }

}
