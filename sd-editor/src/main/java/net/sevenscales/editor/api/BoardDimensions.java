package net.sevenscales.editor.api;

import java.util.List;

import net.sevenscales.editor.diagram.Diagram;

public class BoardDimensions {
	private int leftmost = Integer.MAX_VALUE;
	private int topmost = Integer.MAX_VALUE;
	private int rightmost = Integer.MIN_VALUE;
	private int bottommost = Integer.MIN_VALUE;
	
	private static BoardDimensions instance = null;
	
	private BoardDimensions() {
	}
	
	public static BoardDimensions instance() {
		if (instance == null) {
			instance = new BoardDimensions();
		}
		return instance;
	}

	public static void resolveDimensions(List<Diagram> diagrams) {
		for (Diagram diagram : diagrams) {
			instance()._resolveDimensions(diagram);
		}
	}

	public static void resolveDimensions(Diagram diagram) {
		instance()._resolveDimensions(diagram);
	}

	private void _resolveDimensions(Diagram diagram) {
  	int left = diagram.getLeft();
  	if (left < leftmost) {
  		leftmost = left;
  	}

  	int top = diagram.getTop();
  	if (top < topmost) {
  		topmost = top;
  	}

  	int right = left + diagram.getWidth();
  	if (right > rightmost) {
  		rightmost = right;
  	}

  	int bottom = top + diagram.getHeight();
  	if (bottom > bottommost) {
  		bottommost = bottom;
  	}
  }
	
	public static int getLeftmost() {
		return instance().leftmost;
	}

	public static int getTopmost() {
		return instance().topmost;
	}

	public static int getRightmost() {
		return instance().rightmost;
	}

	public static int getBottommost() {
		return instance().bottommost;
	}
	
	public static int getWidth() {
		int right = BoardDimensions.getRightmost();
		int left = BoardDimensions.getLeftmost();
		return right - left;
	}
	
	public static int getHeight() {
		int top = BoardDimensions.getTopmost();
		int bottom = BoardDimensions.getBottommost(); 
		return bottom - top;
	}

}
