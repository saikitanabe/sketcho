package net.sevenscales.editor.uicomponents;

import java.util.List;

import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.domain.utils.SLogger;

public class AnchorUtils {
  private static final SLogger logger = SLogger.createLogger(AnchorUtils.class);
  static {
    logger.addFilter(AnchorUtils.class);
  }

  public static class AnchorProperties {
    public int x;
    public int y;
    public double relativeValueX;
    public double relativeValueY;
    public CardinalDirection cardinalDirection = CardinalDirection.NORTH;
  }
  
  private static final int MAGNETIC_VALUE = 3;
  public static final int ATTACH_EXTRA_DISTANCE = 2;

  public static boolean onAttachArea(int x, int y, IRectangle rect) {
    if ( x >= (rect.getX() - MAGNETIC_VALUE) && x <= (rect.getX()+rect.getWidth() + MAGNETIC_VALUE) &&
         y >= (rect.getY() - MAGNETIC_VALUE) && y <= (rect.getY()+rect.getHeight() + MAGNETIC_VALUE)) {
        return true;
    }
    return false;
  }
  
  public static boolean onAttachArea(int x, int y, int left, int top, int width, int height) {
//  	System.out.println("x: " + x + " y: " + y + " left: " + left + " top: " + top);
    if ( x >= (left - MAGNETIC_VALUE) && x <= (left+width+MAGNETIC_VALUE) &&
        y >= (top - MAGNETIC_VALUE) && y <= (top+height+MAGNETIC_VALUE)) {
        return true;
    }
    return false;
  }
  
	public static boolean onAttachArea(int x, int y, int left, int top, int width, int height, int distance) {
		
		// top line
    if ( (x >= (left - MAGNETIC_VALUE) && x <= (left + width + MAGNETIC_VALUE)) &&
    		 (y >= (top - MAGNETIC_VALUE) && y <= (top + distance))) {
    	return true;
    }
    		
    // right line
    if ((x <= (left + width + MAGNETIC_VALUE) && x >= (left + width - distance)) &&
        (y >= (top - MAGNETIC_VALUE) && y <= (top + height + MAGNETIC_VALUE))) {
      return true;
    }
    
		// bottom line
    if ( (x >= (left - MAGNETIC_VALUE) && x <= (left + width + MAGNETIC_VALUE)) &&
    		 (y <= (top + height + MAGNETIC_VALUE) && y >= (top + height - distance))) {
    	return true;
    }
    
    // left line
    if ((x >= (left - MAGNETIC_VALUE) && x <= (left + distance)) &&
        (y >= (top - MAGNETIC_VALUE) && y <= (top + height + MAGNETIC_VALUE))) {
      return true;
    }

		return false;
	}

  public static void anchorPoint(int x, int y, AnchorProperties ap, IRectangle rect) {
    anchorPoint(x, y, ap, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
  }
  
  public static void anchorPoint(int x, int y, AnchorProperties ap, int left, int top, int width, int height) {
//    tempax = GridUtils.align(tempax);
//    tempay = GridUtils.align(tempay);
    int tempax = 0;
    int tempay = 0;

    // Sharp values are without extra distance, used just for relative calculation
    // to have as accurate relative result from real element dimension.
    int sharpX = 0;
    int sharpY = 0;

    CardinalDirection cd = findCardinalDirection(x, y, left, top, width, height);
    switch (cd) {
      case WEST:
        tempax = left - ATTACH_EXTRA_DISTANCE;
        tempay = y;
        sharpX = left;
        sharpY = y;
      break;
      case NORTH:
        tempax = x;
        tempay = top - ATTACH_EXTRA_DISTANCE;
        sharpX = x;
        sharpY = top;
      break;
      case EAST:
        tempax = left + width + ATTACH_EXTRA_DISTANCE;
        tempay = y;
        sharpX = left + width;
        sharpY = y;
      break;
      case SOUTH:
        tempax = x;
        tempay = top + height + ATTACH_EXTRA_DISTANCE;
        sharpX = x;
        sharpY = top + height;
      break;
    }

    ap.x = tempax;
    ap.y = tempay;
    makeRelativeValue(cd, ap, sharpX, sharpY, left, top, width, height);
  }

  public static CardinalDirection findCardinalDirection(int x, int y, int left, int top, int width, int height) {
    int dx = Math.abs(x - left);
    int dy = Math.abs(y - top);
    int dxw = Math.abs(x - (left + width));
    int dyh = Math.abs(y - (top + height));
    
    int smallest = Math.min(dx, dy);
    smallest = Math.min(smallest, dxw); 
    smallest = Math.min(smallest, dyh); 
    if (smallest == dx) {
      return CardinalDirection.WEST;
    } else if (smallest == dy) {
      return CardinalDirection.NORTH;
    } else if (smallest == dxw) {
      return CardinalDirection.EAST;
    } else if (smallest == dyh) {
      return CardinalDirection.SOUTH;
    }
    return CardinalDirection.WEST;
  }

  /**
  * Rounds to use only one decimal, e.g. 0.492 => 0.5 and between 0 - 1.
  * In this way center positions are not rounded little bit of all the time.
  * 0 - 1 position cannot be relatively outside of this element.
  */
  public static double round(double value) {
    double result = Math.round(value * 10) / 10.0;
    if (result > 1) {
      return 1;
    } else if (result < 0) {
      return 0;
    }
    return result;
  }
  
  public static void relativeValue(AnchorProperties ap, int sharpX, int sharpY, int elementLeft, int elementTop, int elementWidth, int elementHeight) {
    CardinalDirection cd = findCardinalDirection(sharpX, sharpY, elementLeft, elementTop, elementWidth, elementHeight);
    makeRelativeValue(cd, ap, sharpX, sharpY, elementLeft, elementTop, elementWidth, elementHeight);
  }

  private static void makeRelativeValue(CardinalDirection cardinalDirection, AnchorProperties ap, int sharpX, int sharpY, int elementLeft, int elementTop, int elementWidth, int elementHeight) {
    ap.relativeValueX = round((sharpX - elementLeft) / (double) elementWidth);
    ap.relativeValueY = round((sharpY - elementTop) / (double) elementHeight);
    ap.cardinalDirection = cardinalDirection;
  }
  
  /**
   * 
   * @param points
   * @return index of closest x => x, y can be used as anchor point
   */
  private static int findClosestPoint(int x, int y, Integer[] points) {
  	int result = -1;
  	int sdx = Integer.MAX_VALUE/2;
  	int sdy = Integer.MAX_VALUE/2;
  	for (int i = 0; i < points.length; i += 2) {
  		int px = points[i];
  		int py = points[i + 1];
      int dx = Math.abs(x - px);
      int dy = Math.abs(y - py);
  		if ( (dx + dy) < (sdx + sdy)) {
  			sdx = dx;
  			sdy = dy;
  			result = i;
  		}
  		
  	}
  	return result;
  }
  
  public static void anchorPoint(int x, int y, int left, int top, int width, int height, AnchorProperties ap, List<Integer> points) {
  	Integer[] array = new Integer[points.size()];
  	int xindex = findClosestPoint(x, y, points.toArray(array));
    
    ap.x = points.get(xindex);
    ap.y = points.get(xindex + 1);
    ap.cardinalDirection = findCardinalDirection(ap.x, ap.y, left, top, width, height);
//    ap.relativeValueX = Math.abs((points.get(xindex)-left)/(double)width);
//    ap.relativeValueY = Math.abs((tempay-top)/(double)height);
  }
  
  public static void anchorPoint(int x, int y, int left, int top, int width, int height, AnchorProperties ap, Integer[] points) {
  	int xindex = findClosestPoint(x, y, points);
    if (xindex + 1 < points.length) {
      ap.x = points[xindex];
      ap.y = points[xindex + 1];
      
      // just set fixed index and if fixed points are set then use directly the index
      ap.relativeValueX = xindex;
      ap.relativeValueY = xindex + 1;
      ap.cardinalDirection = findCardinalDirection(ap.x, ap.y, left, top, width, height);
    }
  }

	public static void setRelativePosition(AnchorElement ae, int left, int top,	int width, int height) {
		// int extraDistanceX = 0;
		// int extraDistanceY = 0;
		// if (ae.getAx() == left - AnchorUtils.ATTACH_EXTRA_DISTANCE) {
		// 	extraDistanceX = -AnchorUtils.ATTACH_EXTRA_DISTANCE;
		// } else if (ae.getAx() == left + width + AnchorUtils.ATTACH_EXTRA_DISTANCE) {
		// 	extraDistanceX = AnchorUtils.ATTACH_EXTRA_DISTANCE;
		// }
		
		// if (ae.getAy() == top - ATTACH_EXTRA_DISTANCE) {
		// 	extraDistanceY = -ATTACH_EXTRA_DISTANCE;
		// } else if (ae.getAy() == top + height + ATTACH_EXTRA_DISTANCE) {
		// 	extraDistanceY = ATTACH_EXTRA_DISTANCE;
		// }

    CardinalDirection cd = ae.getCardinalDirection();

    if (cd == null) {
      // fallback to calculate cardinal direction... what is the case?
      cd = findCardinalDirection(ae.getAx(), ae.getAy(), left, top, width, height);
    }

    doSetRelativePostion(cd, ae, left, top, width, height);

		// if extra distance has not been changed then this is a relative movement and do the movement
		// if (extraDistanceX == 0) { 
		// 	ae.setAx(((int)(width * ae.getRelativeFactorX())) + left);
		// }
		// if (extraDistanceY == 0) {
		// 	ae.setAy(((int)(height * ae.getRelativeFactorY())) + top);
		// }
	}

  private static void doSetRelativePostion(CardinalDirection cd, AnchorElement ae, int left, int top, int width, int height) {
    switch (cd) {
      case WEST:
        ae.setAx(((int)(width * ae.getRelativeFactorX())) + left - ATTACH_EXTRA_DISTANCE);
        ae.setAy(((int)(height * ae.getRelativeFactorY())) + top);
      break;
      case NORTH:
        ae.setAx(((int)(width * ae.getRelativeFactorX())) + left);
        ae.setAy(((int)(height * ae.getRelativeFactorY())) + top - ATTACH_EXTRA_DISTANCE);
      break;
      case EAST:
        ae.setAx(((int)(width * ae.getRelativeFactorX())) + left + ATTACH_EXTRA_DISTANCE);
        ae.setAy(((int)(height * ae.getRelativeFactorY())) + top);
      break;
      case SOUTH:
        ae.setAx(((int)(width * ae.getRelativeFactorX())) + left);
        ae.setAy(((int)(height * ae.getRelativeFactorY())) + top + ATTACH_EXTRA_DISTANCE);
      break;
    }
  }

  public static class ClosestSegment {
    public Point start = new Point();
    public Point end = new Point();
  }

  public static ClosestSegment closestSegment(int left, int top, int width, int height, int left2, int top2, int width2, int height2) {
    ClosestSegment result = new ClosestSegment();

    int diffLeftLeft = Math.abs(left - left2);
    int diffLeftRigth = Math.abs(left - (left2 + width2));
    int diffRightLeft = Math.abs((left + width) - left2);
    int diffRightRight = Math.abs((left + width) - (left2 + width2));

    int diffTopTop = Math.abs(top - top2);
    int diffTopBottom = Math.abs(top - (top2 + height2));
    int diffBottomTop = Math.abs((top + height) - top2);
    int diffBottomBottom = Math.abs((top + height) - (top2 + height2));

    // vertical edges
    int smallest = Math.min(diffLeftLeft, diffLeftRigth);
    smallest = Math.min(smallest, diffRightLeft);
    smallest = Math.min(smallest, diffRightRight);

    // horizontal edges
    smallest = Math.min(smallest, diffTopTop);
    smallest = Math.min(smallest, diffTopBottom);
    smallest = Math.min(smallest, diffBottomTop);
    smallest = Math.min(smallest, diffBottomBottom);

    if (smallest == diffLeftLeft) {
      result.start.x = left;
      result.start.y = top + height / 2;
      result.end.x = left2;
      result.end.y = top2 + height2 / 2;
    } else if (smallest == diffLeftRigth) {
      result.start.x = left;
      result.start.y = top + height / 2;
      result.end.x = left2 + width2;
      result.end.y = top2 + height2 / 2;
    } else if (smallest == diffRightLeft) {
      result.start.x = left + width;
      result.start.y = top + height / 2;
      result.end.x = left2;
      result.end.y = top2 + height2 / 2;
    } else if (smallest == diffRightRight) {
      result.start.x = left + width;
      result.start.y = top + height / 2;
      result.end.x = left2 + width2;
      result.end.y = top2 + height2 / 2;
    } else if (smallest == diffTopTop) {
      result.start.x = left + width / 2;
      result.start.y = top;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2;
    } else if (smallest == diffTopBottom) {
      result.start.x = left + width / 2;
      result.start.y = top;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2 + height2;
    } else if (smallest == diffBottomTop) {
      result.start.x = left + width / 2;
      result.start.y = top + height;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2;
    } else if (smallest == diffBottomBottom) {
      result.start.x = left + width / 2;
      result.start.y = top + height;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2 + height2;
    }

    return result;
  }

}
