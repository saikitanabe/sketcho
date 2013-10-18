package net.sevenscales.editor.diagram.shape;


public class HorizontalPartitionShape extends HasRectShape {
  
  public HorizontalPartitionShape() {
  }
  
  public HorizontalPartitionShape(int left, int top, int width, int height) {
  	super(left, top, width, height);
  }

	public HorizontalPartitionShape(String[] rectinfo) {
		super(rectinfo);
	}
  
}
