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

public class TextElementVerticalFormatUtil extends TextElementFormatUtil {
  private JavaScriptObject tokens;

	public TextElementVerticalFormatUtil(Diagram parent, HasTextElement hasTextElement, IGroup group, EditorContext editorContext) {
  	super(parent, hasTextElement, group, editorContext);
  }

  public void setText(String newText, boolean editable) {
  	setText(newText, editable, false);
  }
  
  @Override
  public void show() {
  	calculateLines2(hasTextElement.getX());
  	setTextShape();
  	super.show();
  }
  
  private void calculateLines(int left) {
  	List<StringToken> tokens = TokenParser.parse(getText());
  	
  	clearLines();
  	List<IShape> currentline = new ArrayList<IShape>();
  	lines.add(currentline);
  	
		IText text = createText(true);
		currentline.add(text);

		boolean firstInsert = true;
  	for (StringToken token : tokens) {
	  	boolean newline = false;
	  	if (token.text.matches("\\s*")) { // "\\s*" == line break...
	  		newline = true;
	  	}

	  	text.addText(token.text.trim(), token.fontWeight, firstInsert, newline, hasTextElement.getX() + 9, parent.getMeasurementAreaWidth());
	  	firstInsert = false;
  	}
  }

  private void calculateLines2(int left) {
    this.tokens = TokenParser.parse2(getText());
    // token to be reused in html formatting
    
    clearLines();
    List<IShape> currentline = new ArrayList<IShape>();
    lines.add(currentline);
    
    IText text = createText(true);
    currentline.add(text);
    text.addText(tokens, hasTextElement.getX() + 9, parent.getMeasurementAreaWidth());
  }

  
	private void calculateAndNotifyHeight(int width) {
		MeasurementPanel.setTokens(tokens, width);
		MeasurementPanel.setPosition(hasTextElement.getX() + parent.getWidth() + 20, hasTextElement.getY());
    hasTextElement.resize(hasTextElement.getX(), hasTextElement.getY(), hasTextElement.getWidth(), MeasurementPanel.getOffsetHeight() + DEFAULT_MARGIN_BOTTOM);
  }
  
  public void setText(String newText, boolean editable, boolean force) {
  	// convert json text line (\\n) breaks to line breaks
//  	newText = newText.replaceAll("\\\\n", "\n");
  	newText = newText.replaceAll("\\\\r", ""); // remove \r chars and handle only \n later
//    boolean changed = getText() != null && getText().equals(newText) ? false : true;
//    if (changed || force) {
    setText(newText);
    
    if (force || editorContext.isTrue(EditorProperty.ON_SURFACE_LOAD) || editorContext.isTrue(EditorProperty.ON_OT_OPERATION)) {
    	calculateLines2(hasTextElement.getX());
      if (!editorContext.isTrue(EditorProperty.ON_OT_OPERATION)) {
        // during OT operation element is NOT resized and everything is 
        // copied as is, element size and text
        // though in vertical case text needs to be recalculated based on element size
        calculateAndNotifyHeight(parent.getMeasurementAreaWidth());
      }
      this.tokens = null; // cleanup some memory
    	setTextShape();
    }

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

	private IText createText(boolean editable) {
    IText text = IShapeFactory.Util.factory(editable).createText(textGroup);
    text.setFontFamily(IText.SANS);
//    hasTextElement.addShape(text);
    text.setFill(hasTextElement.getTextColorAsString());
    text.setAttribute("xml:space", "preserve");
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

}
