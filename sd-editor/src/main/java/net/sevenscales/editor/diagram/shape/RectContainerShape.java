package net.sevenscales.editor.diagram.shape;


public class RectContainerShape extends HasRectShape {
  
  public RectContainerShape() {
  }
  
  public RectContainerShape(int left, int top, int width, int height) {
  	super(left, top, width, height);
  }

	public RectContainerShape(String[] rectinfo) {
		super(rectinfo);
	}
  
}
