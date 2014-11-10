package net.sevenscales.editor.diagram.shape;


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
  
}
