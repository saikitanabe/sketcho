package net.sevenscales.editor.diagram.shape;

import net.sevenscales.domain.ElementType;


public class RectContainerShape extends HasRectShape {
  
  public RectContainerShape() {
  }
  
  public RectContainerShape(int left, int top, int width, int height) {
  	super(left, top, width, height);
  }

	public RectContainerShape(String[] rectinfo) {
		super(rectinfo);
	}
  
  public GenericShape toGenericShape(Integer shapeProperties) {
    return new GenericShape(ElementType.VERTICAL_PARTITION.getValue(), rectShape.left, rectShape.top, rectShape.width, rectShape.height, shapeProperties);
  }

}
