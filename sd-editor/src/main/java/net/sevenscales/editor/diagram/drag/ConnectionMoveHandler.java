package net.sevenscales.editor.diagram.drag;

import java.util.List;

import com.google.gwt.touch.client.Point;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.AnchorUtils;
import net.sevenscales.editor.gfx.domain.ISurface;


public class ConnectionMoveHandler implements AnchorMoveHandler {
  private int lastDispachSequence = Integer.MAX_VALUE;

  // >>>>>>>>> Debugging
  // private static net.sevenscales.editor.gfx.domain.ICircle tempCircle;
  // <<<<<<<<< Debugging

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

  public void rotate(
    AnchorElement anchorElement,
    boolean save,
    Integer oldRotate,
    Integer newRotate,
    // >>>>>>>>>> DEBUGGING
    net.sevenscales.editor.api.ISurfaceHandler surface
    // <<<<<<<<<<< DEBUGGING
  ) {

    // >>>>>>>>>> DEBUGGING
    // if (tempCircle == null) {
    //   tempCircle = net.sevenscales.editor.gfx.domain.IShapeFactory.Util.factory(true).createCircle(surface.getInteractionLayer());
    //   tempCircle.setShape(0, 0, 10);
    //   tempCircle.setStroke(218, 57, 57, 1);
    //   // tempCircle.setFill(218, 57, 57, 1);
    //   tempCircle.setStrokeWidth(2);
    // }
    // <<<<<<<<<<< DEBUGGING


    Diagram source = anchorElement.getSource();

    List<Integer> points = anchorElement.getRelationship().getPoints();

    if (anchorElement.getRelationship().getEndAnchor().getDiagram() == source) {

      int endxpos = points.size()-2;
      int endypos = points.size()-1;

      rotateAndSetRelationshipPoint(
        anchorElement,
        save,
        oldRotate,
        newRotate,
        endxpos,
        endypos,
        points
      );

    } else if (anchorElement.getRelationship().getStartAnchor().getDiagram() == source) {

      rotateAndSetRelationshipPoint(
        anchorElement,
        save,
        oldRotate,
        newRotate,
        0,
        1,
        points
      );

    }
  }

  private void rotateAndSetRelationshipPoint(
    AnchorElement anchorElement,
    boolean save,
    Integer oldRotate,
    Integer newRotate,
    int indexX,
    int indexY,
    List<Integer> points
  ) {
    Diagram source = anchorElement.getSource();

    if (source.getRotate() != null) {
      double cx = source.getLeft() + source.getWidth() / 2;
      double cy = source.getTop() + source.getHeight() / 2;

      int ax = anchorElement.getAx();
      int ay = anchorElement.getAy();

      // restore point according to old angle
      Point oldp = AnchorUtils.unrotatePointToOriginalRectCenter(ax, ay, cx, cy, oldRotate);

      // now it is possible to calculate position with the new angle
      Point p = AnchorUtils.rotatePoint(oldp.getX(), oldp.getY(), cx, cy, newRotate);

      ax = ((int) p.getX());
      ay = ((int) p.getY());

      // >>>>>> DEBUGGING
      // tempCircle.setShape(ax, ay, 10);
      // <<<<<< DEBUGGING

      points.set(indexX, ax);
      points.set(indexY, ay);
      anchorElement.getRelationship().doSetShape();

      if (save) {
        anchorElement.setAx(ax);
        anchorElement.setAy(ay);
      }
    }
  }

  private void moveAllPoints(AnchorElement anchorElement, List<Integer> points, int dx, int dy) {
    for (int i = 0; i < points.size(); i+=2) {
      points.set(i, points.get(i) + dx);
      points.set(i+1, points.get(i+1) + dy);
    }
    anchorElement.getRelationship().doSetShape();
  }
}
