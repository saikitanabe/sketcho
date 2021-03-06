package net.sevenscales.editor.uicomponents;

import java.util.List;

import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.Point;
import net.sevenscales.editor.diagram.drag.AnchorElement;
import net.sevenscales.editor.diagram.drag.Anchor;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.gfx.domain.Color;
import net.sevenscales.editor.gfx.domain.IRectangle;
import net.sevenscales.editor.gfx.domain.IShapeFactory;
import net.sevenscales.editor.api.ISurfaceHandler;

public class AnchorUtils {
  private static final SLogger logger = SLogger.createLogger(AnchorUtils.class);
  static {
    logger.addFilter(AnchorUtils.class);
  }

	private static final Color HIGHLIGHT_COLOR = new Color(0x6A, 0xCA, 0x00, 0.5);
	private static final Color COLOR_TRANSPARENT = new Color(0x6A, 0xCA, 0x00, 0);
	private static IRectangle anchorPoint;

  public static class AnchorProperties {
    public int x;
    public int y;
    public double relativeValueX;
    public double relativeValueY;
    public CardinalDirection cardinalDirection = CardinalDirection.NORTH;
  }
  
  private static final int MAGNETIC_VALUE = 15;
  private static final int MAGNETIC_AUTO_VALUE = 0;
  public static final int ATTACH_EXTRA_DISTANCE = 2;

	private static void Init(ISurfaceHandler surface) {
		if (AnchorUtils.anchorPoint == null) {
			AnchorUtils.anchorPoint = IShapeFactory.Util.factory(true).createRectangle(surface.getInteractionLayer());
			AnchorUtils.anchorPoint.setStrokeWidth(2);
			AnchorUtils.anchorPoint.setVisibility(false);
		}
  }
  
  public static void hide() {
    if (AnchorUtils.anchorPoint != null) {
      AnchorUtils.anchorPoint.setVisibility(false);
    }
  }

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
  
	public static boolean onAttachAreaManual(int x, int y, int left, int top, int width, int height, ISurfaceHandler surface) {

    // small shapes have smaller inner magnet
    int distance = calcDistance(width, height);

    boolean result = AnchorUtils.onAttachArea(
      x,
      y,
      left,
      top,
      width,
      height,
      distance,
      MAGNETIC_VALUE
    );

    AnchorUtils.Init(surface);

    if (result) {
			// net.sevenscales.domain.utils.Debug.log("", left + ", ", top + ", ", width + ", ", height + "");
      
      // AnchorUtils.anchorPoint.setShape(left, top, width, height, 4);
      // AnchorUtils.anchorPoint.setStrokeWidth(distance);
      // AnchorUtils.anchorPoint.setStroke(HIGHLIGHT_COLOR);
      // AnchorUtils.anchorPoint.setFill(COLOR_TRANSPARENT);
      // AnchorUtils.anchorPoint.setVisibility(true);

      // 7.5.2018 ST: decided to hide helper rect on manual connections
      // abstract diagram item highlights the edge.
      AnchorUtils.anchorPoint.setVisibility(false);
    }

    return result;
  }

	public static boolean onAttachAreaAuto(int x, int y, int left, int top, int width, int height, ISurfaceHandler surface) {

    int distance = calcDistance(width, height);
    int dd = distance * 2;

    int l = left + distance;
    int t = top + distance;
    int w = width - dd;
    int h = height - dd;

    boolean result = AnchorUtils.pointOnArea(
      x,
      y,
      l,
      t,
      w,
      h
    );

    // net.sevenscales.domain.utils.Debug.log("point on area: " + result, "", "x:" + x + ",", "y:" + y + ",", l + ", ", t + ", ", w + ", ", h + "");

    AnchorUtils.Init(surface);

    if (result) {
      AnchorUtils.anchorPoint.setShape(l, t, w, h, 4);
      AnchorUtils.anchorPoint.setVisibility(true);
      AnchorUtils.anchorPoint.setStrokeWidth(1);
      AnchorUtils.anchorPoint.setStroke(HIGHLIGHT_COLOR);
      AnchorUtils.anchorPoint.setFill(HIGHLIGHT_COLOR);
    }

    return result;
  }

  private static int calcDistance(int width, int height) {
    int min = Math.min(width, height);
    int distance = 15;
    if (min < 50) {
      distance = min / 4;
    }

    return distance;
  }

	private static boolean onAttachArea(int x, int y, int left, int top, int width, int height, int distance, int magneticValue) {
		
		// top line
    if ( (x >= (left - magneticValue) && x <= (left + width + magneticValue)) &&
    		 (y >= (top - magneticValue) && y <= (top + distance))) {
    	return true;
    }
    		
    // right line
    if ((x <= (left + width + magneticValue) && x >= (left + width - distance)) &&
        (y >= (top - magneticValue) && y <= (top + height + magneticValue))) {
      return true;
    }
    
		// bottom line
    if ( (x >= (left - magneticValue) && x <= (left + width + magneticValue)) &&
    		 (y <= (top + height + magneticValue) && y >= (top + height - distance))) {
    	return true;
    }
    
    // left line
    if ((x >= (left - magneticValue) && x <= (left + distance)) &&
        (y >= (top - magneticValue) && y <= (top + height + magneticValue))) {
      return true;
    }

		return false;
  }

	private static boolean pointOnArea(int x, int y, int left, int top, int width, int height) {
		
		// on rect area
    if ( (x >= left && x <= (left + width)) &&
    		 (y >= top && y <= (top + height))) {
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

  public static boolean isRightToLeft(int sx, int sy, int ex, int ey) {
    return sx - ex > 0;
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

  public static int distance(int x, int y, int x1, int y1) {
    int dx = Math.abs(x - x1);
    int dy = Math.abs(y - y1);
    return (int) Math.sqrt(dx * dx + dy * dy);
  }

  public static Point centerEndPoint(int x, int y, int left, int top, int width, int height) {
    Point result = new Point(x, y);
    // which edge
    int leftdist = Math.abs(x - left);
    int rightdist = Math.abs(x - (left + width));
    int topdist = Math.abs(y - top);
    int bottomdist = Math.abs(y - (top + height));

    int smallest = Math.min(leftdist, rightdist);
    smallest = Math.min(smallest, topdist);
    smallest = Math.min(smallest, bottomdist);

    if (smallest == leftdist) {
      result.y = top + height / 2;
    } else if (smallest == rightdist) {
      result.y = top + height / 2;
    } else if (smallest == topdist) {
      result.x = left + width / 2;
    } else if (smallest == bottomdist) {
      result.x = left + width / 2;
    }
    return result;
  }

  /**
  * Finds closest segment and compares to start and end end points.
  */
  public static boolean isClosestPathBetweenDiagrams(Anchor startAnchor, Anchor endAnchor, int[] points) {
    boolean result = false;
    Diagram start = null;
    Diagram end = null;

    // get 
    if (startAnchor != null && endAnchor != null) {
      start = startAnchor.getDiagram();
      end = endAnchor.getDiagram();
    }

    if (start != null && end != null) {
      ClosestSegment cs = AnchorUtils.closestSegment(
        start.getLeft(),
        start.getTop(),
        start.getWidth(),
        start.getHeight(),
        end.getLeft(),
        end.getTop(),
        end.getWidth(),
        end.getHeight());

        int startx = points[0];
        int starty = points[1];
        int endx = points[points.length - 2];
        int endy = points[points.length - 1];

      if (cs.start.x == startx && cs.start.y == starty &&
          cs.end.x == endx && cs.end.y == endy) {
        return true;
      }
    }

    return false;
  }

  public static ClosestSegment closestSegment(int left, int top, int width, int height, int left2, int top2, int width2, int height2) {
    ClosestSegment result = new ClosestSegment();

    int distLeftLeft =      distance(left, top + height / 2, left2, top2 + height2 / 2);
    int distLeftRigth =     distance(left, top + height / 2, left2 + width2, top2 + height2 / 2);
    int distLeftTop =       distance(left, top + height / 2, left2 + width2 / 2, top2);
    int distLeftBottom =    distance(left, top + height / 2, left2 + width2 / 2, top2 + height2);

    int distRightLeft =     distance(left + width, top + height / 2, left2, top2 + height2 / 2);
    int distRightRight =    distance(left + width, top + height / 2, left2 + width2, top2 + height2 / 2);
    int distRightTop =      distance(left + width, top + height / 2, left2 + width2 / 2, top2);
    int distRightBottom =   distance(left + width, top + height / 2, left2 + width2 / 2, top2 + height2);

    int distTopTop =        distance(left + width / 2, top, left2 + width2 / 2, top2);
    int distTopBottom =     distance(left + width / 2, top, left2 + width2 / 2, top2 + height2);
    int distTopLeft =       distance(left + width / 2, top, left2, top2 + height2 / 2);
    int distTopRight =      distance(left + width / 2, top, left2 + width2, top2 + height2 / 2);

    int distBottomTop =     distance(left + width / 2, top + height, left2 + width2 / 2, top2);
    int distBottomBottom =  distance(left + width / 2, top + height, left2 + width2 / 2, top2 + height2);
    int distBottomLeft =    distance(left + width / 2, top + height, left2, top2 + height2 / 2);
    int distBottomRight =   distance(left + width / 2, top + height, left2 + width2, top2 + height2 / 2);

    int smallest = Math.min(distLeftLeft, distLeftRigth);
    smallest = Math.min(smallest, distLeftTop);
    smallest = Math.min(smallest, distLeftBottom);

    smallest = Math.min(smallest, distRightLeft);
    smallest = Math.min(smallest, distRightRight);
    smallest = Math.min(smallest, distRightTop);
    smallest = Math.min(smallest, distRightBottom);

    // horizontal edges
    smallest = Math.min(smallest, distTopTop);
    smallest = Math.min(smallest, distTopBottom);
    smallest = Math.min(smallest, distTopLeft);
    smallest = Math.min(smallest, distTopRight);

    smallest = Math.min(smallest, distBottomTop);
    smallest = Math.min(smallest, distBottomBottom);
    smallest = Math.min(smallest, distBottomLeft);
    smallest = Math.min(smallest, distBottomRight);

    if (smallest == distLeftLeft) {
      result.start.x = left;
      result.start.y = top + height / 2;
      result.end.x = left2;
      result.end.y = top2 + height2 / 2;
    } else if (smallest == distLeftRigth) {
      result.start.x = left;
      result.start.y = top + height / 2;
      result.end.x = left2 + width2;
      result.end.y = top2 + height2 / 2;
    } else if (smallest == distLeftTop) {
      result.start.x = left;
      result.start.y = top + height / 2;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2;
    } else if (smallest == distLeftBottom) {
      result.start.x = left;
      result.start.y = top + height / 2;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2 + height2;
    } else if (smallest == distRightLeft) {
      result.start.x = left + width;
      result.start.y = top + height / 2;
      result.end.x = left2;
      result.end.y = top2 + height2 / 2;
    } else if (smallest == distRightRight) {
      result.start.x = left + width;
      result.start.y = top + height / 2;
      result.end.x = left2 + width2;
      result.end.y = top2 + height2 / 2;
    } else if (smallest == distRightTop) {
      result.start.x = left + width;
      result.start.y = top + height / 2;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2;
    } else if (smallest == distRightBottom) {
      result.start.x = left + width;
      result.start.y = top + height / 2;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2 + height2;
    } else if (smallest == distTopTop) {
      result.start.x = left + width / 2;
      result.start.y = top;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2;
    } else if (smallest == distTopBottom) {
      result.start.x = left + width / 2;
      result.start.y = top;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2 + height2;
    } else if (smallest == distTopLeft) {
      result.start.x = left + width / 2;
      result.start.y = top;
      result.end.x = left2;
      result.end.y = top2 + height2 / 2;
    } else if (smallest == distTopRight) {
      result.start.x = left + width / 2;
      result.start.y = top;
      result.end.x = left2 + width2;
      result.end.y = top2 + height2 / 2;
    } else if (smallest == distBottomTop) {
      result.start.x = left + width / 2;
      result.start.y = top + height;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2;
    } else if (smallest == distBottomBottom) {
      result.start.x = left + width / 2;
      result.start.y = top + height;
      result.end.x = left2 + width2 / 2;
      result.end.y = top2 + height2;
    } else if (smallest == distBottomLeft) {
      result.start.x = left + width / 2;
      result.start.y = top + height;
      result.end.x = left2;
      result.end.y = top2 + height2 / 2;
    } else if (smallest == distBottomRight) {
      result.start.x = left + width / 2;
      result.start.y = top + height;
      result.end.x = left2 + width2;
      result.end.y = top2 + height2 / 2;
    }

    extraDistance(result.start, left, top, width, height);
    extraDistance(result.end, left2, top2, width2, height2);

    return result;
  }

  private static void extraDistance(Point point, int left, int top, int width, int height) {
    CardinalDirection cd = findCardinalDirection(point.x, point.y, left, top, width, height);
    switch (cd) {
      case WEST:
        point.x -= ATTACH_EXTRA_DISTANCE;
      break;
      case NORTH:
        point.y -= ATTACH_EXTRA_DISTANCE;
      break;
      case EAST:
        point.x += ATTACH_EXTRA_DISTANCE;
      break;
      case SOUTH:
        point.y += ATTACH_EXTRA_DISTANCE;
      break;
    }
  }

}
