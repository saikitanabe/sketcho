package net.sevenscales.editor.diagram.drag;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.uml.Relationship2;
import net.sevenscales.editor.gfx.domain.Point;

public class Anchor implements AnchorMoveHandler {
	private Diagram diagram;
	private Relationship2 relationship;
	private Point point = new Point();
	// not attached, just over a diagram
	private Diagram onDiagram;
  private AnchorElement anchorElement;
	private Anchor theOtherEnd;
	private String clientId;
	
	public Anchor(Relationship2 relationship) {
		this.relationship = relationship;
	}
	
	public Anchor(Anchor anchor) {
		copyFrom(anchor);
	}
	
	public void copyFrom(Anchor anchor) {
		this.diagram = anchor.diagram;
		this.relationship = anchor.relationship;
		this.point = anchor.point;
		this.onDiagram = anchor.onDiagram;
		this.anchorElement = anchor.anchorElement;
		this.theOtherEnd = anchor.theOtherEnd;
		this.clientId = anchor.clientId;
	}

	public void setDiagram(Diagram diagram) {
		setDiagram(diagram, true);
	}

	public void setDiagram(Diagram diagram, boolean hightlight) {
		if (hightlight && (diagram != null && this.diagram != diagram)) {
			// attached to new diagram
			diagram.setHighlight(true);
		} else if (diagram == null && this.diagram != null) {
		  this.diagram.setHighlight(false);
		}
		
		// clientId needs to be updated at the same time
		if (diagram != null) {
			clientId = diagram.getDiagramItem().getClientId();
		} else {
			clientId = null;
		}
		this.diagram = diagram;		
	}

	public Diagram getDiagram() {
		return diagram;
	}
	
	// public void setRelationship(Relationship2 relationship) {
	// 	this.relationship = relationship;
	// }
	
	public Relationship2 getRelationship() {
		return relationship;
	}

	public void setPoint(Point point) {
		if (point == null) {
			this.point.x = 0;
			this.point.y = 0;
		} else {
			this.point = point;
		}
	}
	
	public void setPoint(int x, int y) {
		this.point.x = x;
		this.point.y = y;
	}

	public Point getPoint() {
		return point;
	}

	public void setOnDiagram(Diagram onDiagram) {
		this.onDiagram = onDiagram;
	}
	
	public Diagram getOnDiagram() {
		return onDiagram;
	}

  public void applyAnchorElement(AnchorElement anchorElement) {
    this.anchorElement = anchorElement;
    if (relationship != null) {
    	relationship.applyAnchor(this);
    }
  }
  public AnchorElement getAnchorElement() {
    return anchorElement;
  }

  public void clear() {
    if (anchorElement != null) {
      anchorElement.remove();
      anchorElement = null;
    }

    if (diagram != null) {
	    diagram.removeAnchor(this);
    }
    clientId = null;
  }

  public void clearParentAnchorMap() {
  	if (diagram != null) {
  		diagram.clearAnchorMap();
  	}
  }

  public void setDiff(int dx, int dy) {
    if (anchorElement != null) {
      anchorElement.setDiff(dx, dy);
    }
  }

  public boolean isSelected() {
    if (diagram != null && diagram.isSelected()) {
      return true;
    }
    return false;
  }

	public void setTheOtherEnd(Anchor theOtherEnd) {
		this.theOtherEnd = theOtherEnd;
	}
	public Anchor getTheOtherEnd() {
		return theOtherEnd;
	}

	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void moving(AnchorElement anchorElement, int dx, int dy, long dispachSequence) {
		relationship.getAnchorMoveHandler().moving(anchorElement, dx, dy, dispachSequence);
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

}
