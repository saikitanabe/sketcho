package net.sevenscales.editor.diagram.utils;

import net.sevenscales.editor.uicomponents.Point;



public class DiagramAnchorUtils {
	public enum RelDirection {
		LEFT_TO_RIGHT,
		RIGHT_TO_LEFT,
		TOP_TO_DOWN,
		DOWN_TO_TOP;
	}
	
	public static RelDirection dir(int x1, int y1, int x2, int y2) {
    double mvert = y2 - y1;
    double mhor = x2 - x1;
    
    
    RelDirection result = RelDirection.LEFT_TO_RIGHT;
    
    if (Math.abs(mvert) > Math.abs(mhor)) {
    	result = RelDirection.TOP_TO_DOWN;
    	if (mvert < 0) {
    		result = RelDirection.DOWN_TO_TOP;
    	}
    } else {
    	if (mhor < 0) {
    		result = RelDirection.RIGHT_TO_LEFT;
    	}
    }
    
    return result;
	}
	
	public static Point startCoordinate(int x1, int y1, int x2, int y2, boolean start, int width, int height) {
		RelDirection d = dir(x1, y1, x2, y2);
		Point result = null;
		
		int x = start ? x1 : x2;
		int y = start ? y1 : y2;
		
		switch (d) {
		case LEFT_TO_RIGHT:
			result = new Point(x, y - height/2);
			break;
		case RIGHT_TO_LEFT:
			result = new Point(x - width, y - height/2);
			break;
		case TOP_TO_DOWN:
			result = new Point(x - width/2, y);
			break;
		case DOWN_TO_TOP:
			result = new Point(x - width/2, y - height);
			break;
		}
		return result;
	}
	
//	public static RelDirection relationshipDir(RelationshipShape2 relationship) {
//
//	}
}
