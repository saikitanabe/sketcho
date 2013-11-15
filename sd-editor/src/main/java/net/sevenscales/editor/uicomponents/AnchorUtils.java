package net.sevenscales.editor.uicomponents;

import java.util.List;

import net.sevenscales.editor.gfx.domain.IRectangle;

public class AnchorUtils {
  public static class AnchorProperties {
    public int x;
    public int y;
    public double relativeValueX;
    public double relativeValueY;
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
    int dx = Math.abs(x - left);
    int dy = Math.abs(y - top);
    int dxw = Math.abs(x - (left + width));
    int dyh = Math.abs(y - (top + height));
    
    int smallest = Math.min(dx, dy);
    smallest = Math.min(smallest, dxw); 
    smallest = Math.min(smallest, dyh); 
    int tempax = 0;
    int tempay = 0;
    if (smallest == dx) {
      tempax = left - ATTACH_EXTRA_DISTANCE;
      tempay = y;
    } else if (smallest == dy) {
      tempax = x;
      tempay = top - ATTACH_EXTRA_DISTANCE;
    } else if (smallest == dxw) {
      tempax = left + width + ATTACH_EXTRA_DISTANCE;
      tempay = y;
    } else if (smallest == dyh) {
      tempax = x;
      tempay = top + height + ATTACH_EXTRA_DISTANCE;
    }
    
//    tempax = GridUtils.align(tempax);
//    tempay = GridUtils.align(tempay);
    ap.x = tempax;
    ap.y = tempay;
    relativeValue(ap, left, top, width, height);
  }

  /**
  * Rounds to use only one decimal, e.g. 0.492 => 0.5.
  * In this way center positions are not rounded little bit of all the time.
  */
  public static double round(double value) {
    return Math.round(value * 10) / 10.0;
  }
  
  public static void relativeValue(AnchorProperties ap, int elementLeft, int elementTop, int elementWidth, int elementHeight) {
    ap.relativeValueX = round(Math.abs((ap.x - elementLeft) / (double) elementWidth));
    ap.relativeValueY = round(Math.abs((ap.y - elementTop) / (double) elementHeight));
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
  
  public static void anchorPoint(int x, int y, AnchorProperties ap, List<Integer> points) {
  	Integer[] array = new Integer[points.size()];
  	int xindex = findClosestPoint(x, y, points.toArray(array));
    
    ap.x = points.get(xindex);
    ap.y = points.get(xindex + 1);
//    ap.relativeValueX = Math.abs((points.get(xindex)-left)/(double)width);
//    ap.relativeValueY = Math.abs((tempay-top)/(double)height);
  }
  
  public static void anchorPoint(int left, int top, AnchorProperties ap, Integer[] points) {
  	int xindex = findClosestPoint(left, top, points);
    if (xindex + 1 < points.length) {
      ap.x = points[xindex];
      ap.y = points[xindex + 1];
      
      // just set fixed index and if fixed points are set then use directly the index
      ap.relativeValueX = xindex;
      ap.relativeValueY = xindex + 1;
    }
  }

	public static void setRelativePosition(AnchorElement ae, int left, int top,
			int width, int height) {
		int extraDistanceX = 0;
		int extraDistanceY = 0;
		if (ae.getAx() == left - AnchorUtils.ATTACH_EXTRA_DISTANCE) {
			extraDistanceX = -AnchorUtils.ATTACH_EXTRA_DISTANCE;
		} else if (ae.getAx() == left + width + AnchorUtils.ATTACH_EXTRA_DISTANCE) {
			extraDistanceX = AnchorUtils.ATTACH_EXTRA_DISTANCE;
		}
		
		if (ae.getAy() == top - ATTACH_EXTRA_DISTANCE) {
			extraDistanceY = -ATTACH_EXTRA_DISTANCE;
		} else if (ae.getAy() == top + height + ATTACH_EXTRA_DISTANCE) {
			extraDistanceY = ATTACH_EXTRA_DISTANCE;
		}
		
		// if extra distance has not been changed then this is a relative movement and do the movement
		if (extraDistanceX == 0) { 
			ae.setAx(((int)(width * ae.getRelativeFactorX())) + left);
		}
		if (extraDistanceY == 0) {
			ae.setAy(((int)(height * ae.getRelativeFactorY())) + top);
		}
	}

}
