package net.sevenscales.editor.diagram.shape;


public class ActivityShape extends HasRectShape {
  public ActivityShape() {
  }
  
  public ActivityShape(int left, int top, int width, int height) {
  	super(left, top, width, height);
  }

	public ActivityShape(String[] rectinfo) {
		super(rectinfo);
	}

}
