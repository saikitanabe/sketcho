package net.sevenscales.editor.diagram.shape;

import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.domain.TextPosition;


public class GenericShape extends HasRectShape {
  private String elementType;
  private TextPosition textPosition;

  public GenericShape(String elementType, String[] shape) {
    this(elementType, shape, TextPosition.CENTER);
  }

  public GenericShape(String elementType, String[] shape, TextPosition textPosition) {
    super(shape);
    this.elementType = elementType;
    this.textPosition = textPosition;
  }

  public GenericShape(String elementType, int left, int top, int width, int height) {
    this(elementType, left, top, width, height, TextPosition.CENTER);
  }

  public GenericShape(String elementType, int left, int top, int width, int height, TextPosition textPosition) {
    super(left, top, width, height);
    this.elementType = elementType;
    this.textPosition = textPosition;
  }

  public String getElementType() {
    return elementType;
  }

  public void setElementType(String elementType) {
    this.elementType = elementType;
  }

  public void setTextPosition(TextPosition textPosition) {
    this.textPosition = textPosition;
  }

  public TextPosition getTextPosition() {
    return textPosition;
  }
  
}
