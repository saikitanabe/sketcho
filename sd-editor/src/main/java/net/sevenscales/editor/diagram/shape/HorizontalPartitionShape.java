package net.sevenscales.editor.diagram.shape;

import net.sevenscales.domain.ElementType;

public class HorizontalPartitionShape extends HasRectShape {
  
  public HorizontalPartitionShape() {
  }
  
  public HorizontalPartitionShape(int left, int top, int width, int height) {
  	super(left, top, width, height);
  }

	public HorizontalPartitionShape(String[] rectinfo) {
		super(rectinfo);
	}

  public GenericShape toGenericShape(Integer shapeProperties) {
    return new GenericShape(ElementType.HORIZONTAL_PARTITION.getValue(), rectShape.left, rectShape.top, rectShape.width, rectShape.height, shapeProperties);
  }

}
