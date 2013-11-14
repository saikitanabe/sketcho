package net.sevenscales.editor.uicomponents;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.domain.ElementType;

public class AnchorElement {
//  private List<AnchorMoveHandler> handlers = new ArrayList<AnchorMoveHandler>();
  private AnchorMoveHandler handler;
  private int ax = 0;
  private int ay = 0;
  private Diagram diagram;
  private double relativeX = 0;
  private double relativeY = 0;
//  private Rectangle boundary;
  private Anchor anchor;
  private boolean fixedPoint;
  
  public AnchorElement(int ax, int ay, Diagram diagram) {
    this.ax = ax;
    this.ay = ay;
    this.diagram = diagram;
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
  
  public void attach(AnchorMoveHandler handler) {
//    handlers.add(handler);
    // currently support only one handler
    this.handler = handler;
    diagram.attachedRelationship(this);
  }

  public void detach() {
    anchor.clear();
    this.handler = null;
  }
  public Diagram getSource() {
    return diagram;
  }

  public AnchorMoveHandler getHandler() {
		return handler;
	}
  
  /**
   * 
   * @param dx
   * @param dy
   * @param dispatchSequence needed to identify dispatch events sent at same time 
   */
  public void dispatch(int dx, int dy, long dispatchSequence) {
//    for (AnchorMoveHandler h : handlers) {
//      h.moving(this, dx, dy);
//    }
    if (handler != null) {
      ax += dx;
      ay += dy;
      handler.moving(this, dx, dy, dispatchSequence);
    }
  }

  public void dispatch(long dispachSequence) {
//  for (AnchorMoveHandler h : handlers) {
//    h.moving(this, dx, dy);
//  }
    if (handler != null) {
      handler.moving(this, 0, 0, dispachSequence);
    }
}

  public void highlight(boolean value) {
    if (!diagram.isSelected()) {
      diagram.setHighlight(value);
    }
  }

  public void remove() {
//    System.out.println("REMOVE anchor: "+anchor+ " "+this);
  	if (diagram != null) {
  	  diagram.getAnchors().remove(this);
  		diagram.removeAnchor(anchor);
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

}
