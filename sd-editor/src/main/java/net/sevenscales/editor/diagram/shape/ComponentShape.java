package net.sevenscales.editor.diagram.shape;


public class ComponentShape extends HasRectShape {
  public ComponentShape(String[] shape) {
  	super(shape);
  }
  
  public ComponentShape(int left, int top, int width, int height) {
  	super(left, top, width, height);
  }

}
