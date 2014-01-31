package net.sevenscales.editor.diagram.shape;

import net.sevenscales.editor.content.utils.DiagramHelpers;


public class FreehandShape extends Info {
  public int points[];

  public FreehandShape(int[] points) {
    this.points = points;
  }	

  @Override
  public Info move(int moveX, int moveY) {
    for (int i = 0; i < points.length; i += 2) {
      points[i] += moveX;
      points[i + 1] += moveY;
    }
    return this;
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
