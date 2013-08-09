package net.sevenscales.editor.diagram.shape;


public class UMLPackageShape extends HasRectShape {
  
  public UMLPackageShape() {
  }
  
  public UMLPackageShape(int left, int top, int width, int height) {
  	super(left, top, width, height);
  }

	public UMLPackageShape(String[] rectinfo) {
		super(rectinfo);
	}
  
}
