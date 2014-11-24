package net.sevenscales.editor.diagram.shape;

import net.sevenscales.domain.ElementType;


public class UMLPackageShape extends HasRectShape {
  
  public UMLPackageShape() {
  }
  
  public UMLPackageShape(int left, int top, int width, int height) {
  	super(left, top, width, height);
  }

	public UMLPackageShape(String[] rectinfo) {
		super(rectinfo);
	}

  public GenericShape toGenericShape(Integer shapeProperties) {
    return new GenericShape(ElementType.PACKAGE.getValue(), rectShape.left, rectShape.top, rectShape.width, rectShape.height, shapeProperties);
  }

}
