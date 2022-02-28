package net.sevenscales.editor.uicomponents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.util.logging.Level;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.logging.client.LogConfiguration;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.constants.Constants;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.Tools;
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
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.Promise;
import net.sevenscales.editor.gfx.domain.ElementSize;

public class TextElementFormatUtil {
	private static final SLogger logger = SLogger.createLogger(TextElementFormatUtil.class);
	
  public final static int DEFAULT_MARGIN_TOP = 7;
  public final static int DEFAULT_MARGIN_BOTTOM = 14;
  // Legacy font height based row height, e.g. properties editor row height to calculate text area height
  public static final int ROW_HEIGHT = 17;
  private static final int START_X = 10;

  // private static final float ROW_HEIGHT_FACTORIAL = 1.4f;
  // private static final float MARGIN_HEIGHT_FACTORIAL = 0.85f;

  // private int rowHeight = ROW_HEIGHT;
  private static final Map<Integer, FontProperty> fontToRowSizeMap;
  protected FontProperty fontProperty;

  protected static class FontProperty {
    public int rowHeight;
    public int marginTop;
    public int marginBottom;
    public int marging;

    FontProperty(int rowHeight, int marginTop, int marginBottom, int marging) {
      this.rowHeight = rowHeight;
      this.marginTop = marginTop;
      this.marginBottom = marginBottom;
      this.marging = marging;
    }
  }

  private static final int REFERENCE_FONT_SIZE = 12;
  private static final int REF_MARGIN = 45;
  private static final int REF_MARGIN_TOP = 8;

  static {
    fontToRowSizeMap = new HashMap<Integer, FontProperty>();

    fontToRowSizeMap.put(8, new FontProperty(13, 5, 12, 26));
    fontToRowSizeMap.put(9, new FontProperty(14, 5, 13, 27));
    fontToRowSizeMap.put(10, new FontProperty(15, 6, 14, 28));
    fontToRowSizeMap.put(11, new FontProperty(16, 6, 15, 29));
    fontToRowSizeMap.put(12, new FontProperty(17, 7, 14, 30));
    fontToRowSizeMap.put(14, new FontProperty(19, 8, 18, 35));
    fontToRowSizeMap.put(18, new FontProperty(25, 8, 20, 48));
    fontToRowSizeMap.put(24, new FontProperty(31, 8, 25, 58));
    fontToRowSizeMap.put(30, new FontProperty(37, 8, 30, 64));
    fontToRowSizeMap.put(36, new FontProperty(43, 8, 32, 75));
    fontToRowSizeMap.put(48, new FontProperty(55, 8, 36, 84));
    fontToRowSizeMap.put(60, new FontProperty(67, 8, 44, 103));
    // fontToRowSizeMap.put(72, new FontProperty(82, 8, 60, 105));
    // fontToRowSizeMap.put(96, new FontProperty(75, 8, 70, 112));

    // createMapping(8);
    // createMapping(9);
    // createMapping(10);
    // createMapping(11);
    // createMapping(12);
    // createMapping(14);
    // createMapping(18);
    // createMapping(24);
    // createMapping(30);
    // createMapping(36);
    // createMapping(48);
    // createMapping(60);
  }

  private static void createMapping(int fontSize) {
    double factorial = (double) fontSize/ (double) REFERENCE_FONT_SIZE;
    int topextra = (fontSize - REFERENCE_FONT_SIZE);
    if (topextra > 0) {
      // need to adjust for below ref font top margin
      topextra = 0;
    }
    FontProperty fp = new FontProperty((int)(ROW_HEIGHT * factorial), 
                                       REF_MARGIN_TOP + topextra, 
                                       (int)(ROW_HEIGHT * factorial), 
                                       (int)(REF_MARGIN * factorial));
    fontToRowSizeMap.put(fontSize, fp);
  }

  public interface HasTextElement {
    int getY();
    int getX();
    int getWidth();
    int getHeight();
    int getMarginLeft();
    int getMarginTop();
    int getMarginBottom();
    boolean isAutoResize();
    void resize(int x, int y, int width, int height);
    void resizeHeight(int height);
    void resizeWidthHeight(int width, int height);
    void setLink(String lik);
    String getLink();
    boolean supportsTitleCenter();
    int getTextMargin(int fontSize);
    boolean forceAutoResize();
    GraphicsEventHandler getGraphicsMouseHandler();
		Color getTextColor();
    boolean verticalAlignMiddle();
		boolean centeredText();
		boolean boldText();
    boolean supportElementResize();
    boolean isSketchiness();
  }
  
  /**
   * Default values.
   * @author saikitanabe
   *
   */
  public abstract static class AbstractHasTextElement implements HasTextElement {
    private Diagram diagram;

    public AbstractHasTextElement(Diagram diagram) {
      this.diagram = diagram;
    }

  	@Override
  	public boolean verticalAlignMiddle() {
  		return false;
  	}
  	@Override
  	public boolean boldText() {
  		return true;
  	}
    public int getTextMargin(int defaultMargin) {
      return 0;
    }
    public boolean supportElementResize() {
      boolean result = true;
      if (ShapeProperty.isShapeAutoResizeDisabled(diagram.getDiagramItem().getShapeProperties())) {
        result = false;
      }
      return result;
    }
    public int getMarginLeft() {
      return 0;
    }
    public int getMarginTop() {
      return 0;
    }
    public int getMarginBottom() {
      return 0;
    }
    public void resizeHeight(int height) {
    }
    public void resizeWidthHeight(int width, int height) {
    }
    public boolean isSketchiness() {
      return false;
    }
    public boolean centeredText() {
      return false;
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
	private int marginBottom;
  // marginTop is used if this is defined
  private boolean marginTopDefined;
  private boolean marginBottomDefined;
  private int marginLeft;
	private int margin;
  private boolean marginDefined;
	// private int marginBottom;
	private int fontSize = 12;
  private int degrees;
  protected IText textElement;
  
  public TextElementFormatUtil(Diagram parent, HasTextElement hasTextElement, IGroup group, EditorContext editorContext) {
  	this.parent = parent;
  	this.editorContext = editorContext;
    this.hasTextElement = hasTextElement;
    this.group = group;

    // marginTop = DEFAULT_MARGIN_TOP;
    // marginBottom = DEFAULT_MARGIN_BOTTOM;
    
//    group = IShapeFactory.Util.factory(editable).createGroup(surface.getElementLayer());
    textGroup = IShapeFactory.Util.factory(true).createGroup(group);
    textGroup.setAttribute("class", "shapebase");
    fontProperty = fontToRowSizeMap.get(12);

    textElement = IShapeFactory.Util.factory(true).createText(textGroup, false);
    textElement.setHorizontal(isHorizontal());
    textElement.setShapeProperties(
      parent.getDiagramItem().getShapeProperties(),
      parent.getDiagramItem().getType(),
      Tools.isSketchMode()
    );
    textElement.setText(parent.getDiagramItem().getText());
    // textElement.setPosition(hasTextElement.getX(), hasTextElement.getY());

    // textElement.setProperties(
    //   10,
    //   11,
    //   getMarginBottom(),
    //   hasTextElement.getWidth()
    // );
    
    // textElement.setShapeSize(hasTextElement.getWidth(), hasTextElement.getHeight());

    if (editorContext.isEditable()) {
      textElement.addGraphicsMouseDownHandler((GraphicsMouseDownHandler) hasTextElement.getGraphicsMouseHandler());
      textElement.addGraphicsMouseUpHandler((GraphicsMouseUpHandler) hasTextElement.getGraphicsMouseHandler());
      textElement.addGraphicsMouseMoveHandler((GraphicsMouseMoveHandler) hasTextElement.getGraphicsMouseHandler());
      textElement.addGraphicsMouseEnterHandler((GraphicsMouseEnterHandler) hasTextElement.getGraphicsMouseHandler());
      textElement.addGraphicsMouseLeaveHandler((GraphicsMouseLeaveHandler) hasTextElement.getGraphicsMouseHandler());
      
      textElement.addGraphicsTouchStartHandler((GraphicsTouchStartHandler) hasTextElement.getGraphicsMouseHandler());
      textElement.addGraphicsTouchMoveHandler((GraphicsTouchMoveHandler) hasTextElement.getGraphicsMouseHandler());
      textElement.addGraphicsTouchEndHandler((GraphicsTouchEndHandler) hasTextElement.getGraphicsMouseHandler());
    }
  }

  protected boolean isHorizontal() {
    return true;
  }
  
  public void applyTextColor() {
    textElement.setColor(hasTextElement.getTextColor());
  	// for (List<IShape> line : lines) {
  	// 	for (IShape s : line) {
    //     s.setFill(hasTextElement.getTextColor());
  	// 	}
  	// }
  }

  protected void applyTextAlignment(final IText text, final int x) {
    ShapeProperty textAlign = null;
    if (ShapeProperty.isTextAlignCenter(parent.getDiagramItem().getShapeProperties())) {
      text.setTextTspanAlignCenter();
      textAlign = ShapeProperty.TXT_ALIGN_CENTER;
    } else if (ShapeProperty.isTextAlignRight(parent.getDiagramItem().getShapeProperties())) {
      text.setTextTspanAlignRight();
      textAlign = ShapeProperty.TXT_ALIGN_RIGHT;
    } else {
      textAlign = ShapeProperty.TXT_ALIGN_LEFT;
    }

    final ShapeProperty _textAlign = textAlign;

    if (textAlign != null) {
      text.getTextSize().then(new Promise.FunctionParam<ElementSize>() {
        public void accept(ElementSize size) {
          updateXPosition(text, _textAlign, x, (int) size.getWidth());
        }
      });
    }
  }

  protected void updateXPosition(IText text, ShapeProperty textAlign, int x, int width) {
  }
  
  public void setMarginTop(int marginTop) {
    marginTopDefined = true;
  	this.marginTop = marginTop;
  }
  public int getMarginTop() {
    if (marginTopDefined) {
      return marginTop;
    }

    int _marginTop = hasTextElement.getMarginTop();
    if (_marginTop > 0) {
      return _marginTop;
    }

    // otherwise use default marginTop
  	return fontProperty.marginTop;
  }

  public void setMarginLeft(int marginLeft) {
    this.marginLeft = marginLeft;
  }

  public int getMarginLeft() {
    return marginLeft;
  }
  
  public void setMargin(int margin) {
    marginDefined = true;
  	this.margin = margin;
  }
  public int getMargin() {
    if (marginDefined) {
      return margin;
    }
    if (hasTextElement.getTextMargin(fontProperty.marging) > 0) {
      return hasTextElement.getTextMargin(fontProperty.marging);
    }
		return fontProperty.marging;
  }
  
  public void setMarginBottom(int marginBottom) {
    marginBottomDefined = true;
    this.marginBottom = marginBottom;
	}
  public int getMarginBottom() {
		// return marginBottom;
    int marginBottom = hasTextElement.getMarginBottom();
    if (marginBottom > 0) {
      return marginBottom;
    }
    return 0;
	}

  public void setText(String newText, boolean editable, boolean force) {
    setText(newText, editable);
  }

  private void setText(String newText, boolean editable) {
  	try {
      _setText(newText, editable);
  	} catch (Exception e) {
  		logger.error("setText... failed", e);
  	}
  }

  private String handleLineBreaks(String newText) {
    newText = newText.replaceAll("\\\\n", "\n");
    newText = newText.replaceAll("\\\\r", "");
    return newText;
  }

  private boolean textChanged(String newText) {
    return text != null && text.equals(newText) ? false : true;
  }

  protected void setText(String text) {
  	// this.text = text;

    text = handleLineBreaks(text);

    textElement.setText(text);
    textElement.setColor(hasTextElement.getTextColor());
    textElement.setBorderColor(parent.getBorderColor());
    textElement.setFontSize(fontSize + "px");
    textElement.setShapeProperties(
      parent.getDiagramItem().getShapeProperties(),
      parent.getDiagramItem().getType(),
      Tools.isSketchMode()
    );
  }

  private void _setText(String newText, boolean editable) {
    // convert json text line (\\n) breaks to line breaks
    // newText = handleLineBreaks(newText);

    // textElement.setText(newText);
    // textElement.setColor(hasTextElement.getTextColor());
    // textElement.setBorderColor(parent.getBorderColor());
    // textElement.setShapeProperties(parent.getDiagramItem().getShapeProperties());
    boolean changed = textChanged(newText);

    setText(newText);

    if ((changed || hasTextElement.forceAutoResize()) && 
  		 editorContext.isTrue(EditorProperty.AUTO_RESIZE_ENABLED)) {
      if (hasTextElement.forceAutoResize() || hasTextElement.isAutoResize()) {
        resizeElement();
      }
    }
     
    // setTextShape();
	}

//   private void createRows(String newText, boolean editable) {
//     text = newText;
//     clearLines();
//     widestWidth = 0;
//     newText += " ";
//     String[] texts = newText.split("\n");
//     for (String t : texts) {
//       t = t.trim();
//       List<IShape> currentline = new ArrayList<IShape>();
//       if (t.length()>0 && t.charAt(t.length()-1) == '\r') {
//         // on windows line feeds are \r\n
//         t = t.substring(0, t.length()-1);
//       }
//       if (t.equals("--") || t.equals("—")) {
//         // create separator line
//         // iPad converts -- to —
//         ILine separator = IShapeFactory.Util.factory(editable).createLine(textGroup);
//         separator.setStroke(parent.getBorderColor());
//         // TODO isSketchiness()
//         if (Tools.isSketchMode()) {
//           separator.setStrokeWidth(Constants.SKETCH_SEPARATOR_WEIGHT);
//         }
// //        separator.setFill(250, 250, 200, 2);
        
//         separator.addGraphicsMouseDownHandler((GraphicsMouseDownHandler) hasTextElement.getGraphicsMouseHandler());
//         separator.addGraphicsMouseUpHandler((GraphicsMouseUpHandler) hasTextElement.getGraphicsMouseHandler());
//         separator.addGraphicsMouseMoveHandler((GraphicsMouseMoveHandler) hasTextElement.getGraphicsMouseHandler());
//         separator.addGraphicsMouseEnterHandler((GraphicsMouseEnterHandler) hasTextElement.getGraphicsMouseHandler());
//         separator.addGraphicsMouseLeaveHandler((GraphicsMouseLeaveHandler) hasTextElement.getGraphicsMouseHandler());
        
//         separator.addGraphicsTouchStartHandler((GraphicsTouchStartHandler) hasTextElement.getGraphicsMouseHandler());
//         separator.addGraphicsTouchMoveHandler((GraphicsTouchMoveHandler) hasTextElement.getGraphicsMouseHandler());
//         separator.addGraphicsTouchEndHandler((GraphicsTouchEndHandler) hasTextElement.getGraphicsMouseHandler());

//         currentline.add(separator);
// //        hasTextElement.addShape(separator);
// //        shapes.add(separator);
//       } else {
//         // method or argument section
//          IText text = IShapeFactory.Util.factory(editable).createText(textGroup);
// //         text.setFontWeight(IText.WEIGHT_NORMAL);
//          text.setFontFamily(IText.SANS);
//          text.setFontSize(fontSize + "px");
//          t = t.replaceAll("<<", Character.toString('\u00AB'));
//          t = t.replaceAll(">>", Character.toString('\u00BB'));
         
//          text.setText(t);
//          currentline.add(text);
// //         hasTextElement.addShape(text);
//          text.setFill(hasTextElement.getTextColor());
// //         shapes.add(text);
         
//          double textWidth = text.getTextWidth();

//          widestWidth = textWidth > widestWidth ? textWidth : widestWidth;

//          if (editorContext.isEditable()) {
//            text.addGraphicsMouseDownHandler((GraphicsMouseDownHandler) hasTextElement.getGraphicsMouseHandler());
//            text.addGraphicsMouseUpHandler((GraphicsMouseUpHandler) hasTextElement.getGraphicsMouseHandler());
//            text.addGraphicsMouseMoveHandler((GraphicsMouseMoveHandler) hasTextElement.getGraphicsMouseHandler());
//            text.addGraphicsMouseEnterHandler((GraphicsMouseEnterHandler) hasTextElement.getGraphicsMouseHandler());
//            text.addGraphicsMouseLeaveHandler((GraphicsMouseLeaveHandler) hasTextElement.getGraphicsMouseHandler());
           
//            text.addGraphicsTouchStartHandler((GraphicsTouchStartHandler) hasTextElement.getGraphicsMouseHandler());
//            text.addGraphicsTouchMoveHandler((GraphicsTouchMoveHandler) hasTextElement.getGraphicsMouseHandler());
//            text.addGraphicsTouchEndHandler((GraphicsTouchEndHandler) hasTextElement.getGraphicsMouseHandler());
//          }
//       }
//       lines.add(currentline);
//     }
//   }

  public void applyBorderColor(Color color) {
    textElement.setBorderColor(parent.getBorderColor());

    // for (List<IShape> line : lines) {
    //   for (IShape s : line) {
    //     if (s instanceof ILine) {
    //       s.setStroke(color);
    //     }
    //   }
    // }
  }

  public void reapplyText() {
    // createRows(text, true);
    resizeElement();
    textElement.setShapeProperties(
      parent.getDiagramItem().getShapeProperties(),
      parent.getDiagramItem().getType(),
      Tools.isSketchMode()
    );
    textElement.reapplyText();
    // setTextShape();
  }

  protected void resizeElement() {
    // only resize when size increases; currently disabled
//      width = width > rectSurface.getWidth() ? width : rectSurface.getWidth();
//      height = height > rectSurface.getHeight() ? height : rectSurface.getHeight();
    if (!editorContext.isTrue(EditorProperty.ON_OT_OPERATION) && hasTextElement.supportElementResize()) {
      // during OT operation element is not resized and everything is 
      // copied as is, element size and text
      // double width = getTextWidth() + getMargin();

      // resize parent as deferred since
      // react component has not yet updated its
      // content, this appears when switching
      // shape in place
      // - first new shape text is set, like default 'Activity'
      // - then custom text is set but it is not yet on DOM
      // Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			// 	public void execute() {

        textElement.getTextSize().then((ElementSize size) -> {
            // double height = getMarginTop() + getTextHeight() + fontProperty.marginBottom + getMarginBottom();
            // double height = getTextHeight();
            // int width = (int) ((ElementSize)size).getWidth();
            // int height = (int) ((ElementSize)size).getHeight();
            int width = (int) size.getWidth();
            int height = (int) size.getHeight();
            hasTextElement.resize(hasTextElement.getX(), hasTextElement.getY(), width, height);
        });
      //   }
			// });
    }
  }

  

  public void setRotate(int degrees) {
    this.degrees = degrees;
    textElement.setRotate(degrees);
  }

  public int middleY(int row) {
    // return hasTextElement.getY() + hasTextElement.getMarginTop() + hasTextElement.getHeight() / 2 - (lines.size() * fontProperty.rowHeight / 2) + (row * fontProperty.rowHeight);
    return hasTextElement.getY() + 
           hasTextElement.getMarginTop() + 
           hasTextElement.getHeight() / 2 + //(lines.size() * fontProperty.rowHeight / 2) + 
           (row * fontProperty.rowHeight);
  }

	public void setTextShape() {
//     int row = 1;
// //    double rowHeight = 13.0;
//     int separated = -1;
//     for (List<IShape> line : lines) {
//       IText prevtext = null;
// 	    for (IShape s : line) {
// 	      if (s instanceof ILine) {
// 	        ILine l = (ILine) s;
//           int y = hasTextElement.getY() + (row * fontProperty.rowHeight) + 4;
//           if (hasTextElement.verticalAlignMiddle()) {
//             // some mystical - 8 to get line little bit higher
//             y = middleY(row) - 8;
//           }
// 	        l.setShape(hasTextElement.getX() + 1, 
// 	        					 y, 
// 	        				   hasTextElement.getX() + hasTextElement.getWidth() - 1, 
// 	        				   y);
// 	        if (separated == -1) {
// 	          separated = row;
// 	        }
// 	      } else {
// 	        IText t = (IText) s;
// 	//        rowHeight = t.getTextHeight();
// 	        int x = hasTextElement.getX();
// 	        String align = IText.ALIGN_LEFT;
// 	        String weight = IText.WEIGHT_NORMAL;
// 	        if ((separated == -1 && hasTextElement.supportsTitleCenter()) || forceTextAlign) {
// 	          x += hasTextElement.getWidth()/2;
// 	          align = IText.ALIGN_CENTER;
// 	          // bold first segment if not stereo type
// 	          if (!t.getText().matches("\u00AB.*\u00BB") && hasTextElement.boldText()) {
// 	            weight = IText.WEIGHT_BOLD;
// 	  	        t.setFontWeight(weight);
// 	          }
// 	        } else if (hasTextElement.centeredText()) {
//             x += hasTextElement.getWidth()/2;
//             align = IText.ALIGN_CENTER;
//           } else {
// 	          x += getStartX();
// 	        }
	        
// 	        if (prevtext != null) {
// 	        	x = prevtext.getX() + (int) prevtext.getTextWidth() + 4;
// 	        }
// 	        int y = 0;
// 	        if (!hasTextElement.verticalAlignMiddle()) {
//             y = getTextTop(row);
// 	        } else {
// 	        	// find middle and then find start y based on all lines
// 	        	y = middleY(row);
// 	        	y -= 5; // some base line align
// 	        }
	        		
// 	        t.setShape(x, y);
// 	        t.setAlignment(align);
//           if (degrees != 0) {
//             if (hasTextElement.boldText()) {
//               t.setFontWeight(IText.WEIGHT_BOLD);
//             }

//             int twidth = (int) t.getTextWidth();
//             align = IText.ALIGN_LEFT;
//             // + 1 to y align little bit higher
//             t.rotatedxdy(degrees, hasTextElement.getX(), hasTextElement.getY() + 1, -(hasTextElement.getHeight() / 2 + twidth / 2 + 5) , 0);
//           }
// 	        prevtext = t;
// 	      }
// 	    }
// 	    prevtext = null;
//       ++row;
//     }
     
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

  protected int getStartX() {
    return START_X;
  }

  protected int getTextTop(int row) {
    return hasTextElement.getY() + getMarginTop() + (row * fontProperty.rowHeight);
  }
  
  public String getText() {
    return textElement.getText();
  }

  public void setStoreText(String text) {
    // setText(text);
  }
    
  protected void clearLines() {
  	// for (List<IShape> line : lines) {
	  //   for (IShape ltext : line) {
	  //   	ltext.remove();
		//   }
  	// }
	  // lines.clear();
  }

	public void hide() {
    textGroup.setVisible(false);
	}

	public void show() {
    textGroup.setVisible(true);
    // show is called after editingEnded and
    // shape shape size needs to be updated according to 
    // text size.
    textElement.setShapeSize(parent.getWidth(), parent.getHeight());
	}

  public void setVisible(boolean visible) {
    textGroup.setVisible(visible);
  }

	public void applyTransformToShape(int dx, int dy) {
  	// for (List<IShape> line : lines) {
	  //   for (IShape ltext : line) {
	  //   	ltext.applyTransformToShape(dx, dy);
	  //   }
  	// }
	}

	public void setForceTextAlign(boolean forceTextAlign) {
		this.forceTextAlign = forceTextAlign;
	}

	public Promise getTextSize() {
		// return widestWidth + getMargin(); // +margin
    return textElement.getTextSize();
	}
	
	// public double getTextHeight() {
	// 	// return (lines.size() + 1) * fontProperty.rowHeight;
  //   return textElement.getTextHeight();
  // }
  
  public boolean isSupportFontSize() {
    return true;
  }

  public int getFontSize() {
    return fontSize;
  }
	
	public List<List<IShape>> getLines() {
		return lines;
	}

	public void setFontSize(int fontSize) {
		this.fontSize  = fontSize;
    FontProperty fp = fontToRowSizeMap.get(fontSize);
    if (fp != null) {
      fontProperty = fp;
    }
    if (LogConfiguration.loggingIsEnabled(Level.FINEST) && fontProperty == null) {
      throw new RuntimeException("fontProperty is null");
    }

    // rowHeight = (int) (fontSize * ROW_HEIGHT_FACTORIAL);
    // marginBottom = (int) (rowHeight * MARGIN_HEIGHT_FACTORIAL);
    // margin = (int) rowHeight;
	}

  public void setTextAlign(Integer textAlign) {

  }

  public void remove() {
    // clearLines();
    textGroup.remove();
  }

  public void alignMiddle() {

  }

  public boolean isAlignMiddle() {
    return false;
  }

  public boolean isMarkdownEditor() {
    return false;
  }

  public void resizeEnd() {
    // textElement.resizeEnd();
  }

  public void setShapeSize(int width, int height) {
    textElement.setShapeProperties(
      parent.getDiagramItem().getShapeProperties(),
      parent.getDiagramItem().getType(),
      Tools.isSketchMode()
    );
    textElement.setShapeSize(width, height);
  }

}
