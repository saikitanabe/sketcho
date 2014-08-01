package net.sevenscales.editor.gfx.svg.converter;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.gfx.domain.ICircle;
import net.sevenscales.editor.gfx.domain.IEllipse;
import net.sevenscales.editor.gfx.domain.IImage;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IPolyline;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IText;

public class SvgFactory {

  public static String convert(IShape s, int transformX, int transformY, EditorContext editorContext, Diagram diagram, boolean fontToChange, boolean absoluteUrl) {
    String result = "";
    if (s instanceof IRectangle) {
      IRectangle r = (IRectangle) s;
      result = SvgRect.svg(r, transformX, transformY, diagram);
    } else if (s instanceof IText) {
      IText t = (IText)s;
      result = SvgText.svg(t, transformX, transformY, diagram, fontToChange);
    } else if (s instanceof ILine) {
      ILine l = (ILine) s;
      result = SvgLine.svg(l, transformX, transformY, diagram);
    } else if (s instanceof IPolyline) {
      IPolyline p = (IPolyline) s;
      result = SvgPolyline.svg(p, transformX, transformY, diagram);
    } else if (s instanceof ICircle) {
      ICircle c = (ICircle) s;
      result = SvgCircle.svg(c, transformX, transformY, diagram);
    } else if (s instanceof IEllipse) {
      IEllipse e = (IEllipse) s;
      result = SvgEllipse.svg(e, transformX, transformY, diagram);
    } else if (s instanceof IImage) {
    	IImage i = (IImage) s;
    	result = SvgImage.svg(i, transformX, transformY, editorContext, diagram, absoluteUrl);
    } else if (s instanceof IPath) {
      IPath p = (IPath) s;
      result = SvgPath.svg(p, transformX, transformY, diagram);
    }
    return result;
  }

}
