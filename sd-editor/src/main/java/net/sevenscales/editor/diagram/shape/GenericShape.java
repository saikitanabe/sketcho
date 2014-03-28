package net.sevenscales.editor.diagram.shape;

import net.sevenscales.editor.content.utils.DiagramHelpers;


public class GenericShape extends HasRectShape {
  private String elementType;
  private int shapeProperties;
  private String svg;

  public GenericShape(String elementType, String[] shape) {
    this(elementType, shape, 0);
  }

  public GenericShape(String elementType, String[] shape, int shapeProperties) {
    super(shape);
    this.elementType = elementType;
    this.shapeProperties = shapeProperties;
    this.svg = null;
  }

  public GenericShape(String elementType, int left, int top, int width, int height) {
    this(elementType, left, top, width, height, 0, null);
  }

  public GenericShape(String elementType, int left, int top, int width, int height, int shapeProperties, String svg) {
    super(left, top, width, height);
    this.elementType = elementType;
    this.shapeProperties = shapeProperties;
    this.svg = svg;
  }

  public String getElementType() {
    return elementType;
  }

  public void setElementType(String elementType) {
    this.elementType = elementType;
  }

  public void setint(int shapeProperties) {
    this.shapeProperties = shapeProperties;
  }

  public int getShapeProperties() {
    return shapeProperties;
  }

  public void setSvg(String svg) {
    this.svg = svg;
  }
  public String getSvg() {
    return svg;
  }
  
}
