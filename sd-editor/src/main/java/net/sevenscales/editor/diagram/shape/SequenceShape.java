package net.sevenscales.editor.diagram.shape;

import net.sevenscales.domain.ElementType;


public class SequenceShape extends HasRectShape {
  public int lifeLineHeight;
  
  public SequenceShape() {
  }
  
  public SequenceShape(int left, int top, int width, int height, int lifeLineHeight) {
  	super(left, top, width, height);
    this.lifeLineHeight = lifeLineHeight;
  }

  public String toString() {
  	return lifeLineHeight + " " + super.toString();
  }

  public GenericShape toGenericShape(Integer shapeProperties) {
    return new GenericShape(ElementType.SEQUENCE.getValue(), rectShape.left, rectShape.top, rectShape.width, rectShape.height, shapeProperties);
  }
  
}
