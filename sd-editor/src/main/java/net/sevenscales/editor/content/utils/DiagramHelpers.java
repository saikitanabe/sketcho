package net.sevenscales.editor.content.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collections;

import net.sevenscales.domain.utils.SLogger;
import net.sevenscales.editor.diagram.Diagram;
import net.sevenscales.editor.diagram.utils.DiagramList;
import net.sevenscales.editor.diagram.utils.DiagramDisplayOrderList;
import net.sevenscales.editor.gfx.domain.IPath;
import net.sevenscales.editor.gfx.domain.IPath.PathTransformer;
import net.sevenscales.editor.uicomponents.uml.NoteElement;
import net.sevenscales.editor.utils.ElementTypeComparator;
import net.sevenscales.editor.api.ActionType;


public class DiagramHelpers {
	private static final SLogger logger = SLogger.createLogger(DiagramHelpers.class);
	
	public static class ComplexElementHandler {
		private boolean hideComplexElements;
		private boolean complexElementsHidden;
		
		public void reset() {
	    hideComplexElements = true;
	    complexElementsHidden = false;
		}
		
		public void showComplexElements(Iterable<Diagram> diagrams) {
			if (complexElementsHidden) {
				for (Diagram d : diagrams) {
					if (d instanceof NoteElement) {
						((NoteElement) d).hideProxy();
					}
				}
			}
			complexElementsHidden = false;
		}
		
		public void hideComplexElements(List<Diagram> diagrams) {
			if (hideComplexElements) {
				for (Diagram d : diagrams) {
					hideComplexElement(d);
				}
			}
		}

		public void hideComplexElement(Diagram diagram) {
			if (diagram instanceof NoteElement) {
				((NoteElement) diagram).showProxy();
			}
			hideComplexElements = false;
			complexElementsHidden = true;
		}
	}
	
	public static Diagram[] filterOwnerDiagrams(Diagram[] diagrams) {
		Set<Diagram> filtered = new HashSet<Diagram>();
		for (Diagram d : diagrams) {
			d = d.getOwnerComponent();
			filtered.add(d);
		}
		Diagram[] result = new Diagram[1];
		return filtered.toArray(result);
	}

	public static List<Diagram> filterOwnerDiagramsAsListOrderByType(Iterable<? extends Diagram> diagrams, ActionType actionType) {
		List<Diagram> result = new DiagramList();
		for (Diagram d : diagrams) {
			d = d.getOwnerComponent(actionType);
			// will not add duplicate items, checks if index already exists with a client id
			result.add(d);
		}
		Collections.sort(result, new ElementTypeComparator.DiagramComparator());
		return result;
	}

	public static List<Diagram> filterOwnerDiagramsAsListKeepOrder(Iterable<Diagram> diagrams, ActionType actionType) {
		List<Diagram> result = new ArrayList();
		for (Diagram d : diagrams) {
			d = d.getOwnerComponent(actionType);
			// do not add duplicate items, check contains element with client id
			if (!result.contains(d)) {
				result.add(d);
			}
		}
		return result;
	}

	public static List<Diagram> diagramsInDisplayOrder(Set<Diagram> diagrams) {
		List<Diagram> result = new DiagramDisplayOrderList();
		for (Diagram d : diagrams) {
			result.add(d);
		}
		return result;
	}
	
	public static Diagram[] filterOwnerDiagrams(Set<Diagram> diagrams) {
		Diagram[] result = new Diagram[1];
		return filterOwnerDiagrams(diagrams.toArray(result));
	}

	public static Diagram[] filterOwnerDiagrams(List<Diagram> diagrams) {
		Diagram[] result = new Diagram[1];
		return filterOwnerDiagrams(diagrams.toArray(result));
	}
	
	public static int getLeftCoordinate(List<Integer> points) {
		return getLeftCoordinate(IntegerHelpers.toIntArray(points));
	}
	public static int getLeftCoordinate(int[] points) {
		int result = Integer.MAX_VALUE;
	  for (int i = 0; i < points.length; i +=2) {
			result = Math.min(result, points[i]);
		}
		return result;
	}
	
	public static int getTopCoordinate(List<Integer> points) {
		return getTopCoordinate(IntegerHelpers.toIntArray(points));
	}
	public static int getTopCoordinate(int[] points) {
  	int result = Integer.MAX_VALUE;
    for (int i = 1; i < points.length; i +=2) {
  		result = Math.min(result, points[i]);
  	}
  	return result;
	}
	
	public static int getWidth(List<Integer> points) {
		return getWidth(IntegerHelpers.toIntArray(points));
	}
	public static int getWidth(int[] points) {
		int result = Integer.MIN_VALUE;
		int left = Integer.MAX_VALUE;
	  for (int i = 0; i < points.length; i +=2) {
			result = Math.max(result, points[i]);
			left = Math.min(left, points[i]);
		}
		return result - left;
	}

	public static int getHeight(List<Integer> points) {
		return getHeight(IntegerHelpers.toIntArray(points));
	}
	public static int getHeight(int[] points) {
  	int result = Integer.MIN_VALUE;
  	int top = Integer.MAX_VALUE;

  	for (int i = 1; i < points.length; i +=2) {
  		result = Math.max(result, points[i]);
  		top = Math.min(top, points[i]);
  	}
  	return result - top;
	}
	
	// helper for svg path translate
	// M379,187 Q379,216 390,233 409,239 428,233 436,212 431,177 406,157 393,159 391,170 388,185 382,186 380,187
	// public static String applyTransformToShape(String shape, int dx, int dy, IPath.PathTransformer transformer) {
	// 	logger.debug("applyTransformToShape {}...", shape);
	// 	String result = doApplyTransformToShape(shape, dx, dy, transformer);
	// 	logger.debug("applyTransformToShape {}... DONE", result);
	// 	return result;
	// }
	
	// private static String doApplyTransformToShape(String shape, int dx, int dy, PathTransformer transformer) {
	// 	if (transformer != null) {
	// 		 return transformer.applyTransformToShape(shape, dx, dy);
	// 	}

	// 	if (String.valueOf(shape.charAt(shape.length() - 1)).matches("\\D")) {
	// 		// drop last z if any
	// 		shape = shape.substring(0, shape.length() - 1);
	// 	}
		
	// 	String[] list = shape.split("\\s");
	// 	shape = "";
	// 	for (String li : list) {
	// 		String[] xy = li.split(",");
	// 		String prefix = "";
	// 		if (xy[0].matches("[a-zA-Z][-]*\\d+")) {
	// 			prefix = xy[0].substring(0, 1);
	// 			xy[0] = xy[0].substring(1);
	// 		}
	// 		String newval = prefix + (Integer.valueOf(xy[0]) + dx) + "," + (Integer.valueOf(xy[1]) + dy);
	// 		shape += newval + " ";
	// 	}
	// 	return shape.trim();
	// }

	public static int[] map(int[] points, int dx, int dy) {
		int[] result = new int[points.length];
		for (int i = 0; i < points.length; i += 2) {
			result[i] = points[i] + dx;
			result[i + 1] = points[i + 1] + dy;
		}
		return result;
	}

	public static void fill4FixedRectPoints(List<Integer> fixed, int left,
			int top, int width, int height) {
		// left point
		fixed.add(left);
		fixed.add(top + height / 2);
		
		// top point
		fixed.add(left + width / 2);
		fixed.add(top);

		// right point
		fixed.add(left + width);
		fixed.add(top + height / 2);
		
		fixed.add(left + width / 2);
		fixed.add(top + height);
	}
	

}
