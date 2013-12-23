package net.sevenscales.editor.uicomponents;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.base.GraphicsEventHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseDownHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseEnterHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseLeaveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseUpHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchEndHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchStartHandler;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.ILine;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.IText;

public class TextElementFormatUtil {
	private static final SLogger logger = SLogger.createLogger(TextElementFormatUtil.class);
	
  public final static int DEFAULT_MARGIN_TOP = 7;
  public final static int DEFAULT_MARGIN_BOTTOM = 14;
  public final static int ROW_HEIGHT = 17;

  public interface HasTextElement {
    int getY();
    int getX();
    int getWidth();
    int getHeight();

    void removeShape(IShape shape);

    void addShape(IShape shape);
    boolean isAutoResize();
    void resize(int x, int y, int width, int height);
    void setLink(String lik);
    String getLink();
    boolean supportsTitleCenter();
    int getTextMargin();
    boolean forceAutoResize();
    GraphicsEventHandler getGraphicsMouseHandler();
		String getTextColorAsString();
		boolean verticalAlignMiddle();
		boolean boldText();
  }
  
  /**
   * Default values.
   * @author saikitanabe
   *
   */
  public abstract static class AbstractHasTextElement implements HasTextElement {
  	@Override
  	public boolean verticalAlignMiddle() {
  		return false;
  	}
  	@Override
  	public boolean boldText() {
  		return true;
  	}
  }

  protected HasTextElement hasTextElement;
  // utility shape container to align text and make separators
//  protected List<IShape> innerShapes = new ArrayList<IShape>();
	protected List<List<IShape>> lines = new ArrayList<List<IShape>>(); 

  private String text;
  protected IGroup group;
  protected IGroup textGroup;
	protected EditorContext editorContext;
	protected Diagram parent;
	private boolean forceTextAlign;
	protected double widestWidth;
	private int marginTop;
  private int marginLeft;
	private int margin;
	private int marginBottom;
	private int fontSize = 12;
  private int degrees;
  
  public TextElementFormatUtil(Diagram parent, HasTextElement hasTextElement, IGroup group, EditorContext editorContext) {
  	this.parent = parent;
  	this.editorContext = editorContext;
    this.hasTextElement = hasTextElement;
    this.group = group;
    marginTop = DEFAULT_MARGIN_TOP;
    marginBottom = DEFAULT_MARGIN_BOTTOM;
    
//    group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    textGroup = IShapeFactory.Util.factory(true).createGroup(group);
  }
  
  public void applyTextColor() {
  	for (List<IShape> line : lines) {
  		for (IShape s : line) {
//	  		if (s instanceof IText) {
//			    IText t = (IText) s;
	//		    t.setStroke("#" + color.toHexString());
			    s.setFill(hasTextElement.getTextColorAsString());
//	  		}
	  		
	  		// difficult to see use default line color
//	  		else if (s instanceof ILine) {
//	  			s.setStroke(parent.getBorderColor());
//	  		}
  		}
  	}
  }
  
  public void setMarginTop(int marginTop) {
  	this.marginTop = marginTop;
  }
  public int getMarginTop() {
  	return marginTop;
  }

  public void setMarginLeft(int marginLeft) {
    this.marginLeft = marginLeft;
  }

  public int getMarginLeft() {
    return marginLeft;
  }
  
  public void setMargin(int margin) {
  	this.margin = margin;
  }
  public int getMargin() {
  	if (margin > 0) {
  		// default margin has been set
  		return margin;
  	}
  	return hasTextElement.getTextMargin();
  }
  
  public void setMarginBottom(int marginBottom) {
		this.marginBottom = marginBottom;
	}
  public int getMarginBottom() {
		return marginBottom;
	}
  
  public void setText(String newText, boolean editable) {
  	try {
    	_setText(newText, editable);
  	} catch (Exception e) {
  		logger.error("setText... failed", e);
  	}
  }

  private void _setText(String newText, boolean editable) {
    // convert json text line (\\n) breaks to line breaks
    newText = newText.replaceAll("\\\\n", "\n");
    newText = newText.replaceAll("\\\\r", "");
    boolean changed = text != null && text.equals(newText) ? false : true;
    
    createRows(newText, editable);

   if ((changed || hasTextElement.forceAutoResize()) && 
  		 editorContext.get(EditorProperty.AUTO_RESIZE_ENABLED).equals(true)) {
      if (hasTextElement.forceAutoResize() || hasTextElement.isAutoResize()) {
        resizeElement();
      }
    }
     
    setTextShape();
	}

  private void createRows(String newText, boolean editable) {
    text = newText;
    clearLines();
    widestWidth = 0;

    // split with \n
    // convert text to shapes
    // space is to keep last line if last is \n
    newText += " ";
    String[] texts = newText.split("\n");
    for (String t : texts) {
      t = t.trim();
      List<IShape> currentline = new ArrayList<IShape>();
      if (t.length()>0 && t.charAt(t.length()-1) == '\r') {
        // on windows line feeds are \r\n
        t = t.substring(0, t.length()-1);
      }
      if (t.equals("--")) {
        // create separator line
        ILine separator = IShapeFactory.Util.factory(editable).createLine(textGroup);
        separator.setStroke(hasTextElement.getTextColorAsString());
//        separator.setStrokeWidth(0.5);
//        separator.setFill(250, 250, 200, 2);
        
        separator.addGraphicsMouseDownHandler((GraphicsMouseDownHandler) hasTextElement.getGraphicsMouseHandler());
        separator.addGraphicsMouseUpHandler((GraphicsMouseUpHandler) hasTextElement.getGraphicsMouseHandler());
        separator.addGraphicsMouseMoveHandler((GraphicsMouseMoveHandler) hasTextElement.getGraphicsMouseHandler());
        separator.addGraphicsMouseEnterHandler((GraphicsMouseEnterHandler) hasTextElement.getGraphicsMouseHandler());
        separator.addGraphicsMouseLeaveHandler((GraphicsMouseLeaveHandler) hasTextElement.getGraphicsMouseHandler());
        
        separator.addGraphicsTouchStartHandler((GraphicsTouchStartHandler) hasTextElement.getGraphicsMouseHandler());
        separator.addGraphicsTouchMoveHandler((GraphicsTouchMoveHandler) hasTextElement.getGraphicsMouseHandler());
        separator.addGraphicsTouchEndHandler((GraphicsTouchEndHandler) hasTextElement.getGraphicsMouseHandler());

        currentline.add(separator);
//        hasTextElement.addShape(separator);
//        shapes.add(separator);
      } else {
        // method or argument section
         IText text = IShapeFactory.Util.factory(editable).createText(textGroup);
//         text.setFontWeight(IText.WEIGHT_NORMAL);
         text.setFontFamily(IText.SANS);
         text.setFontSize(fontSize + "px");
         t = t.replaceAll("<<", Character.toString('\u00AB'));
         t = t.replaceAll(">>", Character.toString('\u00BB'));
         
         // remove trailing and ending white spaces
         text.setText(t);
         currentline.add(text);
//         hasTextElement.addShape(text);
         text.setFill(hasTextElement.getTextColorAsString());
//         shapes.add(text);
         
         double textWidth = text.getTextWidth();

         widestWidth = textWidth > widestWidth ? textWidth : widestWidth; 

         text.addGraphicsMouseDownHandler((GraphicsMouseDownHandler) hasTextElement.getGraphicsMouseHandler());
         text.addGraphicsMouseUpHandler((GraphicsMouseUpHandler) hasTextElement.getGraphicsMouseHandler());
         text.addGraphicsMouseMoveHandler((GraphicsMouseMoveHandler) hasTextElement.getGraphicsMouseHandler());
         text.addGraphicsMouseEnterHandler((GraphicsMouseEnterHandler) hasTextElement.getGraphicsMouseHandler());
         text.addGraphicsMouseLeaveHandler((GraphicsMouseLeaveHandler) hasTextElement.getGraphicsMouseHandler());
         
         text.addGraphicsTouchStartHandler((GraphicsTouchStartHandler) hasTextElement.getGraphicsMouseHandler());
         text.addGraphicsTouchMoveHandler((GraphicsTouchMoveHandler) hasTextElement.getGraphicsMouseHandler());
         text.addGraphicsTouchEndHandler((GraphicsTouchEndHandler) hasTextElement.getGraphicsMouseHandler());
      }
      lines.add(currentline);
    }
  }

  public void reapplyText() {
    createRows(text, true);
    resizeElement();
    setTextShape();
  }

  private void resizeElement() {
    int width = getTextWidth();
    int rows = lines.size();
    int height = getMarginTop() + rows * ROW_HEIGHT + marginBottom;

    // only resize when size increases; currently disabled
//      width = width > rectSurface.getWidth() ? width : rectSurface.getWidth();
//      height = height > rectSurface.getHeight() ? height : rectSurface.getHeight();
    if (!editorContext.isTrue(EditorProperty.ON_OT_OPERATION)) {
      // during OT operation element is not resized and everything is 
      // copied as is, element size and text
      hasTextElement.resize(hasTextElement.getX(), hasTextElement.getY(), width, height);
    }
  }

  public void setRotate(int degrees) {
    this.degrees = degrees;
  }

	public void setTextShape() {
    int row = 1;
//    double rowHeight = 13.0;
    int separated = -1;
    for (List<IShape> line : lines) {
      IText prevtext = null;
	    for (IShape s : line) {
	      if (s instanceof ILine) {
	        ILine l = (ILine) s;
	        l.setShape(hasTextElement.getX() + 1, 
	        					 hasTextElement.getY() + (row * ROW_HEIGHT) + 4, 
	        				   hasTextElement.getX() + hasTextElement.getWidth() - 1, 
	        				   hasTextElement.getY() + (row * ROW_HEIGHT) + 4);
	        if (separated == -1) {
	          separated = row;
	        }
	      } else {
	        IText t = (IText) s;
	//        rowHeight = t.getTextHeight();
	        int x = hasTextElement.getX();
	        String align = IText.ALIGN_LEFT;
	        String weight = IText.WEIGHT_NORMAL;
	        if ((separated == -1 && hasTextElement.supportsTitleCenter()) || forceTextAlign) {
	          x += hasTextElement.getWidth()/2;
	          align = IText.ALIGN_CENTER;
	          // bold first segment if not stereo type
	          if (!t.getText().matches("\u00AB.*\u00BB") && hasTextElement.boldText()) {
	            weight = IText.WEIGHT_BOLD;
	  	        t.setFontWeight(weight);
	          }
	        } else {
	          x += 5;
	        }
	        
	        if (prevtext != null) {
	        	x = prevtext.getX() + (int) prevtext.getTextWidth() + 4;
	        }
	        int y = 0;
	        if (!hasTextElement.verticalAlignMiddle()) {
	        	y = hasTextElement.getY() + getMarginTop() + (row * (int) ROW_HEIGHT);
	        } else {
	        	// find middle and then find start y based on all lines
	        	y = hasTextElement.getY() + hasTextElement.getHeight() / 2 - (lines.size() * (int) ROW_HEIGHT / 2) + (row * (int) ROW_HEIGHT);
	        	y -= 5; // some base line align
	        }
	        		
	        t.setShape(x, y);
	        t.setAlignment(align);
          if (degrees != 0) {
            if (hasTextElement.boldText()) {
              t.setFontWeight(IText.WEIGHT_BOLD);
            }

            int twidth = (int) t.getTextWidth();
            // + 1 to y align little bit higher
            t.rotatedxdy(degrees, hasTextElement.getX(), hasTextElement.getY() + 1, -(hasTextElement.getHeight() / 2 + twidth / 2 + 5) , 0);
          }
	        prevtext = t;
	      }
	    }
	    prevtext = null;
      ++row;
    }
     
    // by default last line
//    int linkLine = lines.size() - 1;
//    if (separated - 1 >= 0) {
//      linkLine = separated - 1;
//    }
//    if (hasTextElement.getLink() != null) {
//      IText t = (IText) innerShapes.get(linkLine);
//      t.setStroke("#E18400");
//      t.setFill("#E18400");
//    }
 }
  
  public String getText() {
    return text;
  }
  
  protected void setText(String text) {
  	this.text = text;
  }
  
  protected void clearLines() {
  	for (List<IShape> line : lines) {
	    for (IShape ltext : line) {
	    	ltext.remove();
		  }
  	}
	  lines.clear();
  }

	public void hide() {
    textGroup.setVisible(false);
	}

	public void show() {
    textGroup.setVisible(true);
	}

  public void setVisible(boolean visible) {
    textGroup.setVisible(visible);
  }

	public void applyTransformToShape(int dx, int dy) {
  	for (List<IShape> line : lines) {
	    for (IShape ltext : line) {
	    	ltext.applyTransformToShape(dx, dy);
	    }
  	}
	}

	public void setForceTextAlign(boolean forceTextAlign) {
		this.forceTextAlign = forceTextAlign;
	}

	public int getTextWidth() {
		return (int) widestWidth + getMargin(); // +margin
	}
	
	public int getTextHeight() {
		return (lines.size() + 1) * ROW_HEIGHT;
	}
	
	public List<List<IShape>> getLines() {
		return lines;
	}

	public void setFontSize(int fontSize) {
		this.fontSize  = fontSize;
	}

  public void remove() {
    clearLines();
    textGroup.remove();
  }

}
