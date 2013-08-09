package net.sevenscales.editor.diagram.shape;

import net.sevenscales.editor.content.utils.DiagramHelpers;


public class FreehandShape extends Info {
  public int points[];

  public FreehandShape(int[] points) {
    this.points = points;
  }	
  
  @Override
  public int getLeft() {
  	return DiagramHelpers.getLeftCoordinate(points);
  }
  
  @Override
  public int getTop() {
  	return DiagramHelpers.getTopCoordinate(points);
  }
  
}
