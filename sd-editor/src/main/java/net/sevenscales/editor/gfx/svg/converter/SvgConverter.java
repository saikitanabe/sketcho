package net.sevenscales.editor.gfx.svg.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.safehtml.shared.SafeUri;

import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.utils.SortHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.GenericElement;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IChildElement;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.editor.api.impl.Theme;
import net.sevenscales.editor.api.impl.Theme.ThemeName;


public class SvgConverter {
	private static final SLogger logger = SLogger.createLogger(SvgConverter.class);
	private static final int MARGIN_WIDTH = 30;
	private static final int MARGIN_HEIGHT = 40;

  static {
    logger.addFilter(SvgConverter.class);
  }
	
	public enum ScaleSize {
		A4_PORTRATE(210, 297), A4_LANDSCAPE(297, 210), A3_PORTRATE(297, 420), A3_LANDSCAPE(420, 297), ORICINAL_SIZE(0, 0);
		
		private int width;
		private int height;

		private ScaleSize(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		public int getWidth() {
			return width;
		}
		public int getHeight() {
			return height;
		}
	}
	
	private ScaleSize scaleSize;
	
//  <?xml version="1.0" encoding="UTF-8"?>
//  <!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'
//            'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>
//  <svg xmlns:xlink="http://www.w3.org/1999/xlink" style="fill-opacity:1; color-rendering:auto; color-interpolation:auto; stroke:black; text-rendering:auto; stroke-linecap:square; stroke-miterlimit:10; stroke-opacity:1; shape-rendering:auto; fill:black; stroke-dasharray:none; font-weight:normal; stroke-width:1; font-family:&apos;Dialog&apos;; font-style:normal; stroke-linejoin:miter; font-size:12; stroke-dashoffset:0; image-rendering:auto;" width="500" height="700" xmlns="http://www.w3.org/2000/svg"
//  ><!--Generated by the Batik Graphics2D SVG Generator--><defs id="genericDefs"
//    /><g><g style="fill:rgb(240,240,202); stroke:rgb(240,240,202);"
//      ><rect x="350" width="100" height="30" y="220" style="stroke:none;"
//        /><rect x="350" y="220" width="100" style="fill:none; stroke:black;" height="30"
//      /></g
//    ></g
//  ></svg
//  >

//<?xml version="1.0"?>
//<svg xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg" width="100%" height="100%" viewBox="0 0 320 200">
//<circle cx="10" cy="10" r="5" fill="blue" stroke-width="1px" stroke="yellow"/>
//</svg>

  private String svgStart = "<?xml version='1.0' encoding='UTF-8'?>"+
          "<svg xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink' " +
          "preserveAspectRatio='xMinYMin meet' style='background-color:transparent;'";
  private String svgStartClose = ">";
  private String svgEnd = "</svg>";

  private int outerleft = Integer.MAX_VALUE;
  private int outerright = Integer.MIN_VALUE;
  private int outertop = Integer.MAX_VALUE;
  private int outerbottom = Integer.MIN_VALUE;

  /**
  * If true converts only selected elements; if false converts always all.
  * Highest precedence.
  */
  private boolean onlySelected;
  /**
  * Converts only items in filter of all if null;
  * Lower precedence after onlySelected.
  */
  private List<String> filter;
  
  public SvgConverter(boolean onlySelected) {
    this.onlySelected = onlySelected;
  	scaleSize = ScaleSize.ORICINAL_SIZE;
	}
  
  public SvgConverter(ScaleSize scaleSize, boolean onlySelected, List<String> filter) {
  	this.scaleSize = scaleSize;
    this.onlySelected = onlySelected;
    this.filter = filter;
  }

  /**
  * If any elements are selected, export only those to svg.
  */
  private Diagram[] getDiagrams(ISurfaceHandler surfaceHandler) {
    Diagram[] result = null;
    if (onlySelected) {
      Set<Diagram> selected = surfaceHandler.getSelectionHandler().getSelectedItems();
      if (onlySelected && selected.size() > 0) {
        result = SortHelpers.toArray(selected);
      }
    } 
    if (result == null) {
      // if no selection then result is still null
      if (filter != null && filter.size() > 0) {
        result = SortHelpers.toArray(surfaceHandler.getDiagrams(), filter);
      } else {
        result = SortHelpers.toArray(surfaceHandler.getDiagrams());
      }
    }

    return result;
  }

  public SvgData convertToSvg(IDiagramContent content, ISurfaceHandler surfaceHandler, boolean fontToChange, boolean absoluteUrl) {
  	EditorContext editorContext = surfaceHandler.getEditorContext();
    Diagram[] diagrams = getDiagrams(surfaceHandler);
    String items = "";
    
    if (surfaceHandler.getEditorContext().isEditable()) {
      ResizeHelpers.createResizeHelpers(surfaceHandler).hideGlobalElement();
    }
    
    List<List<IShape>> shapes = new ArrayList<List<IShape>>();
    for (Diagram d : diagrams) {
      if (!(d instanceof CircleElement)) {
        d.toSvgStart();
        if (surfaceHandler.getEditorContext().isEditable()) {
          d.unselect();
        }
        shapes.clear();
        shapes.add(d.getElements());

        // whole element can be used as link
        items += linkStart(d);
        // all shapes are under group
        IGroup group = d.getGroup();
        if (d.getDiagramItem().isComment()) {
          // comment should move with parent element; only parent group has been moved
          // NOTE child text element is living it's own life
          group = ((IChildElement) d).getParent().getGroup();
        }
        items += groupStart(group);

        // check if subgroup is started
        IGroup subgroup = null;
        if (d instanceof GenericElement) {
          subgroup = ((GenericElement) d).getSubgroup();
          items += groupStart(subgroup);
        }

        items += toSvg(d, shapes, editorContext, fontToChange, absoluteUrl);

        if (subgroup != null) {
          // close subgroup
          items += groupEnd();  
        }

        // text helper elements are not included in getElements
        List<List<IShape>> textElements = d.getTextElements();
        if (textElements != null) {
          items += toSvg(d, textElements, editorContext, fontToChange, absoluteUrl);
        }
        items += groupEnd();
        items += linkEnd(d);
        d.toSvgEnd();
      }
    }

		if (diagrams.length > 0) {
			outerleft -= 15;
			outertop -= 10;
			outerright += 15;
			outerbottom += 15;
		} else {
		  outerleft = 0;
		  outertop = 0;
		  outerright = 10;
		  outerbottom = 10;
		}

    items += waterMark(outerright, outerbottom);

//    System.out.println(outerleft+","+outertop+":"+outerright+","+outerbottom);
    SvgData result = new SvgData();
    result.width = outerright - outerleft;
    result.height = outerbottom-outertop;

//    String result = svgStart + " x='"+outerleft+"'"+" y='"+outertop+"'"+ " width='"+outerright+"' height='"+outerbottom+"'"+svgStartClose;
    if (fontToChange) {
      result.svg = svgStart + " viewBox='"+outerleft+" "+outertop+" "+(outerright-outerleft)+" "+(outerbottom-outertop)+"'" + " width='" + width() + "' height='" + height() + "'" + svgStartClose;
    } else {
      // Confluence svg rendering directly no page
      // neeed to scale according to widht provided for the diagram
      // same logic as plain img has when it scales

      // by default uses real width and height, no scaling
      int viewBoxWidth = (outerright-outerleft);
      int viewBoxHeight = (outerbottom-outertop);
      double svgWidth = viewBoxWidth;
      if (content.getWidth() > 0 && viewBoxWidth > content.getWidth()) {
        // content width 0 disables scaling to a certain width
        // scale according to parent div width
        svgWidth = content.getWidth();
        double sizeFactorial = svgWidth / viewBoxWidth;
        // scale height according to width factorial
        viewBoxHeight *= sizeFactorial;
      }
      logger.debug("viewBoxWidth {} viewBoxHeight {}", viewBoxWidth, viewBoxHeight);
      result.svg = svgStart + " viewBox='"+outerleft+" "+outertop+" " + viewBoxWidth + " " + viewBoxHeight + "'" + " width='" + svgWidth + "' height='" + viewBoxHeight + "'" + svgStartClose;
    }
//    result.svg = SafeHtmlUtils.htmlEscape(result.svg);
//    String result = svgStart + " width='100%' height='100%'"+svgStartClose;
    result.svg += items;
    result.svg += svgEnd;

    // logger.debug("result.svg: {}", result.svg);
    return result;
  }

  private native String waterMark(int right, int bottom)/*-{
    if (typeof $wnd.waterMark != 'undefined') {
      return $wnd.waterMark(right, bottom);
    }
    return "";
  }-*/;

  private String height() {
  	if (scaleSize == ScaleSize.ORICINAL_SIZE) {
  		return String.valueOf(outerbottom - outertop/* - outertop*/);
  	}
		return scaleSize.height - MARGIN_HEIGHT + "mm";
	}

	private String width() {
  	if (scaleSize == ScaleSize.ORICINAL_SIZE) {
    	return String.valueOf(outerright - outerleft/* - outerleft*/);
  	}
		return scaleSize.width - MARGIN_WIDTH + "mm";
	}

  // <g transform="matrix(1.00000000,0.00000000,0.00000000,1.00000000,0.00000000,0.00000000)" style="visibility: visible;"></g>
  private String groupStart(IGroup group) {
    String result = "<g";
    String matrix = group.getTransformMatrix();
    if (matrix != null) {
      result += " transform='" + matrix + "'";
    }
    result += ">";
    return result;
  }

  private String groupEnd() {
    return "</g>";
  }

  private String linkStart(Diagram d) {
    String result = "";
    if (d.hasLink()) {
      // This could be user modified straight in attachment (on Confluence)
      SafeUri url = UriUtils.fromString(d.getLink());
      String urlString = url.asString().replaceAll("&", "&amp;");
      result += "<a xlink:href='" + urlString + "'>";
    }
    return result;
  }

  private String linkEnd(Diagram d) {
    if (d.hasLink()) {
      return "</a>";
    } else {
      return "";
    }
  }

	private String toSvg(Diagram d, List<List<IShape>> shapes, EditorContext editorContext, boolean fontToChange, boolean absoluteUrl) {
  	String result = "";
    if (d.isVisible()) {
  	  // don't set read only state, because might not be visible
  	  // and in the end it would come visible
      d.setReadOnly(true);

    	int left = d.getLeft();
    	if (left < outerleft) {
    		outerleft = left;
    	}
    	int right = left + d.getWidth();
    	if (right > outerright) {
    		outerright = right;
    	}
    	int top = d.getTop();
    	if (top < outertop) {
    		outertop = top;
    	}
    	int bottom = top + d.getSvgHeightWithText();
    	if (bottom > outerbottom) {
    		outerbottom = bottom;
    	}

      for (List<IShape> line : shapes) {
	      for (IShape s : line) {
	        // convert to concrete svg shape with factory
	        if (s.isVisible()) {
	          String svg = SvgFactory.convert(s, 0, 0, editorContext, d, fontToChange, absoluteUrl);
	          result += svg;
	        }
	      }
	      d.setReadOnly(false);
      }
    }
    return result;
  }
	
}
