package net.sevenscales.editor.diagram.shape;

import net.sevenscales.editor.content.utils.DiagramHelpers;
import net.sevenscales.domain.ISvgDataRO;
import net.sevenscales.domain.ShapeProperty;


public class GenericShape extends HasRectShape {
  private String elementType;
  private Integer shapeProperties;
  private ISvgDataRO svgdata;

  public GenericShape(String elementType, String[] shape, ISvgDataRO svgdata) {
    this(elementType, shape, 0, svgdata);
  }

  public GenericShape(String elementType, String[] shape, Integer shapeProperties, ISvgDataRO svgdata) {
    super(shape);
    this.elementType = elementType;
    this.shapeProperties = shapeProperties;
    this.svgdata = svgdata;
  }

  public GenericShape(String elementType, int left, int top, int width, int height) {
    this(elementType, left, top, width, height, 0, null);
  }

  public GenericShape(String elementType, int left, int top, int width, int height, Integer shapeProperties) {
    this(elementType, left, top, width, height, shapeProperties, null);
  }

  public GenericShape(String elementType, int left, int top, int width, int height, Integer shapeProperties, ISvgDataRO svgdata) {
    super(left, top, width, height);
    this.elementType = elementType;
    this.shapeProperties = shapeProperties;
    this.svgdata = svgdata;
  }

  public String getElementType() {
    return elementType;
  }

  public void setElementType(String elementType) {
    this.elementType = elementType;
  }

  // public void setint(int shapeProperties) {
  //   this.shapeProperties = shapeProperties;
  // }

  public Integer getShapeProperties() {
    return shapeProperties;
  }

  public void setSvgData(ISvgDataRO svgdata) {
    this.svgdata = svgdata;
  }
  public ISvgDataRO getSvgData() {
    return svgdata;
  }

  public void setShapeProperties(Integer shapeProperties) {
    this.shapeProperties = shapeProperties;
  }

  public void addShapeProperty(ShapeProperty shapeProperty) {
    if (shapeProperties == null) {
      shapeProperties = 0;
    }
    this.shapeProperties |= shapeProperty.getValue();
  }
  
}
