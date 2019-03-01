package net.sevenscales.editor.diagram.utils;

import net.sevenscales.editor.gfx.domain.MatrixPointJS;
import net.sevenscales.editor.gfx.domain.Point;

public class GridUtils {
  public static final int GRID_SIZE = 1;
  /**
	* Treshold to start dragging only if these amount of pixels have been passed since
	* since starting point. More usable with Wacom Bamboo Tablet Pen. Also prevents
	* mistakes...
	*/
	private static final int TRESHOLD = 20;

  private Point prevMovePoint = new Point();
	private int dragStartX = 0;
	private int dragStartY = 0;
	
	private int sumX = 0;
	private int sumY = 0;
	private int gridSize = GRID_SIZE;
	private double scaleFactor;
	/**
	* If value is passed once during this mouse event chaing (down, move)
	* do not start to restrict again. Annoying stopping if trying to
	* align elements.
	*/
	private boolean passed;
	
	public GridUtils() {
	}

	public GridUtils(int gridSize) {
		this.gridSize = gridSize;
	}
	
  public void init(int x, int y, double scaleFactor) {
  	this.scaleFactor = scaleFactor;
    prevMovePoint.x = x;
    prevMovePoint.y = y;
    
    dragStartX = x;
    dragStartY = y;
    
    sumX = 0;
    sumY = 0;
    passed = false;
  }

  public boolean passTreshold(MatrixPointJS point) {
    return passTreshold(point, TRESHOLD);
  }
  
  public boolean passTreshold(MatrixPointJS point, int treshold) {
  	if (passed) {
  		return true;
  	}

    if (Math.abs(dx(point.getScreenX())) >= treshold 
      || Math.abs(dy(point.getScreenY())) >= treshold) {
			passed = true;
			return true;
		}
		return false;
	}

  public int diffX(int value, int from) {
	  int result = diff(value, from);	  
	  sumX = sumX + result;
	  int mx = diff(value, dragStartX);
	
	  // adjust value if transformation has started from the drag start
	  // otherwise pixels are "lost"
	  result = mx - sumX;	  
	  if (mx - sumX!= 0)
		  sumX = sumX + mx - sumX;
	  
//	  System.out.println("sumX: " + sumX + "mx: " + mx);
	  return result;
  }

  public int diffY(int value, int from) {
	  int result = diff(value, from);	  
	  sumY = sumY + result;
	  int my = diff(value, dragStartY);
	
	  // adjust value if transformation has started from the drag start
	  // otherwise pixels are "lost"
	  result = my - sumY;
	  if (my - sumY != 0)
		  sumY = sumY + my - sumY;
	  
//	  System.out.println("sumX: " + sumX + "mx: " + mx);
	  return result;
  }

  public int diff(int value, int from) {
//	Debug.print("value:"+value+"from:"+from);
    int diff = value - from;
    diff = diff - diff % gridSize;
//	Debug.print("diff:"+diff+"gridSize:"+gridSize);
    return diff;
  }
  
  public Point getPrevMovePoint() {
	return prevMovePoint;
  }

  public static int align(int value) {
    return value - value % GRID_SIZE;
  }

	public int dx(int x) {
		return x - dragStartX;
	}

	public int dy(int y) {
		return y - dragStartY;
	}

}
