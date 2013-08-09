package net.sevenscales.editor.diagram.shape;

import java.util.List;

import net.sevenscales.editor.content.ui.LineSelections.RelationShipType;

public class RelationshipShape2 extends Info {
	public static final int INHERITANCE = 0x00000001;
	public static final int DIRECTED = 0x00000002;
	public static final int DEPENDANCY = 0x00000004;
  public static final int AGGREGATE = 0x00000008;

  public List<Integer> points;
	public int caps;
	public RelationShipType type;

	public RelationshipShape2() {
	}
	
	public RelationshipShape2(int caps, RelationShipType type) {
	  this.caps = caps;
	  this.type = type;
  }
	
  public RelationshipShape2(List<Integer> points) {
    this.points = points;
  }	
  
	public RelationshipShape2(List<Integer> points, int caps) {
	  this.points = points;
		this.caps = caps;
	}

	public boolean isInheritance() {
		return (caps & RelationshipShape2.INHERITANCE) == RelationshipShape2.INHERITANCE;
	}

	public boolean isDirected() {
		return (caps & RelationshipShape2.DIRECTED) == RelationshipShape2.DIRECTED;
	}

	public boolean isDependancy() {
		return (caps & RelationshipShape2.DEPENDANCY) == RelationshipShape2.DEPENDANCY;
	}

  public boolean isAggregate() {
    return (caps & RelationshipShape2.AGGREGATE) == RelationshipShape2.AGGREGATE;
  }
  
  @Override
  public int getLeft() {
  	return Math.min(points.get(0), points.get(points.size()-2));
  }
  
  @Override
  public int getTop() {
  	return points.get(1);
  }
  
}
