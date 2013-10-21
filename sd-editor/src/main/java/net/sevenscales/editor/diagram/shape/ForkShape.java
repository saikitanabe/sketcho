package net.sevenscales.editor.diagram.shape;


public class ForkShape extends HasRectShape {
	public int orientation;

  public ForkShape() {
  }

  public ForkShape(int left, int top, int width, int height) {
  	this(left, top, width, height, 0);
  }
  
  /**
  * @orientation 0 horizontal 1 >= vertical
  */
  public ForkShape(int left, int top, int width, int height, int orientation) {
  	super(left, top, width, height);
  	this.orientation = orientation;
  }

	public ForkShape(String[] rectinfo) {
		super(rectinfo);
		if (rectinfo.length >= 5) {
			orientation = Integer.valueOf(rectinfo[4]);
		} else {
			orientation = 0;
		}
	}

}
