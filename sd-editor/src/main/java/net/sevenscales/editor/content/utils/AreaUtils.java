package net.sevenscales.editor.content.utils;

import java.util.List;

public class AreaUtils {

	/**
	 * All points needs to be in selection area.
	 * @param x
	 * @param y
	 * @param bx
	 * @param by
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @return
	 */
	public static boolean onArea(int x, int y, int bx, int by, int left, int top, int right, int bottom) {
//		System.out.println("x: " + x + " y: " + y + " bx: " + bx + " by: " + by + " left: " + left + 
//				" top: " + top + " right: " + right + " bottom:" + bottom);
		if (x > left && x < right && bx > left && bx < right &&
				y > top && y < bottom && by > top && by < bottom) {
			return true;
		}
		
  	return false;
	}

	/**
	 * Consecutive numbers are x,y pairs. All points needs to be inside selection area.
	 * @param points
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @return
	 */
	public static boolean onArea(List<Integer> points, int left, int top,
			int right, int bottom) {
		// assert(points.size() % 2 == 0); // parillinen maara
		
		for (int i = 0; i < points.size(); i += 2) {
			int x = points.get(i);
			int y = points.get(i + 1);
			if ( !(x > left && x < right && y > top && y < bottom) ) {
				return false;
			}
		}
		
		return true;
	}

}
