package net.sevenscales.editor.diagram.shape;

import java.util.List;

import net.sevenscales.editor.content.RelationShipType;

public class RelationshipShape2 extends Info {
	public static final int INHERITANCE 		= 0x00000001;
	public static final int DIRECTED 				= 0x00000002;
	public static final int DEPENDANCY 			= 0x00000004;
  public static final int AGGREGATE 			= 0x00000008;
  public static final int FILLED 					= 0x00000010;
  public static final int DIRECTED_START 	= 0x00000020;
  public static final int CURVED          = 0x00000040;

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

  @Override
  public Info move(int moveX, int moveY) {
    for (int i = 0; i < points.size(); i += 2) {
      points.set(i, points.get(i) + moveX);
      points.set(i + 1, points.get(i + 1) + moveY);
    }
    return this;
  }

	public boolean isInheritance() {
		return (caps & RelationshipShape2.INHERITANCE) == RelationshipShape2.INHERITANCE;
	}

	public boolean isDirected() {
		return (caps & RelationshipShape2.DIRECTED) == RelationshipShape2.DIRECTED;
	}

	public boolean isDirectedStart() {
		return (caps & RelationshipShape2.DIRECTED_START) == RelationshipShape2.DIRECTED_START;
	}

	public boolean isDependancy() {
		return (caps & RelationshipShape2.DEPENDANCY) == RelationshipShape2.DEPENDANCY;
	}

  public boolean isAggregate() {
    return (caps & RelationshipShape2.AGGREGATE) == RelationshipShape2.AGGREGATE;
  }

  public boolean isFilled() {
    return (caps & RelationshipShape2.FILLED) == RelationshipShape2.FILLED;
  }

  public boolean isCurved() {
    return (caps & RelationshipShape2.CURVED) == RelationshipShape2.CURVED;
  }

  public void asCurve() {
    caps |= RelationshipShape2.CURVED;
  }

  public void asStraight() {
    caps &= 0xffffff & ~RelationshipShape2.CURVED;
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
