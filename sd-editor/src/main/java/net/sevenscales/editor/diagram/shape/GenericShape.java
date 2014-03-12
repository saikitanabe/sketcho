package net.sevenscales.editor.diagram.shape;

import net.sevenscales.editor.content.utils.DiagramHelpers;


public class GenericShape extends HasRectShape {
  private String elementType;

  public GenericShape(String elementType, String[] shape) {
    super(shape);
    this.elementType = elementType;
  }
  
  public GenericShape(String elementType, int left, int top, int width, int height) {
    super(left, top, width, height);
    this.elementType = elementType;
  }

  public String getElementType() {
    return elementType;
  }

  public void setElementType(String elementType) {
    this.elementType = elementType;
  }
  
}
