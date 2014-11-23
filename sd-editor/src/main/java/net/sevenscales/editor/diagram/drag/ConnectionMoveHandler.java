package net.sevenscales.editor.diagram.drag;

import java.util.List;


public class ConnectionMoveHandler implements AnchorMoveHandler {
  private int lastDispachSequence = Integer.MAX_VALUE;

  public ConnectionMoveHandler() {
	}

	public void moving(AnchorElement anchorElement, int dx, int dy, int dispachSequence) {
    // need to compare dispachSequence not to move twice when both anchors refer to same 
    // diagram
    if (anchorElement.getRelationship().isSelected()) {
      // don't do anything, just move with the selection
      return;
    }
    
    List<Integer> points = anchorElement.getRelationship().getPoints();
    boolean attachedToSameElement = (anchorElement.getRelationship().getStartAnchor().getDiagram() == anchorElement.getSource() && anchorElement.getRelationship().getEndAnchor().getDiagram() == anchorElement.getSource());

    if (lastDispachSequence != dispachSequence && attachedToSameElement) {
      moveAllPoints(anchorElement, points, dx, dy);
    } else if (lastDispachSequence != dispachSequence && 
              (dx != 0 || dy != 0) &&
              (anchorElement.getRelationship().getStartAnchor().isSelected() && 
               anchorElement.getRelationship().getEndAnchor().isSelected())) {
      // both ends of the relationship are moved => move all the points
      moveAllPoints(anchorElement, points, dx, dy);
    } else if (!attachedToSameElement && anchorElement.getRelationship().getStartAnchor().getDiagram() == anchorElement.getSource()) {
      // if anchor dragged => move according to those
      points.set(0, anchorElement.getAx());
      points.set(1, anchorElement.getAy());
      anchorElement.getRelationship().doSetShape();
    } else if (!attachedToSameElement && anchorElement.getRelationship().getEndAnchor().getDiagram() == anchorElement.getSource()) {
      // if anchor dragged => move according to those
      int endxpos = points.size()-2;
      int endypos = points.size()-1;
      points.set(endxpos, anchorElement.getAx());//points.get(endxpos)+dx);
      points.set(endypos, anchorElement.getAy());//points.get(endypos)+dy);
      anchorElement.getRelationship().doSetShape();
    }
    lastDispachSequence = dispachSequence;
  }

  private void moveAllPoints(AnchorElement anchorElement, List<Integer> points, int dx, int dy) {
    for (int i = 0; i < points.size(); i+=2) {
      points.set(i, points.get(i) + dx);
      points.set(i+1, points.get(i+1) + dy);
    }
    anchorElement.getRelationship().doSetShape();
  }
}
