package net.sevenscales.editor.api;

import java.util.List;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.uicomponents.CircleElement;

public class BoardDimensions {
	private int leftmost = Integer.MAX_VALUE;
	private int topmost = Integer.MAX_VALUE;
	private int rightmost = Integer.MIN_VALUE;
	private int bottommost = Integer.MIN_VALUE;
	
	private static BoardDimensions instance = null;
	
	private BoardDimensions() {
	}

	private void reset() {
		leftmost = Integer.MAX_VALUE;
		topmost = Integer.MAX_VALUE;
		rightmost = Integer.MIN_VALUE;
		bottommost = Integer.MIN_VALUE;
	}
	
	public static BoardDimensions instance() {
		if (instance == null) {
			instance = new BoardDimensions();
		}
		return instance;
	}

	public static void resolveDimensions(List<Diagram> diagrams) {
		instance().reset();
		for (Diagram diagram : diagrams) {
			instance()._resolveDimensions(diagram);
		}
	}

	public static void resolveDimensions(Diagram diagram) {
		instance().reset();
		instance()._resolveDimensions(diagram);
	}

	private void _resolveDimensions(Diagram diagram) {
		if (diagram instanceof CircleElement) {
			// this is not a real shape
			return;
		}

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
