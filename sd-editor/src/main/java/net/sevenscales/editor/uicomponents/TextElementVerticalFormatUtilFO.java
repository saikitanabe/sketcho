package net.sevenscales.editor.uicomponents;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.content.utils.TokenParser;
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
import net.sevenscales.editor.gfx.domain.Promise;
import net.sevenscales.editor.gfx.domain.ElementSize;


public class TextElementVerticalFormatUtilFO extends TextElementFormatUtil {
  private static final SLogger logger = SLogger.createLogger(TextElementVerticalFormatUtilFO.class);

  public static int DEFAULT_VERTICAL_TEXT_MARGIN = 0;
  // 11 is just the begging
  public static int DEFAULT_TOP_MARGIN = 11 + 10;

  private double prevTextHeight = 0;

	public TextElementVerticalFormatUtilFO(
    Diagram parent,
    HasTextElement hasTextElement,
    IGroup group,
    EditorContext editorContext
  ) {
  	super(parent, hasTextElement, group, editorContext);
  }

  @Override
  protected boolean isHorizontal() {
    return false;
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
  public boolean isSupportFontSize() {
    return false;
  }

  private void calculateLines2() {
    try {
      // this.tokens = TokenParser.parse2(getText());
      // token to be reused in html formatting
      
      // clearLines();
      // List<IShape> currentline = new ArrayList<IShape>();
      // lines.add(currentline);
      
      // IText text = createText(true);

      // applyTextAlignment(text, hasTextElement.getX());

      // currentline.add(text);
      // textElement.setProperties(
      //   hasTextElement.getMarginLeft(),
      //   13,
      //   getMarginBottom(),
      //   hasTextElement.getWidth()
      // );
      // textElement.setText(getText());
      textElement.setShapeSize(
        hasTextElement.getWidth(), 
        hasTextElement.getHeight()
      );
    } catch (Exception e) {
      logger.error("calculateLines2: " + e);
    }
  }

  @Override
  protected int getStartX() {
    return 0;
  }

  @Override
  protected int getTextTop(int row) {
    return hasTextElement.getY() + DEFAULT_TOP_MARGIN;
  }

	private void calculateAndNotifyHeight(int width) {
		// MeasurementPanel.setTokens(tokens, width - getMarginLeft());
		// MeasurementPanel.setPosition(hasTextElement.getX() + parent.getWidth() + 20, hasTextElement.getY());
  //   hasTextElement.resize(hasTextElement.getX(), hasTextElement.getY(), hasTextElement.getWidth(), MeasurementPanel.getOffsetHeight() + DEFAULT_VERTICAL_TEXT_MARGIN);

    getTextSize().then(new Promise.FunctionParam<ElementSize>() {
      public void accept(ElementSize size) {
        double textHeight = size.getHeight();
        if (/*textHeight != prevTextHeight && */textHeight > 0) {
          int theight = (int) (textHeight / parent.getSurfaceHandler().getScaleFactor());
          // int height = ((int) theight) + DEFAULT_TOP_MARGIN + hasTextElement.getMarginTop() + hasTextElement.getMarginBottom();
          // parent.setHeight();
          hasTextElement.resizeHeight(theight);
          // IText text = getTextShape();
          // if (text != null) {
          //   text.setHeight(height);
          // }
        }
      }
    });
    // prevTextHeight = textHeight;
    // MeasurementHelpers.setMeasurementPanelTextAndResizeDiagram(parent, getText());
  }

  @Override
  public void reapplyText() {
    calculateLines2();
    cleanupAndApplyShape();
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
      if (!editorContext.isTrue(EditorProperty.ON_OT_OPERATION) && !editorContext.isTrue(EditorProperty.ON_SURFACE_LOAD) && !editorContext.isTrue(EditorProperty.ON_LIBRARY_LOAD)) {
        // during OT operation element is NOT resized and everything is 
        // copied as is, element size and text
        // though in vertical case text needs to be recalculated based on element size
        calculateAndNotifyHeight(hasTextElement.getWidth());
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
    setTextShape();
  }

	private IText createText(boolean editable) {
    IText text = IShapeFactory.Util.factory(editable).createText(textGroup);
    // text.setFontFamily(IText.SANS);
    // text.setFill(hasTextElement.getTextColor());
    // text.setAttribute("xml:space", "preserve");
    
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

  // @Override
  // public double getTextHeight() {
  //   // double result = 0;
  //   // if (lines.size() == 1 && lines.get(0).size() == 1) {
  //   //   IShape s = lines.get(0).get(0);
  //   //   if (s instanceof IText) {
  //   //     result = ((IText)s).getTextHeight();
  //   //   }
  //   // }
  //   // return result;

  //   return textElement.getTextHeight();
  // }

  @Override
  public boolean isMarkdownEditor() {
    return true;
  }

  @Override
  public void setShapeSize(int width, int height) {
    IText text = getTextShape();
    if (text != null) {
      text.setShapeSize(width, height);
    }
  }

  private IText getTextShape() {
    // IText result = null;
    // if (lines.size() == 1 && lines.get(0).size() == 1) {
    //   IShape s = lines.get(0).get(0);
    //   if (s instanceof IText) {
    //     result = (IText)s;
    //   }
    // }

    // return result;

    return textElement;
  }

  @Override
  public void resizeEnd() {
    calculateAndNotifyHeight(hasTextElement.getWidth());
  }

  @Override
  protected void resizeElement() {
  }

}
