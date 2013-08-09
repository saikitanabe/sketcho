package net.sevenscales.editor.silver;

import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.uicomponents.Point;

public class RelationshipShape extends Info {
	public static final int INHERITANCE = 0x00000001;
	public static final int DIRECTED = 0x00000002;
	public static final int DEPENDANCY = 0x00000004;
  public static final int AGGREGATE = 0x00000008;
	
	public Point startPoint = new Point();
	public Point endPoint = new Point();
	public int caps;

	public RelationshipShape() {
	}
	
	public RelationshipShape(int caps) {
	  this.caps = caps;
  }
	
  public RelationshipShape(int x1, int y1, int x2, int y2) {
    startPoint.x = x1;
    startPoint.y = y1;
    endPoint.x = x2;
    endPoint.y = y2;
  }	
  
	public RelationshipShape(int x1, int y1, int x2, int y2, int caps) {
		startPoint.x = x1;
		startPoint.y = y1;
		endPoint.x = x2;
		endPoint.y = y2;
		this.caps = caps;
	}

	public boolean isInheritance() {
		return (caps & RelationshipShape.INHERITANCE) == RelationshipShape.INHERITANCE;
	}

	public boolean isDirected() {
		return (caps & RelationshipShape.DIRECTED) == RelationshipShape.DIRECTED;
	}

	public boolean isDependancy() {
		return (caps & RelationshipShape.DEPENDANCY) == RelationshipShape.DEPENDANCY;
	}

  public boolean isAggregate() {
    return (caps & RelationshipShape.AGGREGATE) == RelationshipShape.AGGREGATE;
  }
  
}
