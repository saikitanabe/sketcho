package net.sevenscales.editor.uicomponents;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.api.MeasurementPanel;
import net.sevenscales.editor.content.utils.TokenParser;
import net.sevenscales.editor.content.utils.TokenParser.StringToken;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.base.GraphicsMouseDownHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseEnterHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseLeaveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseUpHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchEndHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsTouchStartHandler;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.IText;
import net.sevenscales.domain.ShapeProperty;
import net.sevenscales.domain.utils.SLogger;


public class TextElementHorizontalFormatUtil extends TextElementFormatUtil {
  private static final SLogger logger = SLogger.createLogger(TextElementHorizontalFormatUtil.class);

  private JavaScriptObject tokens;
  public static int DEFAULT_VERTICAL_TEXT_MARGIN = 0;
  // 11 is just the begging
  public static int DEFAULT_TOP_MARGIN = 11 + 10;

  private double prevTextHeight = 0;
  private IText text;
  private String textAnchor;

	public TextElementHorizontalFormatUtil(Diagram parent, HasTextElement hasTextElement, IGroup group, EditorContext editorContext) {
  	super(parent, hasTextElement, group, editorContext);
  }

  public void setText(String newText, boolean editable) {
  	setText(newText, editable, false);
  }

  @Override
  public void show() {
  	calculateLines2();
  	setTextShape();
  	super.show();
    // calculateAndNotifyHeight(hasTextElement.getWidth());
  }

  @Override
  public void reapplyText() {
    calculateLines2();
    cleanupAndApplyShape();
  }

  private void calculateLines2() {
    try {
      this.tokens = TokenParser.parse2(getText());
      clearLines();
      List<IShape> currentline = new ArrayList<IShape>();
      lines.add(currentline);

      text = createText(true);

      currentline.add(text);
      text.addText(
        tokens,
        hasTextElement.getX(),
        0,
        parent.isTextAlignCenter(),
        parent.isTextAlignRight()
      );
    } catch (Exception e) {
      logger.error("tokens: " + tokens, e);
    }
  }

  @Override
  public void setTextShape() {
    super.setTextShape();
    if (text != null) {
      applyTextAlignment(text, hasTextElement.getX());
    }
  }

  @Override
  protected void updateXPosition(IText text, ShapeProperty textAlign, int x, int width) {
    if (ShapeProperty.TXT_ALIGN_CENTER.equals(textAlign)) {
      text.updateTspanX(x + width / 2);
    } else if (ShapeProperty.TXT_ALIGN_RIGHT.equals(textAlign)) {
      text.updateTspanX(x + width);
    }
  }

  @Override
  protected int getStartX() {
    return 0;
  }

  @Override
  protected int getTextTop(int row) {
    return hasTextElement.getY();
  }

  private void resizeElement() {
    double width = text.getTextWidth();
    double height = text.getTextHeight();

    if (!editorContext.isTrue(EditorProperty.ON_OT_OPERATION) && hasTextElement.supportElementResize()) {
      // during OT operation element is not resized and everything is 
      // copied as is, element size and text
      hasTextElement.resize(hasTextElement.getX(), hasTextElement.getY(), (int) width, (int) height);
    }
  }

  public void setText(String newText, boolean editable, boolean force) {
  	// convert json text line (\\n) breaks to line breaks
//  	newText = newText.replaceAll("\\\\n", "\n");
    newText = newText.replaceAll("\\\\r", ""); // remove \r chars and handle only \n later
    // boolean changed = getText() != null && getText().equals(newText) ? false : true;
    // if (changed || force) {
    setText(newText);
  
    // if (force || editorContext.isTrue(EditorProperty.ON_SURFACE_LOAD) || editorContext.isTrue(EditorProperty.ON_OT_OPERATION)) {
    if (!editorContext.isTrue(EditorProperty.PROPERTY_EDITOR_IS_OPEN) || force) {
  	  calculateLines2();
      if (!editorContext.isTrue(EditorProperty.ON_OT_OPERATION) && !editorContext.isTrue(EditorProperty.ON_SURFACE_LOAD)) {
        // during OT operation element is NOT resized and everything is 
        // copied as is, element size and text
        // though in vertical case text needs to be recalculated based on element size
        // calculateAndNotifyHeight(hasTextElement.getWidth());
        resizeElement();
      }
      cleanupAndApplyShape();
    }
    // }

//    if ((hasTextElement.forceAutoResize()) && 
//   		 editorContext.get(EditorProperty.AUTO_RESIZE_ENABLED).equals(true)) {
//       if (hasTextElement.forceAutoResize() || hasTextElement.isAutoResize()) {
//            int width = (int) widestWidth+hasTextElement.getTextMargin(); // +margin
//         int height = getMarginTop() + tempLines.size() * ROW_HEIGHT + MARGIN_BOTTOM;
   
//            boolean changed = height != parent.getHeight();
//         hasTextElement.resize(hasTextElement.getX(), hasTextElement.getY(), hasTextElement.getWidth(), height);
         
//            if (changed && editorContext.isTrue(EditorProperty.ON_CHANGE_ENABLED)) {
//            	editorContext.getEventBus().fireEvent(new PotentialOnChangedEvent(parent));
//            }
//       }
//     }

//	    newest = new ScheduledText(getText());
//	    Scheduler.get().scheduleIncremental(newest);
//    }
  }

  private void cleanupAndApplyShape() {
    this.tokens = null; // cleanup some memory
    setTextShape();
  }

	private IText createText(boolean editable) {
    IText text = IShapeFactory.Util.factory(editable).createText(textGroup, true);
    text.setFontFamily(IText.SANS);
//    hasTextElement.addShape(text);
    text.setFill(hasTextElement.getTextColor());
    text.setAttribute("xml:space", "preserve");


    if (textAnchor != null) {
      setTextAnchor(text, textAnchor);
    }

//    if (token.fontWeight) {
//    	text.setFontWeight(IText.WEIGHT_BOLD);
//    } else {
//      text.setFontWeight(IText.WEIGHT_NORMAL);
//    }
//		text.setText(token.text);
    
    text.addGraphicsMouseDownHandler((GraphicsMouseDownHandler) hasTextElement.getGraphicsMouseHandler());
    text.addGraphicsMouseUpHandler((GraphicsMouseUpHandler) hasTextElement.getGraphicsMouseHandler());
    text.addGraphicsMouseMoveHandler((GraphicsMouseMoveHandler) hasTextElement.getGraphicsMouseHandler());
    text.addGraphicsMouseEnterHandler((GraphicsMouseEnterHandler) hasTextElement.getGraphicsMouseHandler());
    text.addGraphicsMouseLeaveHandler((GraphicsMouseLeaveHandler) hasTextElement.getGraphicsMouseHandler());
    
    text.addGraphicsTouchStartHandler((GraphicsTouchStartHandler) hasTextElement.getGraphicsMouseHandler());
    text.addGraphicsTouchMoveHandler((GraphicsTouchMoveHandler) hasTextElement.getGraphicsMouseHandler());
    text.addGraphicsTouchEndHandler((GraphicsTouchEndHandler) hasTextElement.getGraphicsMouseHandler());

		return text;
	}

  @Override
  public double getTextHeight() {
    double result = 0;
    if (lines.size() == 1 && lines.get(0).size() == 1) {
      IShape s = lines.get(0).get(0);
      if (s instanceof IText) {
        result = ((IText)s).getTextHeight();
      }
    }
    return result;
  }

  @Override
  public double getTextWidth() {
    return text.getTextWidth();
  }

  @Override
  public void alignMiddle() {
    textAnchor = "middle";
    // setTextAnchor(text, textAnchor);
  }

  @Override
  public boolean isAlignMiddle() {
    return textAnchor != null;
  }

  private void setTextAnchor(IText text, String textAnchor) {
    // text.setAttribute("style", "text-anchor:" + textAnchor);
  }

  @Override
  public boolean isMarkdownEditor() {
    return true;
  }

}
