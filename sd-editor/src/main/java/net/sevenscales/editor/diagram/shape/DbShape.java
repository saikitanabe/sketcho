package net.sevenscales.editor.diagram.shape;


public class DbShape extends HasRectShape {
  public DbShape() {
  }
  
  public DbShape(int left, int top, int width, int height) {
  	super(left, top, width, height);
  }

	public DbShape(String[] rectinfo) {
		super(rectinfo);
	}

}
