package net.sevenscales.editor.diagram.shape;

import net.sevenscales.domain.ElementType;

public class ComponentShape extends HasRectShape {
  public ComponentShape(String[] shape) {
  	super(shape);
  }
  
  public ComponentShape(int left, int top, int width, int height) {
  	super(left, top, width, height);
  }

  public GenericShape toGenericShape(Integer shapeProperties) {
    return new GenericShape(ElementType.COMPONENT.getValue(), rectShape.left, rectShape.top, rectShape.width, rectShape.height, shapeProperties);
  }

}
