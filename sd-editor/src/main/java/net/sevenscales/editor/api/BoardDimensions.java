package net.sevenscales.editor.api;

import java.util.List;
import java.util.ArrayList;

import com.google.gwt.core.client.JsArrayString;

import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.shape.Info;
import net.sevenscales.editor.diagram.DiagramSearch;
import net.sevenscales.editor.uicomponents.CircleElement;
import net.sevenscales.domain.IDiagramItemRO;
import net.sevenscales.editor.content.utils.DuplicateHelpers;
import net.sevenscales.editor.content.utils.ShapeParser;

public class BoardDimensions {
	private int leftmost = Integer.MAX_VALUE;
	private int topmost = Integer.MAX_VALUE;
	private int rightmost = Integer.MIN_VALUE;
	private int bottommost = Integer.MIN_VALUE;
	
	private static BoardDimensions instance = null;

	public static class DiagramsDimension {
		public int x;
		public int y;
		public int right;
		public int bottom;

		public DiagramsDimension(int x, int y, int right, int bottom) {
			this.x = x;
			this.y = y;
			this.right = right;
			this.bottom = bottom;
		}
	}
	
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

	public static DiagramsDimension snapshot() {
		return new DiagramsDimension(
			getLeftmost(),
			getTopmost(),
			getRightmost(),
			getBottommost()
		);
	}

	public static void resolveDimensions(JsArrayString clientIds, ISurfaceHandler surface) {
		instance().reset();

		DiagramSearch search = surface.createDiagramSearch();

		List<Diagram> diagrams = new ArrayList<Diagram>();
		for (int i = 0; i < clientIds.length(); ++i) {
			Diagram added = search.findByClientId(clientIds.get(i));
			if (added != null) {
				diagrams.add(added);
			}
		}

		for (Diagram diagram : diagrams) {
			instance()._resolveDimensions(diagram);
		}
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

	public static int itemsWidth(List<? extends IDiagramItemRO> items) {
		int left = Integer.MAX_VALUE;
		int top = Integer.MAX_VALUE;
		int right = Integer.MIN_VALUE;

		for (IDiagramItemRO di : items) {
			if (DuplicateHelpers.allowedToPasteType(di)) {
				Info shape = ShapeParser.parse(di, 0, 0);
				// Info shape = di.parseShape();
				int l = shape.getLeft();
				int t = shape.getTop();
				int r = l + shape.getWidth();

				left = l < left ? l : left;
				top = t < top ? t : top;
				right = r > right ? r : right;
			}
		}

		return right - left;
	}

	public static int itemsHeight(List<? extends IDiagramItemRO> items) {
		int left = Integer.MAX_VALUE;
		int top = Integer.MAX_VALUE;
		int bottom = Integer.MIN_VALUE;

		for (IDiagramItemRO di : items) {
			if (DuplicateHelpers.allowedToPasteType(di)) {
				Info shape = ShapeParser.parse(di, 0, 0);
				// Info shape = di.parseShape();
				int l = shape.getLeft();
				int t = shape.getTop();
				int b = t + shape.getHeight();

				left = l < left ? l : left;
				top = t < top ? t : top;
				bottom = b > bottom ? b : bottom;
			}
		}

		return bottom - top;
	}


}
