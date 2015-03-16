package net.sevenscales.editor.diagram.drag;

import java.util.Collection;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.domain.ElementType;
import net.sevenscales.editor.uicomponents.CardinalDirection;
import net.sevenscales.editor.uicomponents.uml.Relationship2;

public class AnchorElement {
//  private List<AnchorMoveHandler> handlers = new ArrayList<AnchorMoveHandler>();
  private int ax = 0;
  private int ay = 0;
  private Diagram diagram;
  private double relativeX = 0;
  private double relativeY = 0;
//  private Rectangle boundary;
  private Anchor anchor;
  private boolean fixedPoint;
  private CardinalDirection cardinalDirection;
  
  public AnchorElement(int ax, int ay, Diagram diagram, Anchor anchor) {
    this.ax = ax;
    this.ay = ay;
    this.diagram = diagram;
    this.anchor = anchor;
  }
  
  public AnchorElement(Anchor anchor, Diagram diagram) {
    this.diagram = diagram;
    this.anchor = anchor;
  }

  public int getAx() {
    return ax;
  }
  public int getAy() {
    return ay;
  }
  
  public void attach() {
//    handlers.add(handler);
    // currently support only one handler
    // this.handler = handler;
    diagram.attachedRelationship(this);
  }

  public void detach() {
    anchor.clear();
    // this.handler = null;
  }
  public Diagram getSource() {
    return diagram;
  }

  public Relationship2 getRelationship() {
    return anchor.getRelationship();
  }

 //  public AnchorMoveHandler getHandler() {
	// 	return handler;
	// }
  
  /**
   * 
   * @param dx
   * @param dy
   * @param dispatchSequence needed to identify dispatch events sent at same time 
   */
  public void dispatch(int dx, int dy, int dispatchSequence) {
//    for (AnchorMoveHandler h : handlers) {
//      h.moving(this, dx, dy);
//    }
    // if (handler != null) {
    ax += dx;
    ay += dy;
    anchor.moving(this, dx, dy, dispatchSequence);
    // }
  }

  public void dispatch(int dispachSequence) {
//  for (AnchorMoveHandler h : handlers) {
//    h.moving(this, dx, dy);
//  }
    // if (handler != null) {
    anchor.moving(this, 0, 0, dispachSequence);
    // }
}

  public void dragEnd() {
    anchor.dragEnd();
  }

  public void highlight(boolean value) {
    if (!diagram.isSelected()) {
      diagram.setHighlight(value);
    }
  }

  public void remove() {
  	if (diagram != null) {
  	  diagram.getAnchors().remove(this);
  		diagram = null;
  	}
  }

  public void setAx(int ax) {
    this.ax = ax;
  }
  public void setAy(int ay) {
    this.ay = ay;
  }
  
  public double getRelativeFactorX() {
    return relativeX;
  }
  public void setRelativeX(double relativeX) {
    this.relativeX = relativeX;
  }

  public void setRelativeY(double relativeY) {
    this.relativeY = relativeY;
  }
  public double getRelativeFactorY() {
    return relativeY;
  }

  public void setDiff(int dx, int dy) {
    ax += dx;
    ay += dy;
  }

  public void setFixedPoint(boolean fixedPoint) {
    this.fixedPoint = fixedPoint;
  }

  public boolean isFixedPoint() {
    return fixedPoint;
  }

  public void setCardinalDirection(CardinalDirection cardinalDirection) {
    this.cardinalDirection = cardinalDirection;
  }

  public CardinalDirection getCardinalDirection() {
    return cardinalDirection;
  }

  /**
  * Causes to redraw and calculate diagram attached relationships.
  */
  public static void dragEndAnchors(Diagram diagram) {
    Collection<AnchorElement> anchors = diagram.getAnchors();
    for (AnchorElement ae : anchors) {
      ae.dragEnd();
    }
  }
 


}
