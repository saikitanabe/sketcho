package net.sevenscales.editor.uicomponents;

import java.util.ArrayList;
import java.util.List;

import net.sevenscales.editor.api.EditorContext;
import net.sevenscales.editor.api.EditorProperty;
import net.sevenscales.editor.content.utils.TokenParser;
import net.sevenscales.editor.content.utils.TokenParser.StringToken;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.gfx.base.GraphicsMouseDownHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseEnterHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseLeaveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseMoveHandler;
import net.sevenscales.editor.gfx.base.GraphicsMouseUpHandler;
import net.sevenscales.editor.gfx.domain.IGroup;
import net.sevenscales.editor.gfx.domain.IShape;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.gfx.domain.IText;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;

public class TextElementSimpleFormatUtil extends TextElementFormatUtil {
	public TextElementSimpleFormatUtil(Diagram parent, HasTextElement hasTextElement, IGroup group, EditorContext editorContext) {
  	super(parent, hasTextElement, group, editorContext);
  }
	
  public void setText(String newText, boolean editable) {
  	setText(newText, editable, false);
  }
  
  @Override
  public void show() {
  	calculateLines();
  	setTextShape();
  	super.show();
  }
  
  private void calculateLines() {
  	List<StringToken> tokens = TokenParser.parse(getText());
  	
  	clearLines();
  	List<IShape> currentline = new ArrayList<IShape>();
  	lines.add(currentline);
  	
		IText text = createText(true);
		currentline.add(text);

		boolean firstInsert = true;
  	for (StringToken token : tokens) {
	  	String currentText = text.getText();
	  	addText(firstInsert, token, currentText, text);
	  	firstInsert = false;

	  	if (token.text.matches("\\s*")) { // "\\s*" == line break...
	  		// => do not add token to prev line, it didn't fit OR it is a line break
//	  		text.removeLastSpan();

	  		// start a new line
	  		currentline = new ArrayList<IShape>();
	  		// create a new text for the line
	  		text = createText(true);
  			currentText = "";
  			addText(firstInsert, token, currentText, text);
  			currentline.add(text);
	  		lines.add(currentline);
	  	}
  	}
  }
  
  private void addText(boolean firstInsert, StringToken token, String currentText, IText text) {
//		text.setText(currentText + token.text + " ");
  	text.addText(" " + token.text, token.fontWeight, firstInsert, false, parent.getLeft(), 0);
  	
		double lineWidth = text.getTextWidth();
    widestWidth = lineWidth > widestWidth ? lineWidth : widestWidth; 

//  	if (!token.fontWeight) {
//  		// no decoration just add text
//  		// does decoration stay?
//  		text.setText(currentText + token.text + " ");
//  	} else {
//  		text.addBoldText(token.text + " ");
//  	}
	}

	private void calculateAndNotifyHeight() {
    int height = getMarginTop() + lines.size() * ROW_HEIGHT + getMarginBottom();
    hasTextElement.resize(hasTextElement.getX(), hasTextElement.getY(), hasTextElement.getWidth(), height);
  }
  
  public void setText(String newText, boolean editable, boolean force) {
  	widestWidth = 0;
  	// convert json text line (\\n) breaks to line breaks
  	newText = newText.replaceAll("\\\\n", "\n");
  	newText = newText.replaceAll("\\\\r", ""); // remove \r chars and handle only \n later
    setText(newText);
    
  	calculateLines();
  	calculateAndNotifyHeight();
  	setTextShape();
    	
    int width = getTextWidth();
    int height = getMarginTop() + lines.size() * ROW_HEIGHT + getMarginBottom();

    hasTextElement.resize(hasTextElement.getX(), hasTextElement.getY(), width, height);
  }

	private IText createText(boolean editable) {
    IText text = IShapeFactory.Util.factory(editable).createText(textGroup);
    text.setFontFamily(IText.SANS);
//    hasTextElement.addShape(text);
    text.setFill(hasTextElement.getTextColorAsString());
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

		return text;
	}

}
