package net.sevenscales.editor.gfx.svg.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sevenscales.domain.api.IDiagramContent;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.ISurfaceHandler;
import net.sevenscales.editor.content.utils.SortHelpers;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.uicomponents.helpers.ResizeHelpers;
import net.sevenscales.editor.uicomponents.CircleElement;

public class SvgConverter {
	private static final SLogger logger = SLogger.createLogger(SvgConverter.class);
	private static final int MARGIN_WIDTH = 30;
	private static final int MARGIN_HEIGHT = 40;
	
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
  */
  private boolean onlySelected;
  
  public SvgConverter(boolean onlySelected) {
    this.onlySelected = onlySelected;
  	scaleSize = ScaleSize.ORICINAL_SIZE;
	}
  
  public SvgConverter(ScaleSize scaleSize, boolean onlySelected) {
  	this.scaleSize = scaleSize;
    this.onlySelected = onlySelected;
  }

  /**
  * If any elements are selected, export only those to svg.
  */
  private Diagram[] getDiagrams(ISurfaceHandler surfaceHandler) {
    Set<Diagram> selected = surfaceHandler.getSelectionHandler().getSelectedItems();
    if (onlySelected && selected.size() > 0) {
      return SortHelpers.sortDiagramItems(SortHelpers.toArray(selected));
    }

    return SortHelpers.sortDiagramItems(SortHelpers.toArray(surfaceHandler.getDiagrams()));
  }

  public SvgData convertToSvg(IDiagramContent content, ISurfaceHandler surfaceHandler) {
  	EditorContext editorContext = surfaceHandler.getEditorContext();
    Diagram[] diagrams = getDiagrams(surfaceHandler);
    String items = "";
    
    ResizeHelpers.createResizeHelpers(surfaceHandler).hideGlobalElement();
    
    List<List<IShape>> shapes = new ArrayList<List<IShape>>();
    for (Diagram d : diagrams) {
      if (!(d instanceof CircleElement)) {
        d.toSvgStart();
        d.unselect();
        shapes.clear();
        shapes.add(d.getElements());
        items += toSvg(d, shapes, editorContext);
        // text helper elements are not included in getElements
        List<List<IShape>> textElements = d.getTextElements();
        if (textElements != null) {
          items += toSvg(d, textElements, editorContext);
        }
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

//    System.out.println(outerleft+","+outertop+":"+outerright+","+outerbottom);
    SvgData result = new SvgData();
    result.width = outerright - outerleft;
    result.height = outerbottom-outertop;
//    String result = svgStart + " x='"+outerleft+"'"+" y='"+outertop+"'"+ " width='"+outerright+"' height='"+outerbottom+"'"+svgStartClose;
    result.svg = svgStart + " viewBox='"+outerleft+" "+outertop+" "+(outerright-outerleft)+" "+(outerbottom-outertop)+"'"+
    								" width='" + width() + "' height='" + height() + "'"+svgStartClose;
//    result.svg = SafeHtmlUtils.htmlEscape(result.svg);
//    String result = svgStart + " width='100%' height='100%'"+svgStartClose;
    result.svg += items;
    result.svg += svgEnd;

    // logger.debug("result.svg: {}", result.svg);
    return result;
  }
  
  private String height() {
  	if (scaleSize == ScaleSize.ORICINAL_SIZE) {
  		return String.valueOf(outerbottom-outertop);
  	}
		return scaleSize.height - MARGIN_HEIGHT + "mm";
	}

	private String width() {
  	if (scaleSize == ScaleSize.ORICINAL_SIZE) {
    	return String.valueOf(outerright - outerleft);
  	}
		return scaleSize.width - MARGIN_WIDTH + "mm";
	}

	private String toSvg(Diagram d, List<List<IShape>> shapes, EditorContext editorContext) {
  	String result = "";
    if (d.isVisible()) {
  	  // don't set read only state, because might not be visible
  	  // and in the end it would come visible
      d.setReadOnly(true);

    	int left = d.getLeft();
    	if (left < outerleft) {
    		outerleft = left;
    	}
    	int right = d.getLeft() + d.getWidth();
    	if (right > outerright) {
    		outerright = right;
    	}
    	int top = d.getTop();
    	if (top < outertop) {
    		outertop = top;
    	}
    	int bottom = d.getTop() + d.getHeight();
    	if (bottom > outerbottom) {
    		outerbottom = bottom;
    	}

      for (List<IShape> line : shapes) {
	      for (IShape s : line) {
	        // convert to concrete svg shape with factory
	        if (s.isVisible()) {
	          String svg = SvgFactory.convert(s, d.getTransformX(), d.getTransformY(), editorContext);
	          result += svg;
	        }
	      }
	      d.setReadOnly(false);
      }
    }
    return result;
  }
	
}
