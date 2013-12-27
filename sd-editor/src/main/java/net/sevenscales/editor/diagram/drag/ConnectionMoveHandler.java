package net.sevenscales.editor.diagram.drag;

import java.util.List;


public class ConnectionMoveHandler implements AnchorMoveHandler {
  private long lastDispachSequence = Long.MAX_VALUE;

  public ConnectionMoveHandler() {
	}

	public void moving(AnchorElement anchorElement, int dx, int dy, long dispachSequence) {
    // need to compare dispachSequence not to move twice when both anchors refer to same 
    // diagram
    if (anchorElement.getRelationship().isSelected()) {
      // don't do anything, just move with the selection
      return;
    }
    
    List<Integer> points = anchorElement.getRelationship().getPoints();
//      System.out.println("moving:"+points);
    boolean attachedToSameElement = (anchorElement.getRelationship().getStartAnchor().getDiagram() == anchorElement.getSource() && anchorElement.getRelationship().getEndAnchor().getDiagram() == anchorElement.getSource());
    
    if ( /*(dx != 0 && dy != 0)  && */ // could be resize dispatch and then dx and dy are 0
    		lastDispachSequence != dispachSequence &&
    		(attachedToSameElement || 
        (anchorElement.getRelationship().getStartAnchor().isSelected() && anchorElement.getRelationship().getEndAnchor().isSelected()))) {
      // both points to same diagram OR moving anchorElement.getRelationship() with the selection => move all the points
      for (int i = 0; i < points.size(); i+=2) {
        points.set(i, points.get(i) + dx);
        points.set(i+1, points.get(i+1) + dy);
      }
//        System.out.println("moving:"+points);
      anchorElement.getRelationship().doSetShape();
    } else if (anchorElement.getRelationship().getStartAnchor().getDiagram() == anchorElement.getSource() && 
               lastDispachSequence != dispachSequence) {
      // if anchor dragged => move according to those
      points.set(0, anchorElement.getAx());
      points.set(1, anchorElement.getAy());
//        System.out.println("moving2:"+points);
      anchorElement.getRelationship().doSetShape();
    } else if (anchorElement.getRelationship().getEndAnchor().getDiagram() == anchorElement.getSource() && 
               lastDispachSequence != dispachSequence) {
      // if anchor dragged => move according to those
      int endxpos = points.size()-2;
      int endypos = points.size()-1;
      points.set(endxpos, anchorElement.getAx());//points.get(endxpos)+dx);
      points.set(endypos, anchorElement.getAy());//points.get(endypos)+dy);
//        System.out.println("moving3:"+points);
      anchorElement.getRelationship().doSetShape();
    }
    lastDispachSequence = dispachSequence;
  }
}
